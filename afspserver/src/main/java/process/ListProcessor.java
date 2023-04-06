package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspBackupFileHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.List;

public class ListProcessor extends RequestProcessor{

    private final AfspBackupFileHandler fileHandler = new AfspBackupFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    public ListProcessor(AfspRequest request, AfspResponse response, SocketChannel channel){
        super(request,response, channel);
        LOGGER.info("** LIST_PROCESSOR STARTED **");
        LOGGER.info(request.toString());
    }

    public void process() throws AfspProcessingException {
        String target = request.getTarget();
        List<String> _list = null;
        if (target.equals("/") || target.equals("\"") || target.equals(".")) {
            _list = fileHandler.getFileList();

            if (_list == null) {
                LOGGER.info(" ** LIST IS EMPTY ** ");
                return;
            }
            String bodyString = "";
            for (String fileName : _list) {
                bodyString += fileName + "\n";
            }
            AfspHeader contentLengthHeader = new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH);
            contentLengthHeader.setHeaderContent(String.valueOf(bodyString.getBytes().length));
            response.addHeader(contentLengthHeader);
            response.setBody(bodyString);
        } else {
            var fileListHeaders = fileHandler.getFileInfo(request.getTarget());
            response.setHeaderList(fileListHeaders);
        }
        LOGGER.debug("SENDING RESPONSE: \n" + response);
        OutputStream out = Channels.newOutputStream(channel);
        try {
            out.write(response.toString().getBytes());
        } catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
    }
}
