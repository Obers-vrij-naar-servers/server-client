package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.nio.channels.SocketChannel;

public class DeleteProcessor extends BaseProcessor {
    private final SocketChannel socketChannel;
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    public DeleteProcessor(SocketChannel socketChannel, AfspRequest request, AfspResponse response) {
        super(request, response);
        this.socketChannel = socketChannel;
    }

    @Override
    public void process() throws Exception {
        if (socketChannel == null) {
            throw new Exception("Socket channel is null");
        }

    }
}
