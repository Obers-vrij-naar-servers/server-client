package core;

import afsp.*;
import afsp.exception.AfspParsingException;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.ListProcessor;
import util.AfspFileHandler;
import util.Helper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class AfspConnectionWorkerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspConnectionWorkerThread.class);

    private Socket socket;

    public AfspConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            int _byte;
            LOGGER.info(" ** AFSP request received **");

            //TODO read
            AfspRequestParser parser = new AfspRequestParser();
            AfspRequest request = null;
            AfspResponse response = null;
            try{
                request = parser.parseAfspRequest(inputStream);
            } catch (AfspParsingException e){
                response = new AfspResponse(e.getErrorCode());
            }
            if (response == null){
                response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
            }
            if (request.getMethod() == AfspMethod.LIST){
                LOGGER.info(" ** PROCESSING LIST **");
                var processor = new ListProcessor(request,response);
                processor.process();
            }//Send response
            LOGGER.info("Response:\n"+response);
            outputStream.write(response.toString().getBytes());

            LOGGER.info(" * Connection processing finished...");
        } catch (Exception e) {

            LOGGER.error("Problem with communication", e);
            //TODO handle error
            e.printStackTrace();
        } finally {
            Helper.closeConnections(inputStream,outputStream,socket);
        }
    }

}
