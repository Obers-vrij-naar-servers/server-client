package core;

import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.*;

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
                processor = new ListProcessor(request, response, channel);
            }
            if (request.getMethod() == AfspMethod.GET) {
                LOGGER.info(" ** PROCESSING GET **");
                processor = new GetProcessor(request, response, channel);
            }
            if (request.getMethod() == AfspMethod.POST) {
                LOGGER.info(" ** PROCESSING POST **");
                processor = new PostProcessor(request, response, channel);
            }
            if(request.getMethod()==AfspMethod.DELETE){
                LOGGER.info(" ** PROCESSING DELETE **");
                processor = new DeleteProcessor(request,response,channel);
            }
            if (processor == null){
                throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
            }
            processor.process();


        } catch (AfspParsingException | AfspProcessingException e) {
            OutputStream out = Channels.newOutputStream(channel);
            try {
                out.write(new AfspResponse(e.getErrorCode()).toString().getBytes());
            } catch (IOException ex) {
                e.printStackTrace();
            }

        }

    }

}
