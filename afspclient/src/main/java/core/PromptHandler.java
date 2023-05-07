package core;

import afsp.AfspMethod;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import config.Configuration;
import factory.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.*;
import util.Helper;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PromptHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(PromptHandler.class);
    private final Configuration conf;
    private final RequestFactory requestFactory = new RequestFactory();
    private BaseProcessor processor;

    public PromptHandler(Configuration conf) {
        this.conf = conf;
    }

    public void handle(PromptResponse promptResponse) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            InetSocketAddress address = new InetSocketAddress(conf.getHost(), conf.getPort());
            socketChannel.connect(address);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            AfspRequest request = requestFactory.createRequest(promptResponse);
            LOGGER.info("Request: " + request.toString());

            buffer.put(request.toString().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            AfspResponse response = new AfspResponse();
            LOGGER.debug("REQUEST METHOD:"+request.getMethod());

            if (request.getMethod() == AfspMethod.LIST) {
                processor = new ListProcessor(socketChannel, request, response);
            }

            if (request.getMethod() == AfspMethod.GET) {
                processor = new GetProcessor(socketChannel, request, response);
            }

            if (request.getMethod() == AfspMethod.POST) {
                processor = new PostProcessor(socketChannel, request, response);
            }

            if (request.getMethod() == AfspMethod.DELETE) {
                processor = new DeleteProcessor(socketChannel, request, response);
            }

            try {
                processor.process();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("\n An error occurred: \n"+e.getMessage());
                LOGGER.debug(response.toString());
            } finally {
                Helper.closeChanelConnections(socketChannel);
            }

        } catch (IOException e) {
            LOGGER.error("Error while connecting to server");
            e.printStackTrace();
        } catch (AfspParsingException e) {
            LOGGER.error("Error while parsing response");

            e.printStackTrace();
        } catch (AfspProcessingException e) {
            LOGGER.error("Error while processing request");
            e.printStackTrace();
        }
    }
}
