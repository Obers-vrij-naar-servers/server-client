package util;

import afsp.AfspResponseException;
import afsp.AfspStatusCode;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AfspFileHandler {

    private String localFileDir;
    private List<String> fileList;

//    public AfspFileHandler() {
//        this(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
//    }

    public AfspFileHandler(String folder) {
        localFileDir = folder;
        System.out.println("File handler created");
        File file = new File(localFileDir);
        fileList = Arrays.asList(file.list());
    }

    public List<String> getFileList() throws AfspResponseException {
        if (fileList == null || fileList.size() == 0){
            throw new AfspResponseException(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
        return fileList;
    }
}
