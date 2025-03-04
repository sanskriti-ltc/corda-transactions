package com.r3.developers.samples.obligation.workflows;

// A class to hold the deserialized arguments required to start the flow.
public class IOUComplianceFlowArgs {
    private String iouID;

    public IOUComplianceFlowArgs() {
    }

    public IOUComplianceFlowArgs(String iouID) {
        this.iouID = iouID;
    }

    public String getIouID() {
        return iouID;
    }

    public void setIouID(String iouID) {
        this.iouID = iouID;
    }
}
