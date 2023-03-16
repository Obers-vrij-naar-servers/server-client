package afsp;

public enum AfspStatusCode {

    SERVER_SUCCESS_200_OK(200,"OK"),
    SERVER_SUCCESS_304_NOT_MODIFIED(304,"Not Modified"),

    CLIENT_ERROR_400_BAD_REQUEST(400,"Bad Request"),
    CLIENT_ERROR_404_NOT_FOUND(404,"Not found"),
    CLIENT_ERROR_405_METHOD_NOT_ALLOWED(405,"Method not allowed"),
    CLIENT_ERROR_414_URI_TOO_LONG(414,"URI Too Long"),
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500,"Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501,"Not Implemented"),
    SERVER_ERROR_505_PROTOCOL_NOT_SUPPORTED(505,"Protocol Not Supported");

    public final int STATUS_CODE;
    public final String MESSAGE;

    AfspStatusCode(int STATUS_CODE, String MESSAGE){
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }
}
