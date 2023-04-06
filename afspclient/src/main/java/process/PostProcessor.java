package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;

import java.nio.channels.SocketChannel;

public class PostProcessor extends BaseProcessor {

    public PostProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        super(request, response);
    }

    @Override
    public void process() throws AfspProcessingException {
    }
}
