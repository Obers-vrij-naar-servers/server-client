package afsp;

import java.util.List;

public class AfspRequest extends AfspMessage {

    private AfspMethod method;
    private String requestTarget;
    private String protocol;

    private List<AfspHeader> headerList;

    AfspRequest(){

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

    void setMethod(String  methodName) throws AfspParsingException {
        for(AfspMethod _method: AfspMethod.values()){
            if (methodName.equals(_method.name())){
                this.method = _method;
                return;
            }
        }
        throw new AfspParsingException(
                AfspStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
        );
    }

    public void setRequestTarget(String requestTarget) throws AfspParsingException {
        if(requestTarget == null || requestTarget.length() == 0){
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.requestTarget = requestTarget;
    }
    public String getTarget(){
        if (this.requestTarget != null){
            return this.requestTarget;
        }
        return "/";
    }

}
