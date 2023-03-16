package client;

import afsp.AfspResponse;
import afsp.AfspResponseParser;
import config.Configuration;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            String rawDataString = buildRawData();
            out.write(rawDataString.getBytes());
            AfspResponse response = parser.parseResponse(in);
            in.close();
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildRawData() {
        String rawDataString = "LIST / AFSP/1.0\r\n" +
                "Content-length: 8192\r\n"+
                "Content-length: 100\r\n"+
                "Content-length: 500\r\n\r\n";
        return rawDataString;
    }
}
