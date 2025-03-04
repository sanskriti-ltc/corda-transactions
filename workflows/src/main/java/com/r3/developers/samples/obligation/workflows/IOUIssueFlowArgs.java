package com.r3.developers.samples.obligation.workflows;

// A class to hold the deserialized arguments required to start the flow.
public class IOUIssueFlowArgs {
    private String amount;
    private String currency;
    private String payee;
    private String drawee;
    private String issueDate;
    private String dueDate;
    private String endorsements;
    private String termsAndConditions;

    public IOUIssueFlowArgs() {
    }

    public IOUIssueFlowArgs(String amount, String currency, String payee, String drawee, String issueDate, String dueDate, String endorsements, String termsAndConditions) {
        this.amount = amount;
        this.currency = currency;
        this.payee = payee;
        this.drawee = drawee;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.endorsements = endorsements;
        this.termsAndConditions = termsAndConditions;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getDrawee() {
        return drawee;
    }

    public void setDrawee(String drawer) {
        this.drawee = drawer;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getEndorsements() {
        return endorsements;
    }

    public void setEndorsements(String endorsements) {
        this.endorsements = endorsements;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
