package factory;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.exception.AfspParsingException;
import core.PromptResponse;
import java.util.List;


public class RequestFactory {

    public RequestFactory() {
    }

    public AfspRequest createRequest(PromptResponse promptResponse) throws IllegalArgumentException, AfspParsingException {
        AfspRequest request = new AfspRequest();
        List<AfspHeader> headerList = request.getHeaderList();
        request.setRequestTarget(promptResponse.getRequestPath());


        switch (promptResponse.getAction()) {
            case SHOW_ALL_FILES -> {
                setMethod(request, "LIST");
            }
            case SYNC_FILES_TO_LOCAL_FOLDER  -> {
                setMethod(request, "GET");
                headerList.add((new AfspHeader(AfspHeader.HeaderType.CHARSET)).setHeaderContent("UTF-8"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.BUFFER_SIZE)).setHeaderContent("8192"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.TIME_OUT)).setHeaderContent("1000"));

            }
            case UPLOAD_FILES_TO_SERVER ->  {
                setMethod(request, "POST");
                headerList.add((new AfspHeader(AfspHeader.HeaderType.CHARSET)).setHeaderContent("UTF-8"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.BUFFER_SIZE)).setHeaderContent("8192"));
                headerList.add((new AfspHeader(AfspHeader.HeaderType.TIME_OUT)).setHeaderContent("1000"));
                // TODO content-length

            }
            case DELETE_FILE_FROM_SERVER -> {
                setMethod(request, "DELETE");
                headerList.add((new AfspHeader(AfspHeader.HeaderType.TIME_OUT)).setHeaderContent("1000"));
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
}
