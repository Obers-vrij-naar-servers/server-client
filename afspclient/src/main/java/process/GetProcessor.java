package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import config.ConfigurationManager;
import util.AfspBackupFileHandler;

import java.nio.channels.SocketChannel;


public class GetProcessor extends BaseProcessor {

    private final AfspBackupFileHandler fileHandler = new AfspBackupFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
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
        response.getheaderList();

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
        var identifier = response.getheaderList().stream().filter(h -> h.getHeaderType() == AfspHeader.HeaderType.IDENTIFIER).findFirst().orElse(null);
        if (identifier == null){
            throw new Exception("Identifier is not valid");
        }
        fileHandler.receiveFile(socketChannel, fileSize, 8192, request.getTarget(),identifier.getHeaderContent());
    }


}
