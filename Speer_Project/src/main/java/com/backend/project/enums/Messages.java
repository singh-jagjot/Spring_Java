package com.backend.project.enums;

public enum Messages {
    SUCCESS("SUCCESS"), USR_EXIST("USER ALREADY EXIST"), SERVER_ERR("SERVER ERROR"),
    NO_USR_FND("NO USER FOUND"), TOKEN("TOKEN"), INVLD_PWD("INVALID PASSWORD"),
    TKN_VALD("TOKEN VALID"), TKN_EXPIRED("TOKEN EXPIRED"), TKN_INVALD("TOKEN INVALID"),
    ACCES_DND("ACCESS DENIED"), NT_ID_DNTEXST("NOTE ID DON'T EXIST"), FAILED("FAILED"),
    START("START"), END("END");
    private final String message;

    Messages(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }
}
