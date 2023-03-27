package core;

import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspResponseException;
import config.Configuration;
import factory.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Helper;

import java.io.*;
import java.net.Socket;

public class PromptHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(PromptHandler.class);
    private final Configuration conf;
    private final RequestFactory requestFactory = new RequestFactory();


    public PromptHandler(Configuration conf) {
        this.conf = conf;
    }

    public void handle(PromptResponse promptResponse) {
        try {
            Socket socket = new Socket(conf.getHost(), conf.getPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            AfspResponseParser parser = new AfspResponseParser();
            AfspRequest request = requestFactory.createRequest(promptResponse);
            LOGGER.info("Request: " + request.toString());
            out.write(request.toString().getBytes());


            try{
                AfspResponse response = parser.parseResponse(in);
                System.out.println("response " + response.toString());
                return;
            } catch (AfspParsingException | AfspResponseException e) {
                e.printStackTrace();
            }
            Helper.closeConnections(in,out,socket);

        } catch (IOException | AfspParsingException e) {
            throw new RuntimeException(e);
        }
    }

}
