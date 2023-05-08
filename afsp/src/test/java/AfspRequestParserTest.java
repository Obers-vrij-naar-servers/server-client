import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import afsp.AfspMethod;
import afsp.AfspRequest;
import afsp.AfspRequestParser;
import afsp.exception.AfspParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AfspRequestParserTest {


    @Test
    void testParseAfspRequest_validRequest() throws Exception {
        //Arrange
        InputStreamReader reader = getValidRequest_InputReader();
        AfspRequestParser parser = new AfspRequestParser(reader);
        //Act
        AfspRequest request = parser.parseAfspRequest(mock(SocketChannel.class));

        //Assert
        assertEquals(AfspMethod.LIST, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals("AFSP/1.0", request.getProtocol());
    }

//    @Test
//    void testParseAfspRequest_invalidMethod() {
//        //Arrange
//        SocketChannel channel = null;
//        try {
//            channel = getInvalidMethodChannel();
//        } catch (Exception e) {
//            fail(e.toString());
//        }
//
//        //Act/Assert
//        SocketChannel finalChannel = channel;
//        assertThrows(AfspParsingException.class, () -> {
//            parser.parseAfspRequest(finalChannel);
//        });
//    }
//
//    @Test
//    void testParseAfspRequest_invalidRequestTarget() {
//        //Arrange
//        SocketChannel channel = null;
//        try {
//            channel = getInvalidRequestTargetChannel();
//        } catch (Exception e) {
//            fail("error was thrown");
//        }
//
//        //Act/Assert
//        SocketChannel finalChannel = channel;
//        assertThrows(AfspParsingException.class, () -> {
//            parser.parseAfspRequest(finalChannel);
//        });
//    }
//
//    @Test
//    void testParseAfspRequest_invalidProtocolVersion() {
//        //Arrange
//        SocketChannel channel = null;
//        try {
//            channel = getInvalidProtocolVersionChannel();
//        } catch (Exception e) {
//            fail("error was thrown");
//        }
//
//        //Act/Assert
//        SocketChannel finalChannel = channel;
//        assertThrows(AfspParsingException.class, () -> {
//            parser.parseAfspRequest(finalChannel);
//        });
//    }
//
//    @Test
//    void testParseAfspRequest_missingCROrLF() {
//        //Arrange
//        SocketChannel channel = null;
//        try {
//            channel = getMissingCROrLFChannel();
//        } catch (Exception e) {
//            fail("error was thrown");
//        }
//
//        //Act/Assert
//        SocketChannel finalChannel = channel;
//        assertThrows(AfspParsingException.class, () -> {
//            parser.parseAfspRequest(finalChannel);
//        });
//    }
//
//    @Test
//    void testParseAfspRequest_tooLongMethod() {
//        //Arrange
//        SocketChannel channel = null;
//        try {
//            channel = getTooLongMethodChannel();
//        } catch (Exception e) {
//            fail("error was thrown");
//        }
//
//        //Act/Assert
//        SocketChannel finalChannel = channel;
//        assertThrows(AfspParsingException.class, () -> {
//            parser.parseAfspRequest(finalChannel);
//        });
//    }


    //helper methods

    private ByteBuffer createByteBufferFromString(String str) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes());
        buffer.flip();
        return buffer;
    }

    private InputStreamReader createMockedInputStreamReader(String str) throws IOException {
        ByteBuffer buffer = createByteBufferFromString(str);
        InputStream inputStream = new ByteArrayInputStream(buffer.array());
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }


    private InputStreamReader getValidRequest_InputReader() throws IOException {
        AfspRequest request;
        try {
            request = new AfspRequest().setMethod(AfspMethod.LIST).setRequestTarget("/");
        } catch (AfspParsingException e) {
            throw new IOException(e);
        }
       return createMockedInputStreamReader(request.toString());
    }


    private SocketChannel getInvalidMethodChannel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("BAD /path/to/resource AFSP/1.0\r\n".getBytes());
        buffer.put("Host: localhost\r\n".getBytes());
        buffer.put("\r\n".getBytes());
        buffer.flip();

        return MockSocketChannel.create(buffer);

    }

    private SocketChannel getInvalidRequestTargetChannel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("GET /path/to/resource! AFSP/1.0\r\n".getBytes());
        buffer.put("Host: localhost\r\n".getBytes());
        buffer.put("\r\n".getBytes());
        buffer.flip();

        return MockSocketChannel.create(buffer);
    }

    private SocketChannel getInvalidProtocolVersionChannel() throws Exception {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("GET /path/to/resource AFSP/2.0\r\n".getBytes());
            buffer.put("Host: localhost\r\n".getBytes());
            buffer.put("\r\n".getBytes());
            buffer.flip();

            return MockSocketChannel.create(buffer);
        }

    private SocketChannel getMissingCROrLFChannel() throws Exception {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put("GET /path/to/resource AFSP/1.0\n".getBytes());
                buffer.put("Host: localhost\n".getBytes());
                buffer.flip();

                return MockSocketChannel.create(buffer);
            }

    private SocketChannel getTooLongMethodChannel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("REALLYLONGMETHOD /path/to/resource AFSP/1.0\r\n".getBytes());
        buffer.put("Host: localhost\r\n".getBytes());
        buffer.put("\r\n".getBytes());
        buffer.flip();

                    return MockSocketChannel.create(buffer);
    }

}