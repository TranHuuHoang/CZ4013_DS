package cz4013.common.request.reqbody;

public class CancelBookingRequestBody {
    public String facilityName;
    public String id;

    public CancelBookingRequestBody(){
    }

    public CancelBookingRequestBody(String facilityName, String id){
        this.facilityName = facilityName;
        this.id = id;
    }
}
