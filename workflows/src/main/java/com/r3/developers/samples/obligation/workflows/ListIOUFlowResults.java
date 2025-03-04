package com.r3.developers.samples.obligation.workflows;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class ListIOUFlowResults {

    private UUID id;
    private int amount;
    private String currency;
    private String drawee;
    private String drawer;
    private String payee;
    private ZonedDateTime issueDate;
    private ZonedDateTime dueDate;
    private String acceptance;
    private String availisation;
    private List<String> endorsements;
    private String boeDocs;
    private String termsAndConditions;
    private String iso2022Message;

    public ListIOUFlowResults() {}

    public ListIOUFlowResults(UUID id, int amount, String currency, String drawee, String drawer, String payee, ZonedDateTime issueDate, ZonedDateTime dueDate, String acceptance, String availisation, List<String> endorsements, String boeDocs, String termsAndConditions, String iso2022Message) {
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
    }

    public UUID getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDrawee() {
        return drawee;
    }

    public String getDrawer() {
        return drawer;
    }

    public String getPayee() {
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
}