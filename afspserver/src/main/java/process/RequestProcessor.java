package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;
import core.AfspConnectionWorkerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RequestProcessor{

    protected final static Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);
    protected AfspRequest request;
    protected AfspResponse response;

    public RequestProcessor(AfspRequest request, AfspResponse response) {
        this.request = request;
        this.response = response;
    }
    public abstract void process() throws AfspProcessingException;
}
