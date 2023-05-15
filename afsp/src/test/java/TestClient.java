import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TestClient extends Thread{

    private SocketChannel channel;

    @Override
    public void run() {
        try{
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("127.0.0.1", 5500));
            channel.finishConnect();

        } catch (Exception e){
            throw new RuntimeException();
        }

    }
    public SocketChannel getChannel(){
        if (channel != null) {
            return channel;
        } else {
            throw new RuntimeException();
        }
    }

    public TestClient stopServer() throws IOException {
        channel.close();
        return this;
    }
}
