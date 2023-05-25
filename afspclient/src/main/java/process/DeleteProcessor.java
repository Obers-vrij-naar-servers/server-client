package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspProcessingException;
import config.Configuration;
import config.ConfigurationManager;
import core.PromptResponse;
import factory.RequestFactory;
import util.AfspFileHandler;
import util.Helper;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DeleteProcessor extends BaseProcessor {
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final RequestFactory requestFactory = new RequestFactory();
    private SocketChannel socketChannel;

    public DeleteProcessor(PromptResponse response, Configuration conf) {
       super(response, conf);
    }

    @Override
    public void process() throws Exception {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            InetSocketAddress address = new InetSocketAddress(conf.getHost(), conf.getPort());
            socketChannel.connect(address);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            AfspRequest request = requestFactory.createRequest(promptResponse);

            buffer.put(request.toString().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            AfspResponse response = new AfspResponse();

            if (socketChannel == null) {
                throw new Exception("Socket channel is null");
            }

            AfspResponseParser parser = new AfspResponseParser();

            response = parser.parseResponse(socketChannel);
            if (response.getStatusCode() != 200) {
                System.out.println("Error occurred while deleting file, you might need to run 1. Show all files first");
            } else {
                System.out.println("\u001B[32m\033[1mSUCCESS!! the file is deleted" + request.getTarget() + "\u001B[0m");

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Helper.closeChanelConnections(socketChannel);
        }
    }
}
