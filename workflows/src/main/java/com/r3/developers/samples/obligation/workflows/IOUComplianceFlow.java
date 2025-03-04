package com.r3.developers.samples.obligation.workflows;

import com.r3.developers.samples.obligation.states.IOUState;
import com.r3.developers.samples.obligation.contracts.IOUContract;
import net.corda.v5.application.crypto.DigestService;
import net.corda.v5.application.flows.*;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.application.messaging.FlowSession;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.crypto.SecureHash;
import net.corda.v5.ledger.utxo.StateRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.membership.MemberInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.security.PublicKey;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@InitiatingFlow(protocol = "utxo-transaction-transmission-protocol")
public class IOUComplianceFlow implements ClientStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(IOUComplianceFlow.class);

    // Injects the JsonMarshallingService to read and populate JSON parameters.
    @CordaInject
    private JsonMarshallingService jsonMarshallingService;

    // Injects the MemberLookup to look up the VNode identities.
    @CordaInject
    private MemberLookup memberLookup;

    @CordaInject
    private DigestService digestService;

    // Injects the UtxoLedgerService to enable the flow to make use of the Ledger API.
    @CordaInject
    public UtxoLedgerService ledgerService;

    // FlowEngine service is required to run SubFlows.
    @CordaInject
    public FlowEngine flowEngine;

    @Override
    @Suspendable
    public String call(ClientRequestBody requestBody) {
        log.info("IOUComplianceFlow.call() called");

        try {
            // Obtain the deserialized input arguments to the flow from the requestBody.
            IOUComplianceFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, IOUComplianceFlowArgs.class);

            // Get flow args from the input JSON
            UUID iouID;
            try {
                iouID = UUID.fromString(flowArgs.getIouID());
            } catch (IllegalArgumentException e) {
                throw new CordaRuntimeException("Invalid IOU ID format: " + flowArgs.getIouID(), e);
            }

            // Query the IOU input
            List<StateAndRef<IOUState>> iouStateAndRefs = ledgerService.findUnconsumedStatesByExactType(IOUState.class, 100, Instant.now()).getResults();
            List<StateAndRef<IOUState>> iouStateAndRefsWithId = iouStateAndRefs.stream()
                    .filter(sar -> sar.getState().getContractState().getLinearId().equals(iouID))
                    .collect(toList());

            if (iouStateAndRefsWithId.size() != 1) 
                throw new CordaRuntimeException("Multiple or zero IOU states with id " + iouID + " found");
            StateAndRef<IOUState> iouStateAndRef = iouStateAndRefsWithId.get(0);
            IOUState iouInput = iouStateAndRef.getState().getContractState();

            // Get notary from input
            MemberX500Name notary = iouStateAndRef.getState().getNotaryName();

            // Ensure that the token is accepted
            if(!iouInput.getAcceptance())
            {
                throw new CordaRuntimeException("Only accepted token can be send for compliance");
            }

            MemberInfo INRegulatorInfo = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse("CN=RBI Bank, OU=Banking Dept, O=Reserve Bank of India, L=India, C=IN")),
                    "MemberLookup can't find INRegulator specified in flow arguments."
            );
            MemberInfo GBRegulatorInfo = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse("CN=BOE, OU=Banking Dept, O=Bank of England, L=London, C=GB")),
                    "MemberLookup can't find GBRegulator specified in flow arguments."
            );

            List<PublicKey> newParticipants = Arrays.asList(INRegulatorInfo.getLedgerKeys().get(0), GBRegulatorInfo.getLedgerKeys().get(0));

            // Create the IOUState from the input arguments and member information.
            IOUState iouOutput = iouInput.avalise(true).addParticipants(newParticipants);

            // Use UTXOTransactionBuilder to build up the draft transaction.
            UtxoTransactionBuilder txBuilder = ledgerService.createTransactionBuilder()
                    .setNotary(notary)
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addInputState(iouStateAndRef.getRef())
                    .addOutputState(iouOutput)
                    .addCommand(new IOUContract.Compliance())
                    .addSignatories(iouOutput.getParticipants());

            // Convert the transaction builder to a UTXOSignedTransaction and sign with this VNode's first Ledger key.
            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction();

            // Call FinalizeIOU subFlow which will finalize the transaction.
            List<MemberX500Name> otherMembers = Arrays.asList(INRegulatorInfo.getName(), GBRegulatorInfo.getName());
            return flowEngine.subFlow(new FinalizeIOUFlow.FinalizeIOU(signedTransaction, otherMembers));
        }
        // Catch any exceptions, log them and rethrow the exception.
        catch (Exception e) {
            log.warn("Failed to process utxo flow for request body " + requestBody + " because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }
}

/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "complianceiou-1",
    "flowClassName": "com.r3.developers.samples.obligation.workflows.IOUComplianceFlow",
    "requestBody": {
        "iouID": "f5314873-6935-4481-ab24-c0b31c79f58a",
    }
}
*/
