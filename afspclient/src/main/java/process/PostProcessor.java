package process;

import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class PostProcessor extends  BaseProcessor {

    private SocketChannel socket;
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    public PostProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        super(request, response);
        this.socket = socket;
    }

    @Override
    public void process() throws AfspProcessingException {
        //request should contain all required headers
        if(!request.containsHeaders(AfspHeader.HeaderType.CHARSET, AfspHeader.HeaderType.BUFFER_SIZE, AfspHeader.HeaderType.CONTENT_LENGTH, AfspHeader.HeaderType.TIME_OUT)){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
        //set requested bufferSize
        var bufferSizeHeader = request.getHeaderList().stream().filter(h -> h.getHeaderType() == AfspHeader.HeaderType.BUFFER_SIZE).findFirst().map(AfspHeader.class::cast).orElse(null);
        int bufferSize = 8192;
        try{
            bufferSize = Integer.parseInt(bufferSizeHeader.getHeaderContent());
        } catch (NullPointerException e){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        //create outputChannel
        var out = Channels.newOutputStream(socket);
        //send request and file
        try{
            System.out.println(request.toString());
            out.write(request.toString().getBytes());

            fileHandler.sendFile(request.getTarget(),bufferSize,socket);
        } catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        var responseParser = new AfspResponseParser();
        try {
            response = responseParser.parseResponse(socket);
        } catch (AfspParsingException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        } catch (AfspResponseException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        LOGGER.info(response.toString());
    }
}
