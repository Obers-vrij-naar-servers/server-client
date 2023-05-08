
import afsp.AfspHeader;
import afsp.AfspMethod;
import afsp.AfspProtocolVersion;
import afsp.AfspRequest;
import afsp.exception.AfspParsingException;
import afsp.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class AfspRequestTest {

    private AfspRequest afspRequest;

    @BeforeEach
    public void setUp() {
        afspRequest = new AfspRequest();
        try {
            afspRequest.setRequestTarget("/index.html");
            afspRequest.setMethod(AfspMethod.GET);
            afspRequest.setHeaderList(new ArrayList<>());
            afspRequest.addHeader(new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH).setHeaderContent("12345"));
            afspRequest.addHeader(new AfspHeader(AfspHeader.HeaderType.TIME_OUT).setHeaderContent("10000"));
        } catch (AfspParsingException e) {
            fail("Failed to set up AfspRequest object with default values.");
        }
    }

    @Test
    public void testSetMethodWithInvalidMethod() {
        assertThrows(AfspParsingException.class, () -> {
            afspRequest.setMethod("INVALID");
        });
    }

    @Test
    public void testSetMethodWithValidMethod() {
        afspRequest.setMethod(AfspMethod.POST);
        assertEquals(AfspMethod.POST, afspRequest.getMethod());
    }

    @Test
    public void testSetProtocolWithNull() {
        assertThrows(AfspParsingException.class, () -> {
            afspRequest.setProtocol(null);
        });
    }

    @Test
    public void testSetProtocolWithEmptyString() {
        assertThrows(AfspParsingException.class, () -> {
            afspRequest.setProtocol("");
        });
    }

    @Test
    public void testSetProtocolWithValidProtocol() {
        try {
            afspRequest.setProtocol(AfspProtocolVersion.AFSP_1_0.toString());
        } catch (AfspParsingException e) {
            fail("Failed to set valid protocol.");
        }
        assertEquals(AfspProtocolVersion.AFSP_1_0.toString(), afspRequest.getProtocol());
    }

    @Test
    public void testSetRequestTargetWithNull() {
        assertThrows(AfspParsingException.class, () -> {
            afspRequest.setRequestTarget(null);
        });
    }

    @Test
    public void testSetRequestTargetWithEmptyString() {
        assertThrows(AfspParsingException.class, () -> {
            afspRequest.setRequestTarget("");
        });
    }

    @Test
    public void testGetTarget() {
        assertEquals("/index.html", afspRequest.getTarget());
    }

    @Test
    public void testToString() {
        String expected = "GET "+ Utils.encodeString("/index.html")+ " AFSP/1.0\r\n" +
                "Content-length: 12345\r\n" +
                "Time-out: 10000\r\n" +
                "\r\n";

        assertEquals(expected,afspRequest.toString());
    }

    @Test
    public void testContainsHeadersTrue() {
        assertTrue(afspRequest.containsHeaders(
                AfspHeader.HeaderType.CONTENT_LENGTH,
                AfspHeader.HeaderType.TIME_OUT
        ));
    }

    @Test
    public void testContainsHeadersFalse() {
        assertFalse(afspRequest.containsHeaders(
                AfspHeader.HeaderType.CONTENT_LENGTH,
                AfspHeader.HeaderType.BUFFER_SIZE
        ));
    }

    @Test
    public void testAddHeader() {
        AfspHeader header = new AfspHeader(AfspHeader.HeaderType.FILE_SIZE);
        header.setHeaderContent("1000");
        afspRequest.addHeader(header);
        assertTrue(afspRequest.getHeaderList().contains(header));
    }

    @Test
    public void testSetHeaderList() {
        List<AfspHeader> newHeaderList = new ArrayList<>();
        AfspHeader contentLengthHeader = new AfspHeader(AfspHeader.HeaderType.CONTENT_LENGTH);
        contentLengthHeader.setHeaderContent("5000");
        newHeaderList.add(contentLengthHeader);
        afspRequest.setHeaderList(newHeaderList);
        assertEquals(newHeaderList, afspRequest.getHeaderList());
    }

}