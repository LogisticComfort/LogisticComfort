package com.logisticcomfort.model;

public enum COMMANDS {
    START("/start"),
    LOG_IN_ACCOUNT("/log_in_account"),
    DEMO("/demo"),
    ACCESS("/access"),
    SUCCESS("/success");

    private String command;

    COMMANDS(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
