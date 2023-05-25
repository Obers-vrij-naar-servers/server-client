package process;

import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import config.Configuration;
import config.ConfigurationManager;
import core.PromptResponse;
import factory.RequestFactory;
import util.AfspFileHandler;
import util.Helper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static afsp.AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR;

public class PostProcessor extends BaseProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final RequestFactory requestFactory = new RequestFactory();
    private SocketChannel socketChannel;

    public PostProcessor(PromptResponse promptResponse, Configuration conf) {
        super(promptResponse, conf);
    }

    @Override
    public void process() throws AfspProcessingException {

        try (SocketChannel socketChannel = SocketChannel.open()) {
            LOGGER.debug("PROCESSING POST");

            InetSocketAddress address = new InetSocketAddress(conf.getHost(), conf.getPort());
            socketChannel.connect(address);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            AfspRequest request = requestFactory.createRequest(promptResponse);

            buffer.put(request.toString().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            AfspResponse response = new AfspResponse();

            //request should contain all required headers
            if (!request.containsHeaders(AfspHeader.HeaderType.CHARSET, AfspHeader.HeaderType.BUFFER_SIZE, AfspHeader.HeaderType.CONTENT_LENGTH, AfspHeader.HeaderType.TIME_OUT)) {
                throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
            //set requested bufferSize
            var bufferSizeHeader = request.getHeaderList().stream().filter(h -> h.getHeaderType() == AfspHeader.HeaderType.BUFFER_SIZE).findFirst().map(AfspHeader.class::cast).orElse(null);
            int bufferSize = 8192;
            try {
                bufferSize = Integer.parseInt(bufferSizeHeader.getHeaderContent());
            } catch (NullPointerException e) {
                throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOGGER.info("Could not sleep");
            }
            //send request and file
            try {
                LOGGER.debug("Sending file");
                fileHandler.sendFile(request.getTarget(), bufferSize, socketChannel);
            } catch (IOException e) {
                throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
            }
            var responseParser = new AfspResponseParser();
            try {
                response = responseParser.parseResponse(socketChannel);
            } catch (AfspParsingException e) {
                throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
            } catch (AfspResponseException e) {
                throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
            }
            LOGGER.info(response.toString());
        }  catch (Exception e) {
            throw new AfspProcessingException(SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        } finally {
            Helper.closeChanelConnections(socketChannel);
        }
    }
}
