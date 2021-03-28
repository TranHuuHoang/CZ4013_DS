package cz4013.common.request.reqbody;

public class ShiftBookingRequestBody {
    public String facilityName;
    public String id;

    public ShiftBookingRequestBody(){
    }

    public ShiftBookingRequestBody(String facilityName, String id){
        this.facilityName = facilityName;
        this.id = id;
    }
}
