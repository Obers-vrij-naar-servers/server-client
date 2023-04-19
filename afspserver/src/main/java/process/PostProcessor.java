package process;

import afsp.AfspHeader;
import afsp.AfspHeader.HeaderType;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class PostProcessor extends RequestProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    public PostProcessor(AfspRequest request, AfspResponse response, SocketChannel channel) {
        super(request, response, channel);
    }

    @Override
    public void process() throws AfspProcessingException {
        //CHECK IF ALL HEADERS ARE PRESENT AND FIND RELEVANT VALUES
        ArrayList<HeaderType> shouldHaveHeaderTypes = new ArrayList<>(Arrays.asList(HeaderType.BUFFER_SIZE, HeaderType.CHARSET, HeaderType.CONTENT_LENGTH, HeaderType.TIME_OUT));
        int bufferSize = 0;
        int contentLength = 0;
        LOGGER.info("SEARCHING HEADERS");
        for (HeaderType ht : shouldHaveHeaderTypes) {
            var header = request.getHeader(ht);
            LOGGER.info(header.toString());
            if (header.getHeaderType() == HeaderType.BUFFER_SIZE) {
                bufferSize = Integer.parseInt(header.getHeaderContent());
            }
            if (header.getHeaderType() == HeaderType.CONTENT_LENGTH) {
                contentLength = Integer.parseInt(header.getHeaderContent());
            }
        }
        LOGGER.info("CHECKED HEADERLIST");
        if (bufferSize == 0 || contentLength == 0){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
        LOGGER.info("HEADERS FOUND");
        try{
            fileHandler.receiveFile(channel,
                    contentLength,
                    bufferSize,
                    request.getTarget(),
                    "");
        } catch (IOException | AfspParsingException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
    }
}
