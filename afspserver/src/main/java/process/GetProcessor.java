package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.List;

public class GetProcessor extends RequestProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final SocketChannel channel;

    public GetProcessor(AfspRequest request, AfspResponse response, SocketChannel channel) {
        super(request, response);
        this.channel = channel;
    }
    public void process() throws AfspProcessingException{

        //validate headers
        if (!request.containsHeaders(AfspHeader.HeaderType.BUFFER_SIZE,AfspHeader.HeaderType.CHARSET,AfspHeader.HeaderType.TIME_OUT)){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String target = request.getTarget();
        OutputStream out;
        out = Channels.newOutputStream(channel);
        AfspResponse response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        List<AfspHeader> headerList = fileHandler.getFileInfo(target);
        response.setHeaderList(headerList);
        LOGGER.info(response.toString());

        try {
            out.write(response.toString().getBytes());
        } catch (IOException e) {
            //TODO HANDLE ERROR
            e.printStackTrace();
        }
        try{
            fileHandler.sendFile(target, channel,8192);
        }catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        done = true;
    }
}
