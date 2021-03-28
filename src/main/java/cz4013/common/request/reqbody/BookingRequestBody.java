package cz4013.common.request.reqbody;

public class BookingRequestBody {
    public String facilityName;
    public String day;
    public int timeslot;
    
    public BookingRequestBody(){
    }
    
    public BookingRequestBody(String facilityName, String day, int timeslot){
        this.facilityName = facilityName;
        this.day = day;
        this.timeslot = timeslot;
    }
}
