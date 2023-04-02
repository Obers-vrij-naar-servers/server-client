package core;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspResponseException;
import config.Configuration;
import factory.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.GetProcessor;
import util.Helper;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PromptHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(PromptHandler.class);
    private final Configuration conf;
    private final RequestFactory requestFactory = new RequestFactory();


    public PromptHandler(Configuration conf) {
        this.conf = conf;
    }

    public void handle(PromptResponse promptResponse) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            InetSocketAddress address = new InetSocketAddress(conf.getHost(), conf.getPort());
            socketChannel.connect(address);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            AfspResponseParser parser = new AfspResponseParser();
            AfspRequest request = requestFactory.createRequest(promptResponse);
            LOGGER.info("Request: " + request.toString());
            buffer.put(request.toString().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            AfspResponse response = parser.parseResponse(socketChannel);

            GetProcessor processor = new GetProcessor(socketChannel.socket(), request, response);

            try {
                processor.process();
                System.out.println("response " + response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                Helper.closeChanelConnections(socketChannel);
            }
        } catch (IOException | AfspParsingException | AfspResponseException e) {
            throw new RuntimeException(e);
        }
    }

}
