package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.nio.channels.SocketChannel;

public class PostProcessor extends BaseProcessor {

    public PostProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        super(request, response);
    }
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    @Override
    public ProcessResult process() throws AfspProcessingException {
        return new ProcessResult(null);
    }
}
