package com.logisticcomfort.model;

public enum COMMANDS {
    START("/start"),
    LOG_IN_ACCOUNT("/log_in_account"),

    MAIN_PAGE("/main_page"),

    WAREHOUSES("WAREHOUSES"),
    APPLY_PRODUCT("APPLY PRODUCT"),
    PERSONAL("PERSONAL"),
    ADD_PRODUCT("ADD PRODUCT"),
    SIGN_OUT("SIGN OUT"),

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
