import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class InputStreamToSocketChannel {

    public static SocketChannel wrap(InputStream inputStream, Socket socket) throws IOException {
        SocketChannel socketChannel = socket.getChannel();
        if (socketChannel == null) {
            throw new IOException("Socket channel not available");
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inputStream.available() > 0) {
            int count = inputStream.read(buffer.array());
            buffer.position(count);
            buffer.flip();
            socketChannel.write(buffer);
            buffer.compact();
        }
        return socketChannel;
    }
}