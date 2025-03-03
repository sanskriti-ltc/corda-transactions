package com.r3.developers.samples.obligation.workflows;

import com.r3.developers.samples.obligation.contracts.IOUContract;
import com.r3.developers.samples.obligation.states.IOUState;
import net.corda.v5.application.flows.SchedulableFlow;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.application.messaging.FlowMessaging;
import net.corda.v5.base.annotations.CordaSchedule;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@SchedulableFlow
@CordaSchedule
public class IOUScheduledSettleFlow implements ClientStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(IOUScheduledSettleFlow.class);

    @CordaInject
    public MemberLookup memberLookup;

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public FlowMessaging flowMessaging;

    @Override
    @Suspendable
    public String call() {
        log.info("IOUScheduledSettleFlow.call() called");

        try {
            // Query the IOU input state by linear ID
            List<StateAndRef<IOUState>> iouStateAndRefs = ledgerService.findUnconsumedStatesByExactType(IOUState.class, 100, Instant.now()).getResults();
            List<StateAndRef<IOUState>> iouStateAndRefsWithDue = iouStateAndRefs.stream()
                    .filter(sar -> sar.getState().getContractState().getDueDate().toInstant().isBefore(Instant.now()))
                    .collect(toList());

            if (iouStateAndRefsWithDue.isEmpty()) {
                throw new CordaRuntimeException("No IOU states found with due date reached.");
            }

            StateAndRef<IOUState> iouStateAndRef = iouStateAndRefsWithDue.get(0);
            IOUState iouInput = iouStateAndRef.getState().getContractState();

            MemberInfo myInfo = memberLookup.myInfo();
            if (!(myInfo.getName().equals(iouInput.getPayee()))) {
                throw new CordaRuntimeException("Only IOU payee can settle the IOU.");
            }

            MemberInfo drawerInfo = requireNonNull(
                    memberLookup.lookup(iouInput.getDrawer()),
                    "MemberLookup can't find drawer specified in IOU state."
            );

            // Check if the IOU is accepted and avalised
            if (!iouInput.getAcceptance()) {
                throw new CordaRuntimeException("IOU must be accepted before settlement.");
            }
            if (!iouInput.getAvailisation()) {
                throw new CordaRuntimeException("IOU must be avalised before settlement.");
            }

            // Create the IOUState from the input arguments and member information.
            IOUState iouOutput = iouInput.pay(iouInput.getAmount());

            // Get notary from input
            MemberX500Name notary = iouStateAndRef.getState().getNotaryName();

            // Use UTXOTransactionBuilder to build up the draft transaction.
            UtxoTransactionBuilder txBuilder = ledgerService.createTransactionBuilder()
                    .setNotary(notary)
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addInputState(iouStateAndRef.getRef())
                    .addOutputState(iouOutput)
                    .addCommand(new IOUContract.Settle())
                    .addSignatories(iouOutput.getParticipants());

            // Convert the transaction builder to a UTXOSignedTransaction and sign with this VNode's first Ledger key.
            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction();

            // Call FinalizeIOUSubFlow which will finalise the transaction.
            // If successful the flow will return a String of the created transaction ID,
            // if not successful it will return an error message.
            return flowEngine.subFlow(new FinalizeIOUFlow.FinalizeIOU(signedTransaction, Arrays.asList(drawerInfo.getName())));
        } catch (Exception e) {
            log.warn("Failed to process IOUScheduledSettleFlow because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }
}
