package core;

import afsp.AfspMethod;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import config.Configuration;
import config.ConfigurationManager;
import factory.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.*;
import util.AfspFileHandler;
import util.Helper;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class PromptHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(PromptHandler.class);
    private final Configuration conf;
    private final RequestFactory requestFactory = new RequestFactory();
    private BaseProcessor processor;
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private ProcessResult result;

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

            // TODO make this a loop
            if (request.getMethod() == AfspMethod.GET) {
                if (result != null && result.getTarget() != null) {
                    List<String> targets = result.getTarget();
                    request.setRequestTarget(targets.get(0));
                } else {
                    System.out.println("Please first retrieve a list of files");
                    return;
                }
            }

            buffer.put(request.toString().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            AfspResponse response = parser.parseResponse(socketChannel);

            if (request.getMethod() == AfspMethod.LIST) {
                processor = new ListProcessor(socketChannel, request, response);

                try {
                    result = processor.process();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    Helper.closeChanelConnections(socketChannel);
                }
            }

            if (request.getMethod() == AfspMethod.GET) {
                processor = new GetProcessor(socketChannel, request, response);

                try {
                    processor.process();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    Helper.closeChanelConnections(socketChannel);
                }
            }

            if (request.getMethod() == AfspMethod.POST) {
                processor = new PostProcessor(socketChannel, request, response);

                try {
                    processor.process();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    Helper.closeChanelConnections(socketChannel);
                }
            }

            if (request.getMethod() == AfspMethod.DELETE) {
                new DeleteProcessor(socketChannel.socket(), request, response);

                try {
                    result = processor.process();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    Helper.closeChanelConnections(socketChannel);
                }
            }

        } catch (IOException e) {
            LOGGER.error("Error while connecting to server");
            e.printStackTrace();
        } catch (AfspParsingException e) {
            LOGGER.error("Error while parsing response");

            e.printStackTrace();
        } catch (AfspResponseException e) {
            LOGGER.error("Error while processing response");

            e.printStackTrace();
        } catch (AfspProcessingException e) {
            LOGGER.error("Error while processing request");
            e.printStackTrace();
        }
    }
}
