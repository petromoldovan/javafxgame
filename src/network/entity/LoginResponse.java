package network.entity;

import common.constants.ActionTypes;

public class LoginResponse {
    
    private final ActionTypes.Code code;
    private final String error;
    private final String uid;

    public LoginResponse(final ActionTypes.Code code, final String error, final String uid) {
        this.code = code;
        this.error = error;
        this.uid = uid;
    }

    public ActionTypes.Code getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getUid() {
        return uid;
    }
}
