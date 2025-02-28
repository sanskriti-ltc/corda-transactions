package com.r3.developers.samples.obligation.workflows;

import com.r3.developers.samples.obligation.contracts.IOUContract;
import com.r3.developers.samples.obligation.states.IOUState;
import net.corda.v5.application.flows.ClientRequestBody;
import net.corda.v5.application.flows.ClientStartableFlow;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.FlowEngine;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import net.corda.v5.membership.NotaryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class IOUIssueFlow implements ClientStartableFlow {
    private final static Logger log = LoggerFactory.getLogger(IOUIssueFlow.class);

    // Injects the JsonMarshallingService to read and populate JSON parameters.
    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    // Injects the MemberLookup to look up the VNode identities.
    @CordaInject
    public MemberLookup memberLookup;

    // Injects the UtxoLedgerService to enable the flow to make use of the Ledger API.
    @CordaInject
    public UtxoLedgerService ledgerService;

    // Injects the NotaryLookup to look up the notary identity.
    @CordaInject
    public NotaryLookup notaryLookup;

    // FlowEngine service is required to run SubFlows.
    @CordaInject
    public FlowEngine flowEngine;

    @Override
    @Suspendable
    public String call(ClientRequestBody requestBody) {
        log.info("IOUIssueFlow.call() called");

        try {
            // Obtain the deserialized input arguments to the flow from the requestBody.
            IOUIssueFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, IOUIssueFlowArgs.class);

            // Get MemberInfos for the Vnode running the flow and the other members.
            MemberInfo myInfo = memberLookup.myInfo();
            MemberInfo draweeInfo = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getDrawee())),
                    "MemberLookup can't find drawee specified in flow arguments."
            );
            MemberInfo drawerInfo = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getDrawer())),
                    "MemberLookup can't find drawer specified in flow arguments."
            );
            MemberInfo payeeInfo = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getPayee())),
                    "MemberLookup can't find payee specified in flow arguments."
            );
            log.info("PASS 0");
            // Create the IOUState from the input arguments and member information.
            IOUState iou = new IOUState(
                    flowArgs.getId(),
                    Integer.parseInt(flowArgs.getAmount()),
                    flowArgs.getCurrency(),
                    draweeInfo.getName(),
                    drawerInfo.getName(),
                    payeeInfo.getName(),
                    LocalDate.parse(flowArgs.getIssueDate()),
                    LocalDate.parse(flowArgs.getDueDate()),
                    flowArgs.getAcceptance(),
                    flowArgs.getAvailisation(),
                    flowArgs.getEndorsements(),
                    flowArgs.getBoeDocs(),
                    flowArgs.getTermsAndConditions(),
                    flowArgs.getIso2022Message(),
                    Arrays.asList(myInfo.getLedgerKeys().get(0), draweeInfo.getLedgerKeys().get(0), drawerInfo.getLedgerKeys().get(0), payeeInfo.getLedgerKeys().get(0))
            );
            log.info("PASS 1");
            // Obtain the Notary name and public key.
            NotaryInfo notary = requireNonNull(
                    notaryLookup.lookup(MemberX500Name.parse("CN=NotaryService, OU=Test Dept, O=R3, L=London, C=GB")),
                    "NotaryLookup can't find notary specified in flow arguments."
            );

            log.info("PASS 2");
            PublicKey notaryKey = notary.getPublicKey();
            for(MemberInfo memberInfo: memberLookup.lookup()){
                if(!memberInfo.getLedgerKeys().isEmpty()) {
                    if (Objects.equals(
                            memberInfo.getMemberProvidedContext().get("corda.notary.service.name"),
                            notary.getName().toString())) {
                        notaryKey = memberInfo.getLedgerKeys().get(0);
                        break;
                    }
                }
            }
            log.info("PASS 3");
            // Note, in Java CorDapps only unchecked RuntimeExceptions can be thrown not
            // declared checked exceptions as this changes the method signature and breaks override.
            if(notaryKey == null) {
                throw new CordaRuntimeException("No notary PublicKey found");

            }
            log.info("PASS 4");
            // Use UTXOTransactionBuilder to build up the draft transaction.
            UtxoTransactionBuilder txBuilder = ledgerService.createTransactionBuilder()
                    .setNotary(notary.getName())
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addOutputState(iou)
                    .addCommand(new IOUContract.Issue())
                    .addSignatories(iou.getParticipants());
            log.info("PASS 5");
            // Convert the transaction builder to a UTXOSignedTransaction and sign with this Vnode's first Ledger key.
            // Note, toSignedTransaction() is currently a placeholder method, hence being marked as deprecated.
            @SuppressWarnings("DEPRECATION")
            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction();

            // Call FinalizeIOUSubFlow which will finalise the transaction.
            // If successful the flow will return a String of the created transaction id,
            // if not successful it will return an error message.
            return flowEngine.subFlow(new FinalizeIOUFlow.FinalizeIOU(signedTransaction, Arrays.asList(draweeInfo.getName(), drawerInfo.getName())));
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
    "clientRequestId": "createiou-1",
    "flowClassName": "com.r3.developers.samples.obligation.workflows.IOUIssueFlow",
    "requestBody": {
        "id": "some-unique-id",
        "amount":"1000",
        "currency": "INR",
        "drawee":"CN=LBG, O=Lloyds Banking Group, L=London, C=GB",
        "drawer":"CN=ABC imports, O=ABC imports, L=Delhi, C=IN",
        "payee":"CN=Global Exports, O=Global Exports, L=London, C=GB",
        "issueDate": "2025-02-20",
        "dueDate": "2024-09-30",
        "acceptance": "Accepted by LBG on 2025-02-21",
        "availisation": "Guaranteed by ICICI Bank",
        "endorsements": [],
        "boeDocs": "automated",
        "termsAndConditions": "Payment due on demand or by the specified due date. Interest rate of 5% per annum if unpaid by due date",
        "iso2022Message": "<Document><PmtInf><InstrId>BEX123456</InstrId><EndToEndId>E2E123456</EndToEndId><InstdAmt Ccy=\"INR\">1000</InstdAmt></PmtInf></Document>"
        }
}
*/

