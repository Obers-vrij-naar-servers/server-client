package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class GetProcessor extends RequestProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final SocketChannel channel;

    public GetProcessor(AfspRequest request, AfspResponse response, SocketChannel _channel) {
        super(request, response);
        this.channel = _channel;
        LOGGER.info("** GET_PROCESSOR STARTED **");
    }
    public void process() throws AfspProcessingException{
        //validate headers
        if (!request.containsHeaders(AfspHeader.HeaderType.BUFFER_SIZE,AfspHeader.HeaderType.CHARSET,AfspHeader.HeaderType.TIME_OUT)){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
        String target = request.getTarget();
        try{
            fileHandler.sendFile(target, channel,8192);
        }catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
    }
}
