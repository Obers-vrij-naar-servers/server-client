package afsp;

public class AfspResponseException extends Exception {
    private AfspStatusCode errorCode;

    public AfspResponseException(AfspStatusCode errorCode){
        super(errorCode.MESSAGE);
        this.errorCode = errorCode;
    }
    public AfspStatusCode getErrorCode(){
        return errorCode;
    }
}
