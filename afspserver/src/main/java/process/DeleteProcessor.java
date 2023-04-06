package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspBackupFileHandler;

import java.nio.channels.SocketChannel;

public class DeleteProcessor extends RequestProcessor{


    public DeleteProcessor(AfspRequest request, AfspResponse response, SocketChannel channel) {
        super(request, response, channel);
    }

    @Override
    public void process() throws AfspProcessingException {
        AfspBackupFileHandler fileHandler = new AfspBackupFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
        fileHandler.deleteFile(request.getTarget());
    }
}
