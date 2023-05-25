package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;

public abstract class RequestProcessor{

    protected final static Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);
    protected AfspRequest request;
    protected AfspResponse response;

    protected final SocketChannel channel;

    public RequestProcessor(AfspRequest request, AfspResponse response, SocketChannel channel) {
        this.request = request;
        this.response = response;
        this.channel = channel;
    }
    public abstract void process() throws AfspProcessingException;
}
