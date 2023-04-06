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

        if (response.getStatusCode() != 200) {
            System.out.println("Error occurred while deleting file, you might need to run 1. Show all files first");
        } else {
            System.out.println("\u001B[32m\033[1mSUCCESS!! the file is deleted" + request.getTarget() + "\u001B[0m");

        }
    }
}
