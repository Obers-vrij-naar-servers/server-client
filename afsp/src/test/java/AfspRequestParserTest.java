//import afsp.*;
//import afsp.exception.AfspParsingException;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.fail;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class AfspRequestParserTest {
//
//    private AfspRequestParser afspParser;
//
//    @BeforeAll
//    public void beforeClass() {
//
//        afspParser = new AfspRequestParser();
//    }
//
//    @Test
//    void parse_GET_AfspRequest() throws AfspParsingException {
//        try{
//            AfspRequest request = afspParser.parseAfspRequest(generateValidGETTestCase());
//            assertEquals(request.getMethod(), AfspMethod.GET);
//        } catch (AfspParsingException e){
//            fail(e);
//        }
//    }
//    @Test
//    void parse_GeT_AfspRequest() throws AfspParsingException {
//        try{
//            AfspRequest request = afspParser.parseAfspRequest(generateInvalidGETTestCase());
//            fail();
//        } catch (AfspParsingException e){
//            assertEquals(e.getErrorCode(), AfspStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
//        }
//    }
//    @Test
//    void parse_TooLongRequestName_Request() throws AfspParsingException {
//        try{
//            AfspRequest request = afspParser.parseAfspRequest(generateInvalidRequestTypeTestCase());
//            fail();
//        } catch (AfspParsingException e){
//            assertEquals(e.getErrorCode(), AfspStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
//        }
//
//    }
//    @Test
//    void parse_InvalidItemsRequestLine_Request() throws AfspParsingException {
//        try{
//            AfspRequest request = afspParser.parseAfspRequest(generateInvalidRequestItemsTestCase());
//            fail();
//        } catch (AfspParsingException e){
//            assertEquals(e.getErrorCode(), AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
//        }
//
//    }
//    @Test
//    void parse_EmptyRequestLine_Request() throws AfspParsingException {
//        try{
//            AfspRequest request = afspParser.parseAfspRequest(generateEmptyRequestLineTestCase());
//            fail();
//        } catch (AfspParsingException e){
//            assertEquals(e.getErrorCode(), AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
//        }
//
//    }
//    @Test
//    void parse_OnlyCR_NoLF_RequestLine_Request() throws AfspParsingException {
//        try{
//            AfspRequest request = afspParser.parseAfspRequest(generateOnlyCRnoLFTestCase());
//            fail();
//        } catch (AfspParsingException e){
//            assertEquals(e.getErrorCode(), AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
//        }
//
//    }
//
//
//    private InputStream generateValidGETTestCase() {
//        String rawDataString = "GET /hello.htm AFSP/1.0\r\n" +
//                "Content-length: 8192\r\n";
//        InputStream inputStream = new ByteArrayInputStream(rawDataString.getBytes(StandardCharsets.UTF_8));
//        return inputStream;
//    }
//    private InputStream generateInvalidGETTestCase() {
//        String rawDataString = "GeT /hello.htm AFSP/1.0\r\n" +
//                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
//                "Host: www.tutorialspoint.com\r\n" +
//                "Accept-Language: en-us\r\n" +
//                "Accept-Encoding: gzip, deflate\r\n" +
//                "Connection: Keep-Alive";
//        InputStream inputStream = new ByteArrayInputStream(rawDataString.getBytes(StandardCharsets.UTF_8));
//        return inputStream;
//    }
//    private InputStream generateInvalidRequestTypeTestCase() {
//        String rawDataString = "GETTETTETT /hello.htm AFSP/1.0\r\n" +
//                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
//                "Host: www.tutorialspoint.com\r\n" +
//                "Accept-Language: en-us\r\n" +
//                "Accept-Encoding: gzip, deflate\r\n" +
//                "Connection: Keep-Alive";
//        InputStream inputStream = new ByteArrayInputStream(rawDataString.getBytes(StandardCharsets.UTF_8));
//        return inputStream;
//    }
//    private InputStream generateInvalidRequestItemsTestCase() {
//        String rawDataString = "GET /first /hello.htm AFSP/1.0\r\n" +
//                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
//                "Host: www.tutorialspoint.com\r\n" +
//                "Accept-Language: en-us\r\n" +
//                "Accept-Encoding: gzip, deflate\r\n" +
//                "Connection: Keep-Alive";
//        InputStream inputStream = new ByteArrayInputStream(rawDataString.getBytes(StandardCharsets.UTF_8));
//        return inputStream;
//    }
//    private InputStream generateEmptyRequestLineTestCase() {
//        String rawDataString = "\r\n" +
//                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
//                "Host: www.tutorialspoint.com\r\n" +
//                "Accept-Language: en-us\r\n" +
//                "Accept-Encoding: gzip, deflate\r\n" +
//                "Connection: Keep-Alive";
//        InputStream inputStream = new ByteArrayInputStream(rawDataString.getBytes(StandardCharsets.UTF_8));
//        return inputStream;
//    }
//    private InputStream generateOnlyCRnoLFTestCase() {
//        String rawDataString = "GET /hello.htm AFSP/1.0\r" + /*no CR*/
//                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
//                "Host: www.tutorialspoint.com\r\n" +
//                "Accept-Language: en-us\r\n" +
//                "Accept-Encoding: gzip, deflate\r\n" +
//                "Connection: Keep-Alive";
//        InputStream inputStream = new ByteArrayInputStream(rawDataString.getBytes(StandardCharsets.UTF_8));
//        return inputStream;
//    }
//}