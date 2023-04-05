package core;

import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.GetProcessor;
import process.ListProcessor;
import process.RequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class AfspConnectionWorkerThreadChanneled extends Thread{

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspConnectionWorkerThreadChanneled.class);

    private final SocketChannel channel;
    public AfspConnectionWorkerThreadChanneled(SocketChannel channel) throws IOException {
        this.channel = channel;
        LOGGER.debug("CREATED");
    }
    @Override
    public void run(){

        AfspRequestParser parser = new AfspRequestParser();
        AfspRequest request;
        AfspResponse response;
        RequestProcessor processor = null;
        try {
            request = parser.parseAfspRequest(channel);
            LOGGER.info(" ** AFSP request received ** \n" + request);
            response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
            if (request.getMethod() == AfspMethod.LIST) {
                LOGGER.info(" ** PROCESSING LIST **");
                processor = new ListProcessor(request, response);
            }
            if (request.getMethod() == AfspMethod.GET) {
                LOGGER.info(" ** PROCESSING GET **");
                processor = new GetProcessor(request, response, channel);
            }
            if (processor == null){
                throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
            }
            processor.process();
            OutputStream out = Channels.newOutputStream(channel);
            out.write(response.toString().getBytes());

        } catch (AfspParsingException | IOException | AfspProcessingException e) {
            e.printStackTrace();
        }

    }

}
