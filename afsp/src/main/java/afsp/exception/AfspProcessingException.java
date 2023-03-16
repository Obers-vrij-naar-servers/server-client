package afsp.exception;

import afsp.AfspStatusCode;

public class AfspProcessingException extends AfspException{
    public AfspProcessingException(AfspStatusCode errorCode) {
        super(errorCode);
    }
}
