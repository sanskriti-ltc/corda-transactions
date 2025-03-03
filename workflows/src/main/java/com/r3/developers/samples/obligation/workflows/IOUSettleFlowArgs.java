package com.r3.developers.samples.obligation.workflows;

import java.util.UUID;

// A class to hold the deserialized arguments required to start the flow.
public class IOUSettleFlowArgs {
    private UUID iouID;

    public IOUSettleFlowArgs() {
    }

    public IOUSettleFlowArgs(UUID iouID) {
        this.iouID = iouID;
    }

    public UUID getIouID() {
        return iouID;
    }
}