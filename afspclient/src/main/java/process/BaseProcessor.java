package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;

public abstract class BaseProcessor {
    protected final static Logger LOGGER = LoggerFactory.getLogger(BaseProcessor.class);
    protected AfspRequest request;
    protected AfspResponse response;

    public boolean done = false;
    protected SocketChannel socket;

    public BaseProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        this.socket = socket;
        this.request = request;
        this.response = response;
    }
    public abstract void process() throws Exception;
}
