package core;

import afsp.*;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.ListProcessor;
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
            LOGGER.warn("SERVER RESPONSE:\n" +response.toString());
            outputStream.write(response.toString().getBytes());

            //TODO writing
//            String html = "<html><head><title>Title</title></head><body><h1>hi!</h1></body></html>";
//            final String CRLF = "\n\r"; //13,10 ASCII
//            String response = "HTTP/1.1 200 OK " + CRLF +
//                    "Content-Length: " + html.getBytes().length + CRLF +
//                    CRLF +
//                    html +
//                    CRLF + CRLF;
//
//            outputStream.write(response.getBytes());


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
