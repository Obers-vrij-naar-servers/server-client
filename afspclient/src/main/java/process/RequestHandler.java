package process;

import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspResponseException;
import config.Configuration;
import util.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler {

    private final Configuration conf;

    public RequestHandler(Configuration conf) {
        this.conf = conf;
    }

    public AfspResponse handle() {
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
                return response;
            } catch (AfspParsingException | AfspResponseException e) {
                e.printStackTrace();
            }
            Helper.closeConnections(in,out,socket);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
