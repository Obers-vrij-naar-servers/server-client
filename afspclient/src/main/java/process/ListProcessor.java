package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.exception.AfspProcessingException;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;

import static afsp.AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR;

public class ListProcessor extends BaseProcessor {
    List<String> files;

    public ListProcessor(SocketChannel socket, AfspRequest request, AfspResponse response) {
        super(request, response);
    }

    @Override
    public void process() throws AfspProcessingException {

        try {
            String responseContent = response.getBody();
            // Strip \n from response
            responseContent = responseContent.substring(0, responseContent.length() - 1);

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


            ProcessResult.setFiles(files);

        } catch (Exception e) {
            throw new AfspProcessingException(SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }

    }
}
