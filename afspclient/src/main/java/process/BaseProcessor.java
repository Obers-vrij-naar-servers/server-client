package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseProcessor {
    protected final static Logger LOGGER = LoggerFactory.getLogger(BaseProcessor.class);
    protected AfspRequest request;
    protected AfspResponse response;

    public boolean done = false;

    public BaseProcessor(AfspRequest request, AfspResponse response) {
        this.request = request;
        this.response = response;
    }
    public abstract void process() throws Exception;
}
