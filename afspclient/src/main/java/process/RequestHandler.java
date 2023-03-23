package process;

import afsp.AfspRequest;
import afsp.AfspRequestParser;
import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspResponseException;
import client.AfspClient;
import config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Helper;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private final Configuration conf;

    public RequestHandler(Configuration conf) {
        this.conf = conf;
    }

    public void handle(method selectedMenuOption) {
        try {
            Socket socket = new Socket(conf.getHost(), conf.getPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            AfspResponseParser parser = new AfspResponseParser();

//            try {
//                String rawDataString = "GET /hello.htm AFSP/1.0\r\n" +
//                        "Content-length: 8192\r\n";
//                InputStream inputStream = new ByteArrayInputStream(rawDataString.getBytes(StandardCharsets.UTF_8));
//
//                AfspRequestParser afspRequestParser = new AfspRequestParser();
//                AfspRequest request = afspRequestParser.parseAfspRequest(inputStream);

//
//
//            } catch (AfspParsingException e) {
//                e.printStackTrace();
//            }


            try {
                AfspRequest request = new AfspRequest();
                request.setMethod("LIST");
                request.setRequestTarget("/");
                LOGGER.info("Request: " + request.toString());
                out.write(request.toString().getBytes());

            } catch (AfspParsingException e) {
                e.printStackTrace();
            }


//            String rawDataString = "LIST Requirements.pdf AFSP/1.0\r\n\r\n";
//            out.write(rawDataString.getBytes());
//            out.flush();

            try{
                AfspResponse response = parser.parseResponse(in);
                System.out.println(response.toString());
                return;
            } catch (AfspParsingException | AfspResponseException e) {
                e.printStackTrace();
            }
            Helper.closeConnections(in,out,socket);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildRequest(method selectedMenuOption) {
        switch (selectedMenuOption) {
            case SHOW_ALL_FILES:
                return "LIST / AFSP/1.0\r\n\r\n";
            case SYNC_FILES_TO_LOCAL_FOLDER:
                // implement the logic for the SYNC_FILES_TO_LOCAL_FOLDER request
                break;
            case UPLOAD_FILES_TO_SERVER:
                // implement the logic for the UPLOAD_FILES_TO_SERVER request
                break;
            case EXIT:
                System.out.println("Exiting...");
                System.exit(0);
                break;
            default:
                throw new IllegalArgumentException("Invalid option selected.");
        }
        return "";
    }
}
