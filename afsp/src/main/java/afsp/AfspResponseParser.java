package afsp;

import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.exception.AfspResponseException;
import afsp.util.ByteCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class AfspResponseParser {
    private final Logger LOGGER = LoggerFactory.getLogger(AfspResponseParser.class);

    private InputStreamReader reader = null;

    public AfspResponseParser() {
    }


    public AfspResponse parseResponse(SocketChannel socketChannel) throws AfspParsingException, AfspResponseException, AfspProcessingException {
        LOGGER.info("** Start Parsing Response **");

        reader = new InputStreamReader(Channels.newInputStream(socketChannel), StandardCharsets.UTF_8);

        AfspResponse response = new AfspResponse();
        try {
            parseStatusLine(reader, response);
            AfspHeader.parseHeaders(reader, response);
            parseBody(reader, response);
        } catch (IOException e) {
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public AfspResponse parseResponseToGet(SocketChannel socketChannel) throws AfspParsingException, AfspResponseException {
        LOGGER.info("** Start Parsing Response **");
        InputStreamReader reader = new InputStreamReader(Channels.newInputStream(socketChannel), StandardCharsets.UTF_8);
        AfspResponse response = new AfspResponse();
        try {
            parseStatusLine(reader, response);
            AfspHeader.parseHeaders(reader, response);
        } catch (IOException e) {
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        return response;
    }


    private void parseStatusLine(InputStreamReader reader, AfspResponse response) throws AfspResponseException, IOException {
        boolean protocolParsed = false;
        boolean statusCodeParsed = false;

        StringBuilder responseBuffer = new StringBuilder();
        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == ByteCode.CR.code) {
                if (!protocolParsed || !statusCodeParsed) {
                    throw new AfspResponseException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                _byte = reader.read();
                if (_byte == ByteCode.LF.code) {
                    LOGGER.info("** MESSAGE PARSED: " + responseBuffer + "**");
                    return;
                }
            } else {
                if (_byte == ByteCode.LF.code) {
                    LOGGER.warn(" ** NO _CR AFTER _LF_ ** ");
                    throw new AfspResponseException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            if (_byte == ByteCode.SP.code) {
                if (!protocolParsed) {
                    String _protocol = responseBuffer.toString();
                    boolean _supported = false;
                    for (AfspProtocolVersion _version : AfspProtocolVersion.values()) {
                        if (_version.toString().equals(_protocol)) {
                            _supported = true;
                            break;
                        }
                    }
                    if (!_supported) {
                        throw new AfspResponseException(AfspStatusCode.SERVER_ERROR_505_PROTOCOL_NOT_SUPPORTED);
                    }
                    responseBuffer.delete(0, responseBuffer.length());
                    protocolParsed = true;
                } else if (!statusCodeParsed) {

                    int _status;
                    //return bad request if status is not a number
                    try {
                        _status = Integer.parseInt(responseBuffer.toString());
                    } catch (NumberFormatException e) {
                        throw new AfspResponseException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    boolean _validStatus = false;
                    for (AfspStatusCode statusCode : AfspStatusCode.values()) {
                        if (statusCode.STATUS_CODE == _status) {
                            LOGGER.info("** STATUSCODE PARSED: " + _status + "**");

                            response.setStatusCode(_status);
                            response.setMessage(statusCode.MESSAGE);
                            _validStatus = true;
                            statusCodeParsed = true;
                            responseBuffer.delete(0, responseBuffer.length());
                        }
                    }
                    if (!_validStatus) {
                        throw new AfspResponseException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                }
            } else {
                responseBuffer.append((char) _byte);
            }

        }
    }

    private void parseBody(InputStreamReader reader, AfspResponse response) throws IOException, AfspProcessingException {
        long contentLength;
        try {
            contentLength = Long.parseLong(response.getHeader(AfspHeader.HeaderType.CONTENT_LENGTH).getHeaderContent());
        } catch (AfspProcessingException e) {
            LOGGER.info("** NO CONTENT-LENGTH HEADER, NOT PARSING BODY **");
            return;
        }
        LOGGER.info(" ** PARSING BODY **");
        StringBuilder bodyBuffer = new StringBuilder();
        while (true) {
            if (!reader.ready()) {
                break;
            }
            int _byte = reader.read();
            if (_byte < 0) {
                break;
            }
            if (bodyBuffer.length() >= contentLength) {
                break;
            }
            bodyBuffer.append((char) _byte);
        }
        LOGGER.info(" ** PARSING BODY DONE **");

        if (bodyBuffer.length() == 0) {
            return;
        }
        String body = bodyBuffer.toString();
        response.setBody(body);
    }
}
