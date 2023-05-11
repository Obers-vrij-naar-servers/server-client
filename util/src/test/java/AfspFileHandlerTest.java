import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import util.LoggerConfiguration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.junit.Assert.fail;

public class AfspFileHandlerTest {

    private TestServer server;
    private TestClient client;
    private SocketChannel serverChannel;
    private SocketChannel clientChannel;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {

        server = new TestServer();
        client = new TestClient();
        server.run();
        client.run();

        serverChannel = server.getChannel();
        clientChannel = client.getChannel();
    }
    @AfterEach
    public void cleanUp() {
        server.interrupt();
        client.interrupt();
    }

    @Test
    public void testSendMessage() throws IOException {
        // Perform your tests here using the 'server' and 'receiver' SocketChannels
        // For example, you can send data from the server to the receiver like this:
        String message = "Hello, receiver!";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        serverChannel.write(buffer);

        // And receive data from the receiver like this:
        ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
        clientChannel.read(receiveBuffer);
        String receivedMessage = new String(receiveBuffer.array()).trim();
        System.out.println("Received message: " + receivedMessage);
    }
}
