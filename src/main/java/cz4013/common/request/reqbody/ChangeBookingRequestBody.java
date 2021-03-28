package cz4013.common.request.reqbody;

public class ChangeBookingRequestBody {
    public String facilityName;
    public String id;
    public int offset;
    
    public ChangeBookingRequestBody(){
    }
    
    public ChangeBookingRequestBody(String facilityName, String id, int offset){
        this.facilityName = facilityName;
        this.id = id;
        this.offset = offset;
    }
}
