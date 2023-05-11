import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class AfspResponseParserTest {

    private InputStreamMocker mocker = new InputStreamMocker();

    @Test
    void testParseAfspResponse_validResponse() throws Exception {
        // Arrange
        InputStreamReader reader = getValidResponse_InputReader();
        AfspResponseParser parser = new AfspResponseParser(reader);

        // Act
        AfspResponse response = parser.parseResponse(mock(SocketChannel.class));

        // Assert
        assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE, response.getStatusCode());
        assertEquals("OK", response.getMessage());
    }

    @Test
    void testParseAfspResponse_invalidResponseStatusCode() throws Exception {
        // Arrange
        InputStreamReader reader = getInvalidResponseStatusCode_InputReader();
        AfspResponseParser parser = new AfspResponseParser(reader);
        AfspResponseException error = null;

        // Act
        try {
            parser.parseResponse(mock(SocketChannel.class));
        } catch (AfspResponseException e) {
            error = e;
        }

        // Assert
        assertNotNull(error);
        assertEquals(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST, error.getErrorCode());
    }

    @Test
    void testParseAfspResponse_invalidProtocolVersion() throws Exception {
        // Arrange
        InputStreamReader reader = getInvalidProtocolVersion_InputReader();
        AfspResponseParser parser = new AfspResponseParser(reader);
        AfspResponseException error = null;

        // Act
        try {
            parser.parseResponse(mock(SocketChannel.class));
        } catch (AfspResponseException e) {
            error = e;
        }

        // Assert
        assertNotNull(error);
        assertEquals(AfspStatusCode.SERVER_ERROR_505_PROTOCOL_NOT_SUPPORTED, error.getErrorCode());
    }

    @Test
    void testParseBody_emptyBody() throws Exception {
        // Arrange
        InputStreamReader reader = getEmptyBody_InputReader();
        AfspResponseParser parser = new AfspResponseParser(reader);
        AfspResponse response = new AfspResponse();

        // Act
        parser.parseResponse(mock(SocketChannel.class));
        // Assert
        assertEquals("", response.getBody());
    }

    @Test
    void testResponseWithHeaders_noBody() {
        InputStreamReader reader = getAllHeaders_noBody_InputReader();
        AfspResponseParser parser = new AfspResponseParser(reader);
        AfspResponse response = new AfspResponse();

        // Act
        try {
            response = parser.parseResponse(mock(SocketChannel.class));
        } catch (AfspParsingException | AfspResponseException e) {
            fail("Error thrown when parsing valid response");
        }

        String contentLengthContent;
        String charsetContent;
        try {
            contentLengthContent = response.getHeader(AfspHeader.HeaderType.CONTENT_LENGTH).getHeaderContent();
        } catch (Exception e) {
            fail("Content-length header content not found");
            return;
        }
        try {
            charsetContent = response.getHeader(AfspHeader.HeaderType.CHARSET).getHeaderContent();
        } catch (Exception e) {
            fail("Charset header content not found");
            return;
        }

        //Assert
        assertTrue(response.containsHeaders(AfspHeader.HeaderType.CONTENT_LENGTH));
        assertTrue(response.containsHeaders(AfspHeader.HeaderType.CHARSET));
        assertEquals("1234", contentLengthContent);
        assertEquals("UTF8", charsetContent);
    }

    @Test
    void testResponseWithBody() {
        InputStreamReader reader = getBodyWithSomeFileNames_InputReader();
        AfspResponseParser parser = new AfspResponseParser(reader);
        AfspResponse response = new AfspResponse();
        // Act
        try {
            response = parser.parseResponse(mock(SocketChannel.class));
        } catch (AfspParsingException | AfspResponseException e) {
            fail("Error thrown when parsing valid response");
        }
        //Assert
        var body = response.getBody();
        var expected = "test.png\r\ntest2.png\r\ntest3.jpg";
        int contentLength;
        try {
            var content = response.getHeader(AfspHeader.HeaderType.CONTENT_LENGTH).getHeaderContent();
            contentLength = Integer.parseInt(content);
        } catch (AfspProcessingException e) {
            fail("Content-length header content not found");
            return;
        }
        body = body.substring(0,contentLength);
        System.out.println(body.length());
        assertEquals(expected, body);

    }

    //Helper functions

    private InputStreamReader getValidResponse_InputReader() {
        String responseString = "AFSP/1.0 200 OK\r\nContent-length: 12\r\n\r\nHello world!";
        return mocker.getReader(responseString);
    }

    private InputStreamReader getInvalidResponseStatusCode_InputReader() {
        String responseString = "AFSP/1.0 999 Invalid Status Code\r\nContent-length: 0\r\n\r\n";
        return mocker.getReader(responseString);
    }

    private InputStreamReader getInvalidProtocolVersion_InputReader() {
        String responseString = "AFSP/2.0 200 OK\r\nContent-length: 12\r\n\r\nHello world!";
        return mocker.getReader(responseString);
    }

    private InputStreamReader getEmptyBody_InputReader() {
        AfspResponse response = new AfspResponse()
                .setStatusCode(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE);
        return mocker.getReader(response.toString());
    }

    private InputStreamReader getAllHeaders_noBody_InputReader() {
        AfspResponse response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH).setHeaderContent("1234"));
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CHARSET).setHeaderContent("UTF8"));
        return mocker.getReader(response.toString());
    }

    private InputStreamReader getBodyWithSomeFileNames_InputReader() {
        AfspResponse response = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CHARSET).setHeaderContent("UTF8"));

        response.setBody("test.png\r\n" +
                "test2.png\r\n" +
                "test3.jpg");
        response.addHeader(new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH).setHeaderContent(String.valueOf(response.getBody().getBytes().length)));
        return mocker.getReader(response.toString());

    }
}
