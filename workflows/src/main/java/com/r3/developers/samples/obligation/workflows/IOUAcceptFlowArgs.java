package com.r3.developers.samples.obligation.workflows;

import java.util.UUID;

// A class to hold the deserialized arguments required to start the flow.
public class IOUAcceptFlowArgs {
    private UUID iouID;
    private Boolean payeeAcceptance;

    public IOUAcceptFlowArgs() {
    }

    public IOUAcceptFlowArgs(UUID iouID, Boolean payeeAcceptance) {
        this.iouID = iouID;
        this.payeeAcceptance = payeeAcceptance;
    }

    public UUID getIouID() {
        return iouID;
    }

    public void setIouID(UUID iouID) {
        this.iouID = iouID;
    }

    public Boolean getPayeeAcceptance() {
        return payeeAcceptance;
    }

    public void setPayeeAcceptance(Boolean payeeAcceptance) {
        this.payeeAcceptance = payeeAcceptance;
    }
}
