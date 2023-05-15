import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicReference;

import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AfspRequestParserTest {

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
    void parseValidRequestTest() throws Exception {
        //Arrange
        var outRequest = new AfspRequest();
        AfspRequest inRequest = null;
        outRequest.setMethod(AfspMethod.LIST);
        outRequest.setRequestTarget("/");

        //Act
        try {
            inRequest = transferRequest(outRequest.toString());
        }   catch (AfspParsingException e){
            fail("Error thrown on parsing valid request");
        }

        //Assert
        assertNotNull(inRequest);
        assertEquals(outRequest.getMethod(), inRequest.getMethod());
        assertEquals(outRequest.getRequestTarget(),inRequest.getRequestTarget());
    }


    @Test
    void parseInvalidRequestTest() throws Exception {
        //Arrange
        String outRequest = getInvalidMethodRequestString();
        AfspParsingException error=null;
        try {
            transferRequest(outRequest);
        }   catch (AfspParsingException e){
            error = e;
        }
        //Assert
        assertNotNull(error,"No error thrown on bad request method");
        assertEquals(AfspStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, error.getErrorCode());
    }

    @Test
    void parseInvalidRequestTargetTest() throws Exception {
        //Arrange
        String outRequest = getInvalidRequestTargetString();
        AfspParsingException error=null;
        try {
            transferRequest(outRequest);
        }   catch (AfspParsingException e){
            error = e;
        }
        assertNotNull(error,"No error thrown on invalid request target");
        assertEquals(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST, error.getErrorCode());
    }

    @Test
    void parseInvalidProtocolTest() throws Exception {
        //Arrange
        String outRequest = getInvalidProtocolString();
        AfspParsingException error=null;
        try {
            transferRequest(outRequest);
        }   catch (AfspParsingException e){
            error = e;
        }

        assertNotNull(error,"No error thrown on invalid protocol");
        assertEquals(AfspStatusCode.SERVER_ERROR_505_PROTOCOL_NOT_SUPPORTED, error.getErrorCode());
    }

    @Test
    void testParseAfspRequest_MissingCRorLF() throws Exception {
        //Arrange
        var outRequest = getMissingCROrLFString();
        AfspParsingException error=null;
        try {
            transferRequest(outRequest);
        }   catch (AfspParsingException e){
            error = e;
        }
        assertNotNull(error,"No error thrown on missing CR");
        assertEquals(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST, error.getErrorCode());
    }

    //helper methods

    private String getInvalidMethodRequestString() {
        return "BAD /path/to/resource AFSP/1.0\r\n" +
                "\r\n" +
                "Host: localhost\r\n";
    }

    private String getInvalidRequestTargetString() throws AfspParsingException {
        var request = new AfspRequest();
        request.setMethod(AfspMethod.GET);
        request.setRequestTarget(" ");
        return request.toString();
    }

    private String getInvalidProtocolString() {
        return "GET /path/to/resource AFSP/2.0\r\n";
    }

    private String getMissingCROrLFString() {
        return "GET /path/to/resource AFSP/1.0\n";
    }


    private AfspRequest transferRequest(String outRequest) throws Exception{
        AtomicReference<AfspRequest> inRequest = new AtomicReference<>();
        var parser = new AfspRequestParser();
        AtomicReference<AfspParsingException> error = new AtomicReference<>();
        //Act
        Thread serverThread = new Thread(() -> {
            try {
                inRequest.set(parser.parseAfspRequest(serverChannel));
            } catch (AfspParsingException e) {
                error.set(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                clientChannel.write(ByteBuffer.wrap(outRequest.toString().getBytes()));
            } catch (IOException e) {
            }
        });
        serverThread.start();
        clientThread.start();

        serverThread.join();
        clientThread.join();

        if (error.get() != null) {
            throw error.get();
        }
        return inRequest.get();
    }
}