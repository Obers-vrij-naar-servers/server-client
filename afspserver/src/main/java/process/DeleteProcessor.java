package process;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class DeleteProcessor extends RequestProcessor{


    public DeleteProcessor(AfspRequest request, AfspResponse response, SocketChannel channel) {
        super(request, response, channel);
    }

    @Override
    public void process() throws AfspProcessingException {
        AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
        OutputStream out = Channels.newOutputStream(channel);
        try {
            fileHandler.deleteFile(request.getTarget());
            response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        } catch (AfspProcessingException e){
            response = new AfspResponse(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
        try {
            out.write(response.toString().getBytes());
        } catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
    }
}
