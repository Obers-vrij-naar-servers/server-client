package util;

import afsp.AfspResponseException;
import afsp.AfspResponseParser;
import afsp.AfspStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AfspFileHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(AfspFileHandler.class);

    private String localFileDir;
    private List<String> fileList;


    public AfspFileHandler(String folder) {
        localFileDir = folder;
        System.out.println("File handler created");
        File file = new File(localFileDir);
        if (file.isDirectory() && file.exists()){
            fileList = Arrays.asList(file.list());
        }
    }

    public List<String> getFileList() throws AfspResponseException {
        if (fileList == null || fileList.size() == 0){
            throw new AfspResponseException(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
//        for (String fileName : fileList){
//            LOGGER.debug("** "+fileName+" **");
//        }
        return fileList;
    }
}
