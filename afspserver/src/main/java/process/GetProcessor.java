package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.List;

public class GetProcessor extends RequestProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    public GetProcessor(AfspRequest request, AfspResponse response, SocketChannel channel) {
        super(request, response, channel);
        LOGGER.debug("REQUEST:\n" + request);
    }
    public void process() throws AfspProcessingException {

        //validate headers
        if (!request.containsHeaders(AfspHeader.HeaderType.BUFFER_SIZE,AfspHeader.HeaderType.CHARSET,AfspHeader.HeaderType.TIME_OUT)){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        var bufferSizeHeader = request.getHeaderList().stream().filter(h -> h.getHeaderType() == AfspHeader.HeaderType.BUFFER_SIZE).findFirst().map(AfspHeader.class::cast).orElse(null);
        int bufferSize;
        try{
            bufferSize = Integer.parseInt(bufferSizeHeader.getHeaderContent());
        } catch (NullPointerException e){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String target = request.getTarget();

        var out = Channels.newOutputStream(channel);
        AfspResponse response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        List<AfspHeader> headerList = fileHandler.getFileInfo(target);
        response.setHeaderList(headerList);
        LOGGER.info(response.toString());
        try {
            out.write(response.toString().getBytes());
        } catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        try{
            fileHandler.sendFile(target, bufferSize, channel);
        }catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
    }
}
