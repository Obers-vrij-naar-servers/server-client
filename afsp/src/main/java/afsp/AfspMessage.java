package afsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AfspMessage {

    protected final static Logger LOGGER = LoggerFactory.getLogger(AfspResponse.class);
    protected List<AfspHeader> headerList;
    public void addHeader(AfspHeader header){
        if(this.headerList == null){
            this.headerList = new ArrayList<>();
        }
        this.headerList.add(header);
    }

    public List<AfspHeader> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<AfspHeader> headerList) {
        this.headerList = headerList;
    }
}
