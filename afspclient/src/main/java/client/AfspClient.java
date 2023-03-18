package client;

import afsp.exception.AfspParsingException;
import afsp.AfspResponse;
import afsp.exception.AfspResponseException;
import afsp.AfspResponseParser;
import afsp.util.Utils;
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


        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Host: " + conf.getHost());


        try {
            Socket socket = new Socket(conf.getHost(), conf.getPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            AfspResponseParser parser = new AfspResponseParser();
            String target = "/";
//            String target = "(Malazan Book of the Fallen 4) Erikson, Steven - A Malazan Book of the Fallen Collection 4.epub";
            target = Utils.encodeString(target);
            String rawDataString = "LIST "+target+" AFSP/1.0\r\n\r\n";

            out.write(rawDataString.getBytes());
            try{
                AfspResponse response = parser.parseResponse(in);
                System.out.println(response.toString());
            } catch (AfspParsingException | AfspResponseException e) {
                e.printStackTrace();
            }
            Helper.closeConnections(in,out,socket);

        } catch (IOException e) {
            System.out.println("Could not connect to server");
        }
    }

}
