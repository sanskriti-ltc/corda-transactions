package com.r3.developers.samples.obligation.states;

import com.r3.developers.samples.obligation.contracts.IOUContract;
import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;

import java.security.PublicKey;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@BelongsToContract(IOUContract.class)
public class IOUState implements ContractState {

    // Private variables
    private final int amount;
    private final String currency;
    private final MemberX500Name drawee;
    private final MemberX500Name drawer;
    private final MemberX500Name payee;
    private final ZonedDateTime issueDate;
    private final ZonedDateTime dueDate;
    private final Boolean acceptance;
    private final Boolean availisation;
    private final Boolean paid;
    private final List<String> endorsements;
    private final String boeDocs;
    private final String termsAndConditions;
    private final String iso2022Message;
    private final UUID linearId;
    private final List<PublicKey> participants;

    @ConstructorForDeserialization
    public IOUState(int amount, String currency, MemberX500Name drawee, MemberX500Name drawer, MemberX500Name payee, ZonedDateTime issueDate, ZonedDateTime dueDate, Boolean acceptance, Boolean availisation, Boolean paid, List<String> endorsements, String boeDocs, String termsAndConditions, String iso2022Message, UUID linearId, List<PublicKey> participants) {
        this.amount = amount;
        this.currency = currency;
        this.drawee = drawee;
        this.drawer = drawer;
        this.payee = payee;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.acceptance = acceptance;
        this.availisation = availisation;
        this.paid = paid;
        this.endorsements = endorsements;
        this.boeDocs = boeDocs;
        this.termsAndConditions = termsAndConditions;
        this.iso2022Message = iso2022Message;
        this.linearId = linearId;
        this.participants = participants;
    }

    public IOUState(int amount, String currency, MemberX500Name drawee, MemberX500Name drawer, MemberX500Name payee, LocalDate issueDate, LocalDate dueDate, List<String> endorsements, String termsAndConditions, List<PublicKey> participants) {
        this.amount = amount;
        this.currency = currency;
        this.drawee = drawee;
        this.drawer = drawer;
        this.payee = payee;
        this.issueDate = convertToUTC(issueDate);
        this.dueDate = convertToUTC(dueDate);
        this.acceptance = false;
        this.availisation = false;
        this.paid = false;
        this.endorsements = endorsements;
        this.boeDocs = "automatedBoeDocs";
        this.termsAndConditions = termsAndConditions;
        this.iso2022Message = "automatedIso2022Message";
        this.linearId = UUID.randomUUID();
        this.participants = participants;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public MemberX500Name getDrawee() {
        return drawee;
    }

    public MemberX500Name getDrawer() {
        return drawer;
    }

    public MemberX500Name getPayee() {
        return payee;
    }

    public ZonedDateTime getIssueDate() {
        return issueDate;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public Boolean getAcceptance() {
        return acceptance;
    }

    public Boolean getAvailisation() {
        return availisation;
    }

    public Boolean getPaid() {
        return paid;
    }

    public List<String> getEndorsements() {
        return endorsements;
    }

    public String getBoeDocs() {
        return boeDocs;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public String getIso2022Message() {
        return iso2022Message;
    }

    public UUID getLinearId() {
        return linearId;
    }

    @Override
    public List<PublicKey> getParticipants() {
        return participants;
    }

    // Helper method for settle flow
    public IOUState pay(int amountToPay) {
        int newAmountPaid = this.amount - amountToPay;
        return new IOUState(newAmountPaid, currency, drawee, drawer, payee, issueDate, dueDate, acceptance, availisation, paid, endorsements, boeDocs, termsAndConditions, iso2022Message, linearId, participants);
    }

    // Helper method for transfer flow
    public IOUState withNewDrawee(MemberX500Name newDrawee, List<PublicKey> newParticipants) {
        return new IOUState(amount, currency, newDrawee, drawer, payee, issueDate, dueDate, acceptance, availisation, paid, endorsements, boeDocs, termsAndConditions, iso2022Message, linearId, newParticipants);
    }

    // Helper method to convert LocalDate to ZonedDateTime in UTC
    private ZonedDateTime convertToUTC(LocalDate date) {
        return date.atStartOfDay(ZoneId.of("UTC"));
    }
}
