package core;

import afsp.*;
import afsp.exception.AfspParsingException;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.GetProcessor;
import process.ListProcessor;
import process.RequestProcessor;
import util.AfspFileHandler;
import util.Helper;

import java.io.IOException;
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
        AfspRequestParser parser = new AfspRequestParser();
        AfspRequest request = null;
        AfspResponse response = null;
        RequestProcessor processor = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            int _byte;
            request = parser.parseAfspRequest(inputStream);
            LOGGER.info(" ** AFSP request received ** \n" + request);
            response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
            //process list request
            if (request.getMethod() == AfspMethod.LIST) {
                LOGGER.info(" ** PROCESSING LIST **");
                processor = new ListProcessor(request, response);

            }
            //process get request
            if (request.getMethod() == AfspMethod.GET) {
                LOGGER.info(" ** PROCESSING GET **");
                processor = new GetProcessor(request, response, socket);
            }

            if (processor == null) {
                LOGGER.info(" ** REQUEST NOT PROCESSED");
            } else {
                processor.process();
            }
            if (!(processor == null) && !processor.done) {
                response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_304_NOT_MODIFIED);
                try {
                    LOGGER.debug("** WRITING 304");
                    outputStream.write(response.toString().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } catch (AfspParsingException e) {
            response = new AfspResponse(e.getErrorCode());
        } catch (Exception e) {
            LOGGER.error("Problem with communication", e);
            //TODO handle error
            e.printStackTrace();
        } finally {

            Helper.closeConnections(inputStream, outputStream, socket);
            LOGGER.info(" * Connection processing finished...");

        }
    }

}
