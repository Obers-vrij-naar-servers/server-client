package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Helper {

    public static void closeConnections(InputStream inputStream, OutputStream outputStream, Socket socket){
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
