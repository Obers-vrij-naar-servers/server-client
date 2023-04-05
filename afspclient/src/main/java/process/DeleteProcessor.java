package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.net.Socket;
import java.nio.channels.SocketChannel;

public class DeleteProcessor extends BaseProcessor {

    public DeleteProcessor(SocketChannel socketChannel, AfspRequest request, AfspResponse response) {
        super(request, response);
    }
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    @Override
    public void process() throws AfspProcessingException {

    }
}
