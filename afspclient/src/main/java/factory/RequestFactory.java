package factory;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspStatusCode;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import core.PromptResponse;
import util.AfspFileHandler;
import util.FileInfo;

import java.util.List;


public class RequestFactory {

    public RequestFactory() {
    }
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    public AfspRequest createRequest(PromptResponse promptResponse) throws IllegalArgumentException, AfspParsingException, AfspProcessingException {
        AfspRequest request = new AfspRequest();
        List<AfspHeader> headerList = request.getHeaderList();
        request.setRequestTarget("/");

        switch (promptResponse.getAction()) {
            case SHOW_ALL_FILES -> {
                setMethod(request, "LIST");

            }
            case Download_FILE -> {
                setMethod(request, "GET");
                headerList.add((new AfspHeader(AfspHeader.HeaderType.CHARSET)).setHeaderContent("UTF-8"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.BUFFER_SIZE)).setHeaderContent("8192"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.TIME_OUT)).setHeaderContent("1000"));
                request.setRequestTarget(getTargetFileName());
            }
            case UPLOAD_FILES_TO_SERVER ->  {
                setMethod(request, "POST");
                request.setRequestTarget(getTargetFileName());
                headerList.add((new AfspHeader(AfspHeader.HeaderType.CHARSET)).setHeaderContent("UTF-8"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.BUFFER_SIZE)).setHeaderContent("8192"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.TIME_OUT)).setHeaderContent("1000"));
                var headers = fileHandler.getFileInfo(getTargetFileName());
                for (AfspHeader h : headers) {
                    if(h.getHeaderType() == AfspHeader.HeaderType.FILE_SIZE){
                        headerList.add(new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH).setHeaderContent(h.getHeaderContent()));
                    } else {
                        headerList.add(h);
                    }
                }

            }
            case DELETE_FILE_FROM_SERVER -> {
                setMethod(request, "DELETE");
                headerList.add((new AfspHeader(AfspHeader.HeaderType.TIME_OUT)).setHeaderContent("1000"));
                request.setRequestTarget(getTargetFileName());
            }

            case EXIT -> {
                System.out.println("Exiting...");
                System.exit(0);
            }
            default -> throw new IllegalArgumentException("Invalid option selected.");
        }

        request.setHeaderList(headerList);
        return request;
    }

    private void setMethod(AfspRequest request, String method) {
        try {
            request.setMethod(method);
        } catch (AfspParsingException e) {
            e.printStackTrace();
        }
    }

    private String getTargetFileName() {
        List<FileInfo> targets = null;

        try {
            if (AfspFileHandler.getTargetFiles() != null && AfspFileHandler.getTargetFiles().size() > 0) {
                targets = AfspFileHandler.getTargetFiles();
            } else {
                throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
        } catch (AfspProcessingException e) {
            e.printStackTrace();
        }

        return targets.get(AfspFileHandler.getFileChoice()).getFileName();
    }
}
