package network.entity;

import common.constants.ActionTypes;

public class RegistrationResponse {
    
    private final ActionTypes.Code code;
    private final String error;

    public RegistrationResponse(final ActionTypes.Code code, final String error) {
        this.code = code;
        this.error = error;
    }

    public ActionTypes.Code getCode() {
        return code;
    }

    public String getError() {
        return error;
    }
}
