import afsp.AfspHeader;
import afsp.AfspResponse;
import afsp.AfspStatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AfspResponseTest {

    private AfspResponse afspResponse;

    @BeforeEach
    public void setUp() {
        afspResponse = new AfspResponse(AfspStatusCode.SERVER_SUCCESS_200_OK);
        afspResponse.addHeader(new AfspHeader(AfspHeader.HeaderType.IDENTIFIER).setHeaderContent("1234567890"));
        afspResponse.addHeader(new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH).setHeaderContent("12345"));
    }

    @Test
    void testToStringOnlyHeaders() {
        afspResponse.setBody(null);
        String expectedString = "AFSP/1.0 200 OK\r\n" +
                "Identifier: 1234567890\r\n" +
                "Content-length: 12345\r\n" +
                "\r\n";

        assertEquals(expectedString, afspResponse.toString());
    }

    @Test
    void testToStringIncludesBody() {
        String body = "<html><body><h1>Hello World!</h1></body></html>";
        afspResponse.setBody(body);

        String expectedString = "AFSP/1.0 200 OK\r\n" +
                "Identifier: 1234567890\r\n" +
                "Content-length: 12345\r\n\r\n" +
                body;

        assertEquals(expectedString, afspResponse.toString());
    }


    //   Test that the toString method returns a string that matches the expected format for a response message,
    //   including the protocol version, status code, and message.

    @Test
    public void testGetStatusCode() {
        assertEquals(200, afspResponse.getStatusCode());
    }

    @Test
    public void testSetStatusCode() {
        AfspStatusCode newStatus = AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR;
        afspResponse = new AfspResponse(newStatus);
        assertEquals(newStatus.STATUS_CODE, afspResponse.getStatusCode(), "Status code should be set correctly");
        assertEquals(newStatus.MESSAGE, afspResponse.getMessage(), "Message should be set correctly");
        String responseString = afspResponse.toString();
        assertTrue(responseString.contains(Integer.toString(newStatus.STATUS_CODE)), "Status code should be updated in the response string");
    }

    @Test
    public void testAddHeader() {
        // Add a new header
        afspResponse.addHeader(new AfspHeader(AfspHeader.HeaderType.CHARSET).setHeaderContent("UTF8"));

        // Check that the header was added
        List<AfspHeader> headers = afspResponse.getheaderList();
        boolean foundHeader = false;
        for (AfspHeader header : headers) {
            if (header.getHeaderType() == AfspHeader.HeaderType.CHARSET && header.getHeaderContent().equals("UTF8")) {
                foundHeader = true;
                break;
            }
        }
        assertTrue(foundHeader, "Failed to add header to AfspResponse");
    }

    @Test
    public void testSetBody() {
        // Set a new body
        String newBody = "<html><body><h2>New Body</h2></body></html>";
        afspResponse.setBody(newBody);

        // Check that the body was set correctly
        String responseString = afspResponse.toString();
        assertTrue(responseString.contains(newBody), "Failed to set body in AfspResponse");
    }

    @Test
    void testGetHeaderList() {
        List<AfspHeader> headers = afspResponse.getheaderList();
        assertEquals(2, headers.size());
        assertEquals("Identifier", headers.get(0).getHeaderType().toString());
        assertEquals("1234567890", headers.get(0).getHeaderContent());
        assertEquals("Content-length", headers.get(1).getHeaderType().toString());
        assertEquals("12345", headers.get(1).getHeaderContent());
    }

    @Test
    public void testContainsHeadersTrue() {
        assertTrue(afspResponse.containsHeaders(
                AfspHeader.HeaderType.CONTENT_LENGTH,
                AfspHeader.HeaderType.IDENTIFIER
        ));
    }


}