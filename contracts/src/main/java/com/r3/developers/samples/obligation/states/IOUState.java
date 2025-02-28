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

//Link with the Contract class
@BelongsToContract(IOUContract.class)
public class IOUState implements ContractState {

    // Private variables
    private final String id;
    private final int amount;
    private final String currency;
    private final MemberX500Name drawee;
    private final MemberX500Name drawer;
    private final MemberX500Name payee;
    private final ZonedDateTime issueDate;
    private final ZonedDateTime dueDate;
    private final String acceptance;
    private final String availisation;
    private final List<String> endorsements;
    private final String boeDocs;
    private final String termsAndConditions;
    private final String iso2022Message;
    private final UUID linearId;
    private final List<PublicKey> participants;

    @ConstructorForDeserialization
    public IOUState(String id, int amount, String currency, MemberX500Name drawee, MemberX500Name drawer, MemberX500Name payee, ZonedDateTime issueDate, ZonedDateTime dueDate, String acceptance, String availisation, List<String> endorsements, String boeDocs, String termsAndConditions, String iso2022Message, UUID linearId, List<PublicKey> participants) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.drawee = drawee;
        this.drawer = drawer;
        this.payee = payee;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.acceptance = acceptance;
        this.availisation = availisation;
        this.endorsements = endorsements;
        this.boeDocs = boeDocs;
        this.termsAndConditions = termsAndConditions;
        this.iso2022Message = iso2022Message;
        this.linearId = linearId;
        this.participants = participants;
    }

    public IOUState(String id, int amount, String currency, MemberX500Name drawee, MemberX500Name drawer, MemberX500Name payee, LocalDate issueDate, LocalDate dueDate, String acceptance, String availisation, List<String> endorsements, String boeDocs, String termsAndConditions, String iso2022Message, List<PublicKey> participants) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.drawee = drawee;
        this.drawer = drawer;
        this.payee = payee;
        this.issueDate = convertToUTC(issueDate);
        this.dueDate = convertToUTC(dueDate);
        this.acceptance = acceptance;
        this.availisation = availisation;
        this.endorsements = endorsements;
        this.boeDocs = boeDocs;
        this.termsAndConditions = termsAndConditions;
        this.iso2022Message = iso2022Message;
        this.linearId = UUID.randomUUID();
        this.participants = participants;
    }

    public String getId() {
        return id;
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

    public String getAcceptance() {
        return acceptance;
    }

    public String getAvailisation() {
        return availisation;
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
        return new IOUState(id, newAmountPaid, currency, drawee, drawer, payee, issueDate, dueDate, acceptance, availisation, endorsements, boeDocs, termsAndConditions, iso2022Message, linearId, participants);
    }

    // Helper method for transfer flow
    public IOUState withNewDrawee(MemberX500Name newDrawee, List<PublicKey> newParticipants) {
        return new IOUState(id, amount, currency, newDrawee, drawer, payee, issueDate, dueDate, acceptance, availisation, endorsements, boeDocs, termsAndConditions, iso2022Message, linearId, newParticipants);
    }

    // Helper method to convert LocalDate to ZonedDateTime in UTC
    private ZonedDateTime convertToUTC(LocalDate date) {
        return date.atStartOfDay(ZoneId.of("UTC"));
    }
}
