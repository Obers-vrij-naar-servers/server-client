package afsp.exception;

import afsp.AfspStatusCode;

public class AfspException extends Exception{
    private AfspStatusCode errorCode;

    public AfspException(AfspStatusCode errorCode) {
        super(errorCode.MESSAGE);
        this.errorCode = errorCode;
    }
    public AfspStatusCode getErrorCode(){
        return errorCode;
    }
}
