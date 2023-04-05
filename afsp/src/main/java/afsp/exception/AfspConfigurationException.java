package afsp.exception;

import afsp.AfspStatusCode;

public class AfspConfigurationException extends AfspException{

    public AfspConfigurationException(AfspStatusCode errorCode) {
        super(errorCode);
    }
}
