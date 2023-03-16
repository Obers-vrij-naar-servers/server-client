package client;

import afsp.AfspResponse;
import afsp.AfspResponseParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class AfspClient {
//    private final static Logger LOGGER = LoggerFactory.getLogger(AfspServer.class);

    public static void main(String[] args) {
        var client = new AfspClient();
        client.run();
    }
    private void run(){

        String host = "localhost";
        int port = 8080;
        try {
            Socket socket = new Socket(host,port);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            StringBuilder responseBuffer = new StringBuilder();
            AfspResponseParser parser = new AfspResponseParser();

            String rawDataString = "LIST / AFSP/1.0\r\n" +
                    "Content-length: 8192\r\n"+
                    "Content-length: 100\r\n"+
                    "Content-length: 500\r\n\r\n";
            out.write(rawDataString.getBytes());
            AfspResponse response = parser.parseResponse(in);
            in.close();
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
