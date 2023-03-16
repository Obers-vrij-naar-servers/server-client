package afsp;

import java.util.List;

public class AfspResponse {

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
        if (this.headerlist != null && !this.headerlist.isEmpty()){
            responseString += printHeaders();
        }
        return responseString;
    }


    private String printHeaders(){
        if (headerlist == null || headerlist.isEmpty()) return "";
        String headerString = "";
        for (AfspHeader _header:headerlist){
            headerString += _header.getHeaderType().toString() + ": " + _header.getHeaderContent() +"\r\n";
        }
        return headerString;
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
