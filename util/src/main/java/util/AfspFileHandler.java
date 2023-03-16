package util;

import afsp.AfspHeader;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import afsp.AfspStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AfspFileHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(AfspFileHandler.class);

    private String localFileDir;
    private List<String> fileList = new ArrayList<>();


    public AfspFileHandler(String folder) {
        localFileDir = folder;
        System.out.println("File handler created");
        updateFileList();
    }

    private void updateFileList(){
        File file = new File(localFileDir);
        if (file.isDirectory() && file.exists()){
            fileList = Arrays.asList(file.list());
        }
    }

    public List<String> getFileList() throws AfspProcessingException {
        if (fileList == null || fileList.size() == 0){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
        return fileList;
    }
    public List<AfspHeader> getFileInfo(String fileName) throws AfspProcessingException {
        List<AfspHeader> headers = new ArrayList<>();
        updateFileList();
        boolean fileExists = false;

        for(String _fName : fileList){
            if( _fName.equals(fileName)){
                fileExists = true;
            }
        }
        if (!fileExists){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
        LOGGER.info("** FILEPATH: "+this.localFileDir +"/"+fileName+"**");
        Path path = Paths.get(this.localFileDir +"/"+fileName);
        long fileSize;
        try{
            fileSize = Files.size(path);
        } catch (IOException e){
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("** FILE-SIZE: "+fileSize+" **");

        return headers;

    }
}
