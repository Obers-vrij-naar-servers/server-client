package util;

import java.util.ArrayList;
import java.util.List;

public class ServerFileList {
    private static final ServerFileList INSTANCE = new ServerFileList();

    private List<String> fileNames;

    private ServerFileList() {
        this.fileNames = new ArrayList<>();
    }

    public static ServerFileList getInstance() {
        return INSTANCE;
    }

    public List<String> getFileNames() {
        return this.fileNames;
    }

    public void addFileName(String fileName) {
        this.fileNames.add(fileName);
    }
    public void addFilenames(String filenames) {
        String[] filenameArray = filenames.split("\n");
        for (String filename : filenameArray) {
            if (!filename.isEmpty()) {
                this.addFileName(filename.trim());
            }
        }
    }
}
