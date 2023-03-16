package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import core.AfspConnectionWorkerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RequestProcessor{

    protected final static Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);
    protected AfspRequest request;
    protected AfspResponse response;
}
