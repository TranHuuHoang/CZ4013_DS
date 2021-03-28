package cz4013.common.request.reqbody;

public class QueryFacilityRequestBody {
    public String facilityName;
    public String day;

    public QueryFacilityRequestBody() {
    }

    public QueryFacilityRequestBody(String facilityName, String day) {
        this.facilityName = facilityName;
        this.day = day;
    }
  
}
