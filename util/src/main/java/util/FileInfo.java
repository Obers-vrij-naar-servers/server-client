package util;

import afsp.AfspHeader;

import java.util.List;

public class FileInfo {
    private String fileName;
    private List<AfspHeader> headers;

    public FileInfo(String fileName, List<AfspHeader> headers) {
        this.fileName = fileName;
        this.headers = headers;
    }

    public String getFileName() {
        return fileName;
    }

    public List<AfspHeader> getHeaders() {
        return headers;
    }
}
