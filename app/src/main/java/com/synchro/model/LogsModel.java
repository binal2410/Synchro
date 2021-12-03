package com.synchro.model;

import java.io.Serializable;

public class LogsModel implements Serializable {
    public long logTime;
    public String logMsg;

    public LogsModel(String logMsg, long logTime) {
        this.logMsg = logMsg;
        this.logTime = logTime;
    }
}
