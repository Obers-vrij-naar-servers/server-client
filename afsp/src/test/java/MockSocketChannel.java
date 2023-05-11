import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class MockSocketChannel {

    public static SocketChannel create(ByteBuffer inputBuffer) throws IOException {
        SelectableChannel mockSelectableChannel = mock(SelectableChannel.class);
        SocketChannel mockSocketChannel = mock(SocketChannel.class);
        Selector selector = Selector.open();
        SelectionKey selectionKey = mock(SelectionKey.class);
        Socket mockSocket = mock(Socket.class);

        when(mockSocketChannel.register(selector, SelectionKey.OP_READ)).thenReturn(selectionKey);
        when(mockSelectableChannel.configureBlocking(false)).thenReturn(mockSelectableChannel);
        when(mockSelectableChannel.register(selector, SelectionKey.OP_READ)).thenReturn(selectionKey);
        when(mockSocketChannel.configureBlocking(false)).thenReturn(mockSelectableChannel);
        when(mockSocketChannel.read(inputBuffer)).thenReturn(inputBuffer.position());
        when(mockSocketChannel.isOpen()).thenReturn(true);
        when(mockSocketChannel.isConnected()).thenReturn(true);

        InputStreamReader reader = new InputStreamReader(Channels.newInputStream(mockSocketChannel), "UTF-8");
        when(mockSocketChannel.socket()).thenReturn(mockSocket);
        when(mockSocket.getInputStream()).thenReturn(Channels.newInputStream(mockSocketChannel));
        when(mockSocket.getOutputStream()).thenReturn(Channels.newOutputStream(mockSocketChannel));

        when(mockSocketChannel.read(inputBuffer)).thenAnswer(invocationOnMock -> {
            reader.read();
            return inputBuffer.position();
        });

        return mockSocketChannel;
    }
}
