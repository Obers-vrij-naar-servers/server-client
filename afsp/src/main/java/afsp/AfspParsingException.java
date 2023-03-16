package afsp;

public class AfspParsingException extends Exception{
    private AfspStatusCode errorCode;

    public AfspParsingException(AfspStatusCode errorCode) {
        super(errorCode.MESSAGE);
        this.errorCode = errorCode;
    }
    public AfspStatusCode getErrorCode(){
        return errorCode;
    }
}
