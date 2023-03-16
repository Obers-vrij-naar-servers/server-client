package afsp.exception;

import afsp.AfspStatusCode;

public class AfspResponseException extends AfspException {
    private AfspStatusCode errorCode;

    public AfspResponseException(AfspStatusCode errorCode) {
        super(errorCode);
    }
}
