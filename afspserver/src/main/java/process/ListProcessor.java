package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ListProcessor extends RequestProcessor{

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    public ListProcessor(AfspRequest request, AfspResponse response){
        super(request,response);
        LOGGER.info("** LIST_PROCESSOR STARTED **");

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
    }
}
