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

    public ProcessResult process() throws Exception {
        // get the file content as bytes
        byte[] fileBytes = response.getBody().getBytes(StandardCharsets.UTF_8);

        SocketChannel channel = socketChannel;

        if (socketChannel == null) {
            throw new Exception("Socket channel is null");
        }

        Long contentLength = 0L;
        int bufferSize = 0;

        for (AfspHeader header : response.getheaderList()) {
            if (header.getHeaderType() == AfspHeader.HeaderType.CONTENT_LENGTH) {
                contentLength = Long.parseLong(header.getHeaderContent());
            }
        }

        fileHandler.receiveFile(socketChannel, contentLength, 8192, request.getTarget());


        return new ProcessResult(null);

    }


}
