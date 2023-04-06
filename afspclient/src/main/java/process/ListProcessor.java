package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;
import util.FileInfo;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static afsp.AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR;

public class ListProcessor extends BaseProcessor {
    List<String> files;

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    public ListProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        super(request, response);
    }

    @Override
    public void process() throws Exception {

        try {
            String responseContent = response.getBody();
            // Strip \n from response
            responseContent = responseContent.substring(0, responseContent.length() - 1);

            List<AfspHeader> responseHeaders = response.getHeaderList();

            // Split response into separate file names
            String[] fileNames = responseContent.split("\\n");

            // Convert array to list
            files = Arrays.asList(fileNames);

            System.out.println();
            System.out.println("\u001B[36m" + "Files on the server:" + "\u001B[0m");
            System.out.println();

            // with index
            for (int i = 0; i < files.size(); i++) {
                System.out.println("\u001B[32m" + (i + 1) + ". " + files.get(i)  + "\u001B[0m");
            }

            AfspFileHandler.setTargetFiles(buildFileList(files, responseHeaders));

        } catch (Exception e) {
            throw new AfspProcessingException(SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }

    }


    public List<FileInfo> buildFileList(List<String> files, List<AfspHeader> responseHeaders) {

        var fileInfoList = new ArrayList<FileInfo>();

        for (String _file : files) {
            var headers = responseHeaders;
            var fileInfo = new FileInfo(_file, headers);
            fileInfoList.add(fileInfo);
        }

        return fileInfoList;
    }
}
