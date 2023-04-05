package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class GetProcessor extends BaseProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final SocketChannel socketChannel;

    public GetProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        super(request, response);
        this.socketChannel = socket;
    }

    public void process() throws Exception {

        if (socketChannel == null) {
            throw new Exception("Socket channel is null");
        }

        Long fileSize = 0L;

        for (AfspHeader header : response.getheaderList()) {
            if (header.getHeaderType() == AfspHeader.HeaderType.FILE_SIZE) {
                try {
                    fileSize = Long.parseLong(header.getHeaderContent());
                } catch (NumberFormatException e) {
                    throw new Exception("Content length is not a valid number: " + header.getHeaderContent());
                }
            }
        }

        if (fileSize == 0L) {
            throw new Exception("Content length header is missing or has a value of 0");
        }

        fileHandler.receiveFile(socketChannel, fileSize, 8192, request.getTarget());


    }


}
