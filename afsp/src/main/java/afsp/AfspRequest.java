package afsp;

import afsp.exception.AfspParsingException;
import afsp.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AfspRequest extends AfspMessage {

    private AfspMethod method;
    private String requestTarget;
    private String protocol = AfspProtocolVersion.AFSP_1_0.toString();

    public AfspRequest(){

    };

    public AfspMethod getMethod() {
        return method;
    }

    String getRequestTarget() {
        return requestTarget;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) throws AfspParsingException {
        if (protocol == null || protocol.length() ==0){
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.protocol = protocol;
    }

    public AfspRequest setMethod(String  methodName) throws AfspParsingException {
        for(AfspMethod _method: AfspMethod.values()){
            if (methodName.equals(_method.name())){
                this.method = _method;
                return this;
            }
        }
        throw new AfspParsingException(
                AfspStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
        );
    }
    public AfspRequest setMethod(AfspMethod method){
        this.method = method;
        return this;
    }

    public AfspRequest setRequestTarget(String requestTarget) throws AfspParsingException {
        if(requestTarget == null || requestTarget.length() == 0){
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.requestTarget = Utils.decodeString(requestTarget);
        return this;
    }
    public String getTarget(){
        if (this.requestTarget != null){
            return this.requestTarget;
        }
        return "/";
    }

    public boolean containsHeaders(AfspHeader.HeaderType... headerTypes){
        List<Boolean> conformationList = new ArrayList();
        if (this.headerList == null || this.headerList.isEmpty()) {
            LOGGER.debug(" ** HEADERLIST NULL / EMPTY **");
            return false;
        }
        for (AfspHeader.HeaderType headerType : headerTypes){

            for (AfspHeader header: this.headerList){
                if (header.getHeaderType().equals(headerType)) {
                    conformationList.add(true);
                    break;
                }
            }
            conformationList.add(true);
        }
        return !conformationList.contains(false);
    }

    @Override
    public String toString() {
        String requestString;
        requestString = method.toString() + " " +
                Utils.encodeString(requestTarget) + " " +
                protocol + "\r\n";
        requestString += printHeaders();

        return requestString;
    }

}
