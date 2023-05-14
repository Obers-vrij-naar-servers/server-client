import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TestServer extends Thread{

    private ServerSocketChannel serverSocket;

    @Override
    public void run() {
        try{
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("127.0.0.1", 5500));
        serverSocket.configureBlocking(false);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public SocketChannel getChannel()  {
        if (serverSocket.isOpen()) {
            try {
                return serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }

    }
    public TestServer stopServer() throws IOException {
        serverSocket.close();
        return this;
    }
}
