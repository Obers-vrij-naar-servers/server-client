package afsp.exception;

import afsp.AfspStatusCode;

public class AfspParsingException extends AfspException{
    private AfspStatusCode errorCode;

    public AfspParsingException(AfspStatusCode errorCode) {
        super(errorCode);
    }
}
