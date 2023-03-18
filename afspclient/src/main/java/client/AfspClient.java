package client;

import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspResponseException;
import afsp.util.Utils;
import config.Configuration;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.AfspFileHandler;
import util.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;


public class AfspClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspClient.class);

    public static void main(String[] args) {
        LOGGER.info("Client Starting...");


        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.port());
        LOGGER.info("Using Host: " + conf.host());
        LOGGER.info("Using Folder: " + conf.folder());


        try {
            Socket socket = new Socket(conf.host(), conf.port());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            AfspResponseParser parser = new AfspResponseParser();
            AfspResponse response = null;
            AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().folder());
            //String target = "/";
            String target = "(Malazan Book of the Fallen 4) Erikson, Steven - A Malazan Book of the Fallen Collection 4.epub";
            AfspRequest request = null;
            try {
                request= new AfspRequest().setMethod(AfspMethod.GET).setRequestTarget(target);
                request.addHeader(new AfspHeader(AfspHeader.HeaderType.CHARSET).setHeaderContent("UTF8"));
                request.addHeader(new AfspHeader(AfspHeader.HeaderType.BUFFER_SIZE).setHeaderContent("8192"));
                request.addHeader(new AfspHeader(AfspHeader.HeaderType.TIME_OUT).setHeaderContent("10000"));
            } catch (AfspParsingException e) {
                out.write(new AfspResponse(e.getErrorCode()).toString().getBytes());
            }
            LOGGER.info("** REQUEST **\n" + request.toString());
            if (request == null){
                out.write(new AfspResponse(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR).toString().getBytes());
            } else {
                out.write(request.toString().getBytes());
                try {
                    // try to save file
                    response = parser.parseResponse(in);
                    if (response.getStatusCode() == AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE){
                        long fileSize = -1;
                        long identifier = -1;
                        int bufferSize = -1;
                        for(AfspHeader _header: response.getHeaderList()){
                            if (_header.getHeaderType().equals(AfspHeader.HeaderType.FILE_SIZE)){
                                fileSize = Long.parseLong(_header.getHeaderContent());
                            }
                            if (_header.getHeaderType().equals(AfspHeader.HeaderType.IDENTIFIER)){
                                identifier = Long.parseLong(_header.getHeaderContent());
                            }
                            if (_header.getHeaderType().equals(AfspHeader.HeaderType.BUFFER_SIZE)){
                                LOGGER.debug("VALUES: "+ _header.getHeaderContent());

                                bufferSize = Integer.parseInt(_header.getHeaderContent());
                            }
                        }
                        fileHandler.receiveFile(socket.getChannel(),fileSize,8192,target);
                    }
                } catch (AfspParsingException | AfspResponseException e) {
                    e.printStackTrace();
                }
            }
            Helper.closeConnections(in, out, socket);
        } catch (IOException e) {
            System.out.println("Could not connect to server");
        }
    }

}
