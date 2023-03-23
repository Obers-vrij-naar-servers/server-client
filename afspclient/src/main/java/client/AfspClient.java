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

import static util.LoggerConfiguration.reloadLogbackConfiguration;


public class AfspClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspClient.class);

    public static void main(String[] args) {
        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        reloadLogbackConfiguration(AfspClient.class.getClassLoader().getResourceAsStream("client-logback.xml"));

        LOGGER.info("Client Starting...");
        LOGGER.info("Using Mode: " + (conf.getDebug() ? "Debug" : "Production"));
        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Folder: " + conf.getFolder());
        LOGGER.info("Using Host: " + conf.getHost());

        var client = new AfspClient();

        try {
            Socket socket = new Socket(conf.getHost(), conf.getPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            AfspResponseParser parser = new AfspResponseParser();

            String rawDataString = "LIST Requirements.pdf AFSP/1.0\r\n\r\n";
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
