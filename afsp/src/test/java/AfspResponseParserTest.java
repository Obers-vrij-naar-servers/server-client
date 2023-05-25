import afsp.*;
import afsp.exception.AfspException;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class AfspResponseParserTest {
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
    void parseValidResponseTest() throws Exception {
        // Arrange
        var response = getValidResponseString();
        AfspResponse incoming = null;
        AfspException error = null;
        // Act
        try {
            incoming = transferResponse(response);
        } catch (AfspException e) {
            fail("Exception thrown on valid response");
        }
        // Assert
        assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE, incoming.getStatusCode());
        assertEquals("OK", incoming.getMessage());
    }

    @Test
    void testParseAfspResponse_invalidResponseStatusCode() throws Exception {
        //Arrange
        var response = getInvalidResponseStatusCodeString();
        AfspException error = null;
        // Act
        try {
            transferResponse(response);
        } catch (AfspException e) {
            error = e;
        }
        assertNotNull(error);
        assertEquals(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST, error.getErrorCode());
    }

    @Test
    void parseInvalidProtocolResponseTest() throws Exception {
        //Arrange
        var response = getInvalidProtocolVersionString();
        AfspException error = null;
        // Act
        try {
            transferResponse(response);
        } catch (AfspException e) {
            error = e;
        }
        assertNotNull(error);
        assertEquals(AfspStatusCode.SERVER_ERROR_505_PROTOCOL_NOT_SUPPORTED, error.getErrorCode());
    }

    //
    @Test
    void testParseBody_emptyBody() throws Exception {
        // Arrange
        var response = getEmptyBodyResponseString();
        AfspResponse incoming = null;
        AfspException error = null;
        // Act
        try {
            incoming = transferResponse(response);
        } catch (AfspException e) {
            fail("Exception thrown on valid response");
        }
        // Assert
        assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE, incoming.getStatusCode());
        assertEquals("", incoming.getBody());
    }

    @Test
    void testResponseWithHeaders_noBody() throws Exception {
        // Arrange
        var response = getAllHeadersNoBodyResponseString();
        AfspResponse incoming = null;
        AfspException error = null;
        // Act
        try {
            incoming = transferResponse(response);
        } catch (AfspException e) {
            fail("Exception thrown on valid response");
        }

        String contentLengthContent;
        String charsetContent;
        try {
            contentLengthContent = incoming.getHeader(AfspHeader.HeaderType.CONTENT_LENGTH).getHeaderContent();
        } catch (Exception e) {
            fail("Content-length header content not found");
            return;
        }
        try {
            charsetContent = incoming.getHeader(AfspHeader.HeaderType.CHARSET).getHeaderContent();
        } catch (Exception e) {
            fail("Charset header content not found");
            return;
        }

        //Assert
        assertTrue(incoming.containsHeaders(AfspHeader.HeaderType.CONTENT_LENGTH));
        assertTrue(incoming.containsHeaders(AfspHeader.HeaderType.CHARSET));
        assertEquals("1234", contentLengthContent);
        assertEquals("UTF8", charsetContent);
    }

    @Test
    void testResponseWithBody() throws InterruptedException {

        var response = getBodyWithSomeFileNamesString();
        AfspResponse incoming = null;
        // Act
        try {
            incoming = transferResponse(response);
        } catch (AfspException e) {
            fail("Exception thrown on valid response");
        }

        var body = incoming.getBody();
        var expected = "test.png\r\ntest2.png\r\ntest3.jpg";
        int contentLength;
        try {
            var content = incoming.getHeader(AfspHeader.HeaderType.CONTENT_LENGTH).getHeaderContent();
            contentLength = Integer.parseInt(content);
        } catch (AfspProcessingException e) {
            fail("Content-length header content not found");
            return;
        }
        body = body.substring(0, contentLength);
        assertEquals(expected, body);

    }

    //Helper functions

    private String getValidResponseString() {
        String responseString = "AFSP/1.0 200 OK\r\nContent-length: 12\r\n\r\nHello world!";
        return responseString;
    }

    private String getInvalidResponseStatusCodeString() {
        String responseString = "AFSP/1.0 999 Invalid Status Code\r\nContent-length: 0\r\n\r\n";
        return responseString;
    }

    private String getInvalidProtocolVersionString() {
        String responseString = "AFSP/2.0 200 OK\r\nContent-length: 12\r\n\r\nHello world!";
        return responseString;
    }

    private String getEmptyBodyResponseString() {
        AfspResponse response = new AfspResponse()
                .setStatusCode(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE);
        return response.toString();
    }

    private String getAllHeadersNoBodyResponseString() {
        AfspResponse response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH).setHeaderContent("1234"));
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CHARSET).setHeaderContent("UTF8"));
        return response.toString();
    }

    private String getBodyWithSomeFileNamesString() {
        AfspResponse response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CHARSET).setHeaderContent("UTF8"));

        response.setBody("test.png\r\n" +
                "test2.png\r\n" +
                "test3.jpg");
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH).setHeaderContent(String.valueOf(response.getBody().getBytes().length)));
        return response.toString();

    }

    private AfspResponse transferResponse(String outResponse) throws AfspException, InterruptedException {
        AtomicReference<AfspResponse> inResponse = new AtomicReference<>();
        var parser = new AfspResponseParser();
        AtomicReference<AfspException> error = new AtomicReference<>();
        //Act
        Thread serverThread = new Thread(() -> {
            try {
                inResponse.set(parser.parseResponse(serverChannel));
            } catch (AfspParsingException | AfspResponseException | AfspProcessingException e) {
                error.set(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                clientChannel.write(ByteBuffer.wrap(outResponse.toString().getBytes()));
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
        return inResponse.get();
    }
}
