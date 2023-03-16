package client;

import afsp.exception.AfspParsingException;
import afsp.AfspResponse;
import afsp.exception.AfspResponseException;
import afsp.AfspResponseParser;
import config.Configuration;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class AfspClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspClient.class);

    public static void main(String[] args) {
        LOGGER.info("Client Starting...");

        var client = new AfspClient();

        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Host: " + conf.getHost());


        try {
            Socket socket = new Socket(conf.getHost(), conf.getPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            StringBuilder responseBuffer = new StringBuilder();
            AfspResponseParser parser = new AfspResponseParser();

            String rawDataString = "LIST /requirements.pdf AFSP/1.0\r\n";
            out.write(rawDataString.getBytes());
            try{
                AfspResponse response = parser.parseResponse(in);
                System.out.println(response.toString());
            } catch (AfspParsingException | AfspResponseException e) {
                e.printStackTrace();
            }
            Helper.closeConnections(in,out,socket);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
