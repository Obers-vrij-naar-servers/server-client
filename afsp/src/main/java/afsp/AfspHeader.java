package afsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AfspHeader {
    private final static Logger LOGGER = LoggerFactory.getLogger(AfspHeader.class);

    private HeaderType headerType;
    private String headerContent;

    public AfspHeader(HeaderType headerType) {
        this.headerType = headerType;
    }
    AfspHeader(String headerName) throws AfspParsingException{
        for(HeaderType _headerType: HeaderType.values()){
            if(headerName.equals(_headerType.toString())){
                this.headerType = _headerType;
                return;
            }
        }
        throw new AfspParsingException(
                AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR
        );

    }

    public HeaderType getHeaderType(){
        return headerType;
    }

    public String getHeaderContent() {

        return headerContent;
    }

    public void setHeaderContent(String headerContent) {
        this.headerContent = headerContent;
    }

    public enum HeaderType {
        CONTENT_LENGTH{
            @Override
            public String toString() {
                return "Content-length";
            }
        },
        CHARSET{
            @Override
            public String toString(){
                return "Charset";
            }
        },
        BUFFER_SIZE{
            @Override
            public String toString() {
                return "Buffer-Size";
            }
        },
        TIME_OUT{
            @Override
            public String toString() {
                return "Time-out";
            }
        },
        FILE_SIZE{
            @Override
            public String toString() {
                return "File-Size";
            }
        },
        IDENTIFIER{
            @Override
            public String toString() {
                return "Identifier";
            }
        }
    }
    static List<AfspHeader> parseHeaders(InputStreamReader reader, AfspMessage message) throws IOException, AfspParsingException {
        LOGGER.debug(" ** PARSING HEADERS ** ");
        List<AfspHeader> headerList = new ArrayList<>();
        StringBuilder requestBuffer = new StringBuilder();
        int _byte;
        boolean crlfFound = false;
        //Start reading the incoming stream
        while ((_byte = reader.read()) >= 0) {
            if (_byte == ByteCode.CR.code) {
                //check for lineFeed;
                _byte = reader.read();
                if (_byte == ByteCode.LF.code) {
                    if (headerList.isEmpty()){
                        requestBuffer.delete(0, requestBuffer.length());
                        message.setHeaderList(headerList);
                        return headerList;
                    }
                    //check for double CRLF
                    _byte = reader.read();
                    if (_byte == ByteCode.CR.code) {
                        _byte = reader.read();
                        //end of headers, save headerList to request and exit parsing headers
                        if (_byte == ByteCode.LF.code) {
                            headerList.get(headerList.size() - 1).setHeaderContent(requestBuffer.toString());
                            requestBuffer.delete(0, requestBuffer.length());
                            message.setHeaderList(headerList);
                            return headerList;
                        }
                    } else  // save header to local list{
                        headerList.get(headerList.size() - 1).setHeaderContent(requestBuffer.toString());
                        requestBuffer.delete(0, requestBuffer.length());
                    }
                }
            //validate HeaderType
            if (_byte == ByteCode.COL.code) {
                String currentHeaderType = requestBuffer.toString();
                boolean validHeaderType = false;
                for (AfspHeader.HeaderType _headerType : AfspHeader.HeaderType.values()) {
                    if (_headerType.toString().equals(currentHeaderType)) {
                        validHeaderType = true;
                        requestBuffer.delete(0, requestBuffer.length());
                        break;
                    }
                }
                if (!validHeaderType) {
                    LOGGER.debug(" ** INVALID HEADER : " + currentHeaderType + " ** ");
                    throw new AfspParsingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                headerList.add(new AfspHeader(currentHeaderType));
            } else {
                requestBuffer.append((char) _byte);
            }
        }
        return headerList;
    }

}
