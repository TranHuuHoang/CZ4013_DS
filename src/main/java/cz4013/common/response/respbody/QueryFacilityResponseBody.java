package cz4013.common.response.respbody;

import java.util.ArrayList;

public class QueryFacilityResponseBody {
    public String facilityName;
    public ArrayList<Integer> availability;
    public boolean success;
    public String errorMessage;

    public QueryFacilityResponseBody() {
    }

    public QueryFacilityResponseBody(String facilityName, ArrayList<Integer> availability, boolean success, String errorMessage) {
        this.facilityName = facilityName;
        this.availability = availability;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static QueryFacilityResponseBody failed(String errorMessage) {
        return new QueryFacilityResponseBody("", new ArrayList<>(), false, errorMessage);
    }

}
