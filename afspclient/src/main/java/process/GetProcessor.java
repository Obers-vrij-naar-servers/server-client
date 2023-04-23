package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;
import util.FileInfo;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;


public class GetProcessor extends BaseProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    public GetProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        super(socket,request, response);
    }

    public void process() throws Exception {

        if (socket == null) {
            throw new Exception("Socket channel is null");
        }
        if(response.getStatusCode() != AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE){
            throw new Exception(response.toString());
        }

        Long fileSize = 0L;
        String identifier = null;

        for (AfspHeader header : response.getheaderList()) {
            if (header.getHeaderType() == AfspHeader.HeaderType.FILE_SIZE) {
                try {
                    fileSize = Long.parseLong(header.getHeaderContent());

                } catch (NumberFormatException e) {
                    throw new Exception("Content length is not a valid number: " + header.getHeaderContent());
                }
            }

            if (header.getHeaderType() == AfspHeader.HeaderType.IDENTIFIER) {
                try {
                    identifier = header.getHeaderContent();
                } catch (NumberFormatException e) {
                    throw new Exception("ID is not a valid number: " + header.getHeaderContent());
                }
            }
        }

        if (fileSize == 0L) {
            throw new Exception("Content length header is missing or has a value of 0");
        }


        if (identifier == null) {
            throw new Exception("Identifier is not valid");
        }

        var filesInTheClient = buildFileList(fileHandler.getFileList());
        var fileListOnServer = AfspFileHandler.getTargetFiles().get(AfspFileHandler.getFileChoice());


        for (FileInfo file : filesInTheClient) {
            if (file.getFileName().equals(fileListOnServer.getFileName())) {
                for (AfspHeader header : file.getHeaders()) {
                    if(header.getHeaderType() == AfspHeader.HeaderType.IDENTIFIER) {
                        if (header.getHeaderContent().equals(identifier)) {
                            System.out.println("\u001B[36m" + "File already exists and is up-to-date!" + "\u001B[0m");
                            return;
                        }
                    }
                }
            }
        }

        System.out.println("\u001B[36m" + "Downloading file:" + "\u001B[0m");
        fileHandler.receiveFile(socket, fileSize, 8192, request.getTarget(), identifier);
    }


    public List<FileInfo> buildFileList(List<String> files) throws AfspProcessingException {

        var fileInfoList = new ArrayList<FileInfo>();

        for (String _file : files) {
            var headers = fileHandler.getFileInfo(_file);
            var fileInfo = new FileInfo(_file, headers);
            fileInfoList.add(fileInfo);
        }

        return fileInfoList;
    }
}
