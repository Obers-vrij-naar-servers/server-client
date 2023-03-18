package afsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AfspResponse extends AfspMessage{

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspResponse.class);

    private final String protocol = AfspProtocolVersion.AFSP_1_0.toString();
    private int statusCode;
    private String message;

    private String body = null;

    public AfspResponse(AfspStatusCode status){
        this.statusCode = status.STATUS_CODE;
        this.message = status.MESSAGE;
    }
    public AfspResponse(AfspStatusCode status, List<AfspHeader> headers){
        this(status);
        this.headerList = headers;
    }
    public AfspResponse(){

    }

    @Override
    public String toString() {
        String responseString;
        responseString =  protocol + " " +
                statusCode + " " +
                message + "\r\n";

        responseString += printHeaders();
        if (this.body !=null){
            responseString += this.body;
        }
        return responseString;
    }




    public int getStatusCode() {
        return statusCode;
    }

    public AfspResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public AfspResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<AfspHeader> getheaderList() {
        return headerList;
    }

    public AfspResponse setheaderList(List<AfspHeader> headerList) {
        this.headerList = headerList;
        return this;
    }

    public String getBody() {
        if(body == null || body.length()==0){
            return "";
        }
        return body;
    }

    public AfspResponse setBody(String body) {
        this.body = body;
        return this;
    }
}
