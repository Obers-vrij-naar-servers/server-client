package afsp;

import afsp.exception.AfspParsingException;
import afsp.util.ByteCode;
import afsp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class AfspRequestParser {

    private final Logger LOGGER = LoggerFactory.getLogger(AfspRequestParser.class);

    public AfspRequest parseAfspRequest(SocketChannel channel) throws AfspParsingException {
        LOGGER.info("** Start Parsing Request **");

        var reader = new InputStreamReader(Channels.newInputStream(channel), StandardCharsets.UTF_8);

        // get request from input stream

        AfspRequest request = new AfspRequest();

        try {
            parseRequestLine(reader, request);
            LOGGER.info(" ** RequestLine Parsed ** ");
        } catch (IOException e) {
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }

        try {
            AfspHeader.parseHeaders(reader, request);
            LOGGER.info(" ** HEADERS Parsed ** ");
        } catch (IOException e) {
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        // log request
        LOGGER.info(" ** Request Parsed ** ");
        LOGGER.info(" ** Request: {} {} {} ** ", request.getMethod(), request.getRequestTarget(), request.getProtocol());
        return request;
    }

    private void parseRequestLine(InputStreamReader reader, AfspRequest request) throws IOException, AfspParsingException {

        boolean methodParsed = false;
        boolean targetParsed = false;

        StringBuilder requestBuffer = new StringBuilder();
        int _byte;
        //Start reading the incoming stream
        while ((_byte = reader.read()) >= 0) {
            if (_byte == ByteCode.CR.code) {
                if (!methodParsed || !targetParsed) {
                    LOGGER.warn(" ** _CR_ BEFORE METHOD OR TARGET PARSED ** ");
                    throw new AfspParsingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                //check for lineFeed;
                _byte = reader.read();
                if (_byte == ByteCode.LF.code) {
                    //only support AFSP/1.0
                    if (!requestBuffer.toString().equals(AfspProtocolVersion.AFSP_1_0.toString())) {
                        LOGGER.warn(" ** PROTOCOL_VERSION WRONG ** ");
                        throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_505_PROTOCOL_NOT_SUPPORTED);
                    } else {
                        request.setProtocol(requestBuffer.toString());
                        requestBuffer.delete(0, requestBuffer.length());
                    }
                    return;
                } else {
                    LOGGER.warn(" ** NO _LF_ AFTER _CR_ ** ");
                    throw new AfspParsingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } else {
                if (_byte == ByteCode.LF.code) {
                    LOGGER.warn(" ** NO _CR AFTER _LF_ ** ");
                    throw new AfspParsingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            if (_byte == ByteCode.SP.code) {
                if (!methodParsed) {
                    request.setMethod(requestBuffer.toString());
                    methodParsed = true;
                } else if (!targetParsed) {
                    String encodedTarget = requestBuffer.toString();
                    //throw error if target starts with a space
                    if(encodedTarget.startsWith(" ")){
                        throw new AfspParsingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    };
                    //throw error if target has length 1 and is not /, \ or
                    if (encodedTarget.length() == 1){
                        if (!encodedTarget.startsWith("/") || !encodedTarget.startsWith("\\")){
                            throw new AfspParsingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                        }
                    }
                    String decodedTarget = Utils.decodeString(encodedTarget);
                    request.setRequestTarget(decodedTarget);
                    LOGGER.warn(" ** targetParsed ** ");
                    targetParsed = true;
                } else {
                    LOGGER.warn(" ** _SP_ AFTER METHOD AND TARGET PARSED ** ");
                    throw new AfspParsingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                requestBuffer.delete(0, requestBuffer.length());

            } else {
                requestBuffer.append((char) _byte);
                if (!methodParsed) {
                    if (requestBuffer.length() > AfspMethod.MAX_LENGTH) {
                        throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }

    }
}
