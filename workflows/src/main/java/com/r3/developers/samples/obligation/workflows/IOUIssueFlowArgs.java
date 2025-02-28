package com.r3.developers.samples.obligation.workflows;

import java.time.LocalDate;
import java.util.List;

// A class to hold the deserialized arguments required to start the flow.
public class IOUIssueFlowArgs {
    private String amount;
    private String currency;
    private String drawee;
    private String drawer;
    private String payee;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private List<String> endorsements;
    private String termsAndConditions;

    public IOUIssueFlowArgs() {
    }

    public IOUIssueFlowArgs(String amount, String currency, String drawee, String drawer, String payee, LocalDate issueDate, LocalDate dueDate, String acceptance, String availisation, List<String> endorsements, String boeDocs, String termsAndConditions, String iso2022Message) {
        this.amount = amount;
        this.currency = currency;
        this.drawee = drawee;
        this.drawer = drawer;
        this.payee = payee;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.endorsements = endorsements;
        this.termsAndConditions = termsAndConditions;
    }

    public String getAmount() {
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

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public List<String> getEndorsements() {
        return endorsements;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }
}
