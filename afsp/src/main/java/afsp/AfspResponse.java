package afsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AfspResponse extends AfspMessage{

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspResponse.class);

    private final String protocol = AfspProtocolVersion.AFSP_1_0.toString();
    private int statusCode;
    private String message;
    private List<AfspHeader> headerlist = null;

    private String body = null;

    public AfspResponse(AfspStatusCode status){
        this.statusCode = status.STATUS_CODE;
        this.message = status.MESSAGE;
    }
    public AfspResponse(AfspStatusCode status, List<AfspHeader> headers){
        this(status);
        this.headerlist = headers;
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


    private String printHeaders(){
        if (headerlist == null || headerlist.isEmpty()) {
            LOGGER.debug("HEADERLIST EMPTY");
            return "\r\n";
        }
        String headerString = "";

            for (AfspHeader _header : headerlist) {
                headerString += _header.getHeaderType().toString() + ": " + _header.getHeaderContent() + "\r\n";
            }

        return headerString+"\r\n";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AfspHeader> getHeaderlist() {
        return headerlist;
    }

    public void setHeaderlist(List<AfspHeader> headerlist) {
        this.headerlist = headerlist;
    }

    public String getBody() {
        if(body == null || body.length()==0){
            return "";
        }
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
