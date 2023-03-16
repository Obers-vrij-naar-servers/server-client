package afsp;

import java.util.ArrayList;
import java.util.List;

public abstract class AfspMessage {
    private List<AfspHeader> headerList;
    public void addHeader(AfspHeader header){
        if(headerList == null){
            headerList = new ArrayList<>();
        }
        headerList.add(header);
    }

    public List<AfspHeader> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<AfspHeader> headerList) {
        this.headerList = headerList;
    }
}
