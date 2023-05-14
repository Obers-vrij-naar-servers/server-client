import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicReference;

import afsp.*;
import afsp.exception.AfspParsingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AfspRequestParserTest {

    private InputStreamMocker mocker = new InputStreamMocker();

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
    public void cleanUp() throws IOException {
        server.stopServer().interrupt();
        client.stopServer().interrupt();
    }

    @Test
    void testParseValidRequestWithMultiThreadedChannels() throws AfspParsingException, InterruptedException {
        //Arrange
        var outRequest = new AfspRequest();
        AtomicReference<AfspRequest> inRequest = new AtomicReference<>();
        outRequest.setMethod(AfspMethod.LIST);
        outRequest.setRequestTarget("/");
        var parser = new AfspRequestParser();

        //Act
        Thread serverThread = new Thread(() -> {
            try {
                inRequest.set(parser.parseAfspRequest(serverChannel));
            } catch (AfspParsingException e) {
                throw new RuntimeException(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                clientChannel.write(ByteBuffer.wrap(outRequest.toString().getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        clientThread.start();

        serverThread.join();
        clientThread.join();

        //Assert
        assertEquals(outRequest.getMethod(), inRequest.get().getMethod());
        assertEquals(outRequest.getRequestTarget(),inRequest.get().getRequestTarget());
    }

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


    @Test
    void testParseAfspRequest_invalidMethod() throws Exception {
        //Arrange
        InputStreamReader reader = getInvalidMethod_InputReader();
        AfspRequestParser parser = new AfspRequestParser(reader);
        AfspParsingException error = null;
        //Act
        try {
            parser.parseAfspRequest(mock(SocketChannel.class));
        } catch (AfspParsingException e) {
            error = e;
        }
        if (error == null) {
            fail("No error thrown on BAD request");
        }
        assertEquals(AfspStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, error.getErrorCode());
    }

    @Test
    void testParseAfspRequest_invalidRequestTarget() throws Exception {
        //Arrange
        InputStreamReader reader = getInvalidRequestTarget_InputReader();
        AfspRequestParser parser = new AfspRequestParser(reader);
        AfspParsingException error = null;
        //Act
        try {
            parser.parseAfspRequest(mock(SocketChannel.class));
        } catch (AfspParsingException e) {
            error = e;
        }
        if (error == null) {
            fail("No error thrown on SP target");
        }
        assertEquals(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST, error.getErrorCode());
    }

    @Test
    void testParseAfspRequest_invalidProtocolVersion() throws Exception {
        //Arrange
        InputStreamReader reader = getInvalidProtocolVersion_InputReader();
        AfspRequestParser parser = new AfspRequestParser(reader);
        AfspParsingException error = null;
        //Act
        try {
            parser.parseAfspRequest(mock(SocketChannel.class));
        } catch (AfspParsingException e) {
            error = e;
        }
        if (error == null) {
            fail("No error thrown on AFSP/2.0 protocol");
        }
        assertEquals(AfspStatusCode.SERVER_ERROR_505_PROTOCOL_NOT_SUPPORTED, error.getErrorCode());
    }

    @Test
    void testParseAfspRequest_MissingCRorLF() throws Exception {
        //Arrange
        InputStreamReader reader = getMissingCROrLF_InputReader();
        AfspRequestParser parser = new AfspRequestParser(reader);
        AfspParsingException error = null;
        //Act
        try {
            parser.parseAfspRequest(mock(SocketChannel.class));
        } catch (AfspParsingException e) {
            error = e;
        }
        if (error == null) {
            fail("No error thrown on missing CR protocol");
        }
        assertEquals(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST, error.getErrorCode());
    }

    //helper methods

    private InputStreamReader getValidRequest_InputReader() throws IOException {
        AfspRequest request;
        try {
            request = new AfspRequest().setMethod(AfspMethod.LIST).setRequestTarget("/");
        } catch (AfspParsingException e) {
            throw new IOException(e);
        }
        return mocker.getReader(request.toString());
    }


    private InputStreamReader getInvalidMethod_InputReader() {
        var request = "BAD /path/to/resource AFSP/1.0\r\n" +
                "\r\n" +
                "Host: localhost\r\n";

        return mocker.getReader(request);

    }

    private InputStreamReader getInvalidRequestTarget_InputReader() throws AfspParsingException {
        var request = new AfspRequest();
        request.setMethod(AfspMethod.GET);
        request.setRequestTarget(" ");
        return mocker.getReader(request.toString());
    }

    private InputStreamReader getInvalidProtocolVersion_InputReader() {
        var request = "GET /path/to/resource AFSP/2.0\r\n";
        return mocker.getReader(request);
    }

    private InputStreamReader getMissingCROrLF_InputReader() {
        var request = "GET /path/to/resource AFSP/1.0\n";
        return mocker.getReader(request);
    }

}