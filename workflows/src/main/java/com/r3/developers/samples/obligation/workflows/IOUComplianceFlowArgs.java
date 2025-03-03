package com.r3.developers.samples.obligation.workflows;

// A class to hold the deserialized arguments required to start the flow.
public class IOUComplianceFlowArgs {
    private String iouID;
    private String drawerBank;

    public IOUComplianceFlowArgs() {
    }

    public IOUComplianceFlowArgs(String iouID, String drawerBank) {
        this.iouID = iouID;
        this.drawerBank = drawerBank;
    }

    public String getIouID() {
        return iouID;
    }

    public void setIouID(String iouID) {
        this.iouID = iouID;
    }

    public String getDrawerBank() {
        return drawerBank;
    }

    public void setDrawerBank(String drawerBank) {
        this.drawerBank = drawerBank;
    }
}
