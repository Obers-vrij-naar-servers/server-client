import afsp.*;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspResponseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AfspResponseParserTest {

//    private AfspResponseParser responseParser;
//
//    @BeforeAll
//    public void beforeClass(){
//        responseParser = new AfspResponseParser();
//    }
//    @Test
//    void parse_200_OK_No_Headers(){
//        try{
//            AfspResponse response = responseParser.parseResponse(getResponse(createValidListResponse()), new AfspRequest());
//            assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE,response.getStatusCode());
//        }
//        catch (AfspResponseException | AfspParsingException e){
//            fail(e);
//        }
//    }
//
//    @Test
//    void parse_200_OK_header_no_body(){
//        try{
//            AfspResponse response = responseParser.parseResponse(getResponse(createValidListResponseWithOneHeader()), new AfspRequest());
//            assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE,response.getStatusCode());
//            assertEquals(AfspHeader.HeaderType.CONTENT_LENGTH,response.getHeaderList().get(0).getHeaderType());
//        }
//        catch (AfspResponseException | AfspParsingException e){
//            fail(e);
//        }
//    }
//    @Test
//    void parse_200_OK_multiple_headers_no_body(){
//        try{
//            AfspResponse response = responseParser.parseResponse(getResponse(createValidListResponseWithTwoHeader()), new AfspRequest());
//            assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE,response.getStatusCode());
//            assertEquals(AfspHeader.HeaderType.CONTENT_LENGTH,response.getHeaderList().get(0).getHeaderType());
//            assertEquals(2,response.getHeaderList().size());
//        }
//        catch (AfspResponseException | AfspParsingException e){
//            fail(e);
//        }
//    }
//    @Test
//    void parse_200_OK_no_headers_body(){
//        try{
//            AfspResponse response = responseParser.parseResponse(getResponse(createValidListResponseWithNoHeadersAndABody()), new AfspRequest());
//            assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE,response.getStatusCode());
//            assertEquals(0,response.getHeaderList().size());
//            assertEquals("this is the body",response.getBody());
//        }
//        catch (AfspResponseException | AfspParsingException e){
//            fail(e);
//        }
//    }
//    @Test
//    void parse_200_OK_headers_and_body(){
//        try{
//            AfspResponse response = responseParser.parseResponse(getResponse(createValidListResponseWithHeadersAndABody()), new AfspRequest());
//            assertEquals(AfspStatusCode.SERVER_SUCCESS_200_OK.STATUS_CODE,response.getStatusCode());
//            assertEquals(2,response.getHeaderList().size());
//            assertEquals("this is the body",response.getBody());
//        }
//        catch (AfspResponseException | AfspParsingException e){
//            fail(e);
//        }
//    }
//
//
//    private InputStream getResponse(String response){
//        return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
//    }
//
//    private String createValidListResponse(){
//        return "AFSP/1.0 200 OK";
//    }
//    private String createValidListResponseWithOneHeader(){
//        return "AFSP/1.0 200 OK\r\n" +
//                "Content-length: 200\r\n"+
//                "\r\n ";
//    }
//    private String createValidListResponseWithTwoHeader(){
//        return "AFSP/1.0 200 OK\r\n" +
//                "Content-length: 200\r\n"+
//                "File-Size: 200\r\n"+
//                "\r\n";
//    }
//    private String createValidListResponseWithNoHeadersAndABody(){
//        return "AFSP/1.0 200 OK\r\n" +
//                "\r\n" +
//                "this is the body";
//    }
//    private String createValidListResponseWithHeadersAndABody(){
//        return "AFSP/1.0 200 OK\r\n" +
//                "Content-length: 200\r\n"+
//                "File-Size: 200\r\n"+
//                "\r\n" +
//                "this is the body";
//    }
}
