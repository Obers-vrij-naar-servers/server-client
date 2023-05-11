package afsp;

import afsp.exception.AfspProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AfspMessage {

    protected final static Logger LOGGER = LoggerFactory.getLogger(AfspResponse.class);
    protected List<AfspHeader> headerList = new ArrayList<>();

    public void addHeader(AfspHeader header) {
        if (this.headerList == null) {
            this.headerList = new ArrayList<>();
        }
        this.headerList.add(header);
    }

    public List<AfspHeader> getHeaderList() {
        return headerList;
    }

    public AfspMessage setHeaderList(List<AfspHeader> headerList) {
        this.headerList = headerList;
        return this;
    }

    public boolean containsHeaders(AfspHeader.HeaderType... headerTypes) {
        if (this.headerList == null || this.headerList.isEmpty()) {
            LOGGER.debug(" ** HEADERLIST NULL / EMPTY **");
            return false;
        }

        for (AfspHeader.HeaderType headerType : headerTypes) {
            boolean found = false;
            for (AfspHeader header : this.headerList) {
                if (header.getHeaderType().equals(headerType)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }


    public AfspHeader getHeader(AfspHeader.HeaderType headerType) throws AfspProcessingException {
        var optionalHeader = headerList.stream().filter(h -> h.getHeaderType() == headerType).findFirst();
        if (optionalHeader.isEmpty()) {
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
        return optionalHeader.get();
    }

    String printHeaders() {
        if (headerList == null || headerList.isEmpty()) {
            return "\r\n";
        }
        String headerString = "";

        for (AfspHeader _header : headerList) {
            headerString += _header.getHeaderType().toString() + ": " + _header.getHeaderContent() + "\r\n";
        }

        return headerString + "\r\n";
    }

}
