package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import afsp.exception.AfspProcessingException;
import config.ConfigurationManager;
import util.AfspBackupFileHandler;
import util.AfspFileHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.List;

public class GetProcessor extends RequestProcessor {

    private final AfspBackupFileHandler fileHandler = new AfspBackupFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final SocketChannel channel;

    public GetProcessor(AfspRequest request, AfspResponse response, SocketChannel channel) {
        super(request, response);
        this.channel = channel;
        LOGGER.debug("REQUEST:\n" + request);
    }
    public void process() throws AfspProcessingException {

        //validate headers
        if (!request.containsHeaders(AfspHeader.HeaderType.BUFFER_SIZE,AfspHeader.HeaderType.CHARSET,AfspHeader.HeaderType.TIME_OUT)){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        var bufferSizeHeader = request.getHeaderList().stream().filter(h -> h.getHeaderType() == AfspHeader.HeaderType.BUFFER_SIZE).findFirst().map(AfspHeader.class::cast).orElse(null);
        int bufferSize;
        try{
            bufferSize = Integer.parseInt(bufferSizeHeader.getHeaderContent());
        } catch (NullPointerException e){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String target = request.getTarget();

        var out = Channels.newOutputStream(channel);
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
        //    public static void sendFile( String fileName, int bufferSize, SocketChannel socketChannel) throws IOException {
        try{
            fileHandler.sendFile(target, bufferSize, channel);
        }catch (IOException e) {
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        done = true;
    }
}
