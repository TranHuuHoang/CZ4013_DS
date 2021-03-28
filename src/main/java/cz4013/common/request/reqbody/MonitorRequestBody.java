package cz4013.common.request.reqbody;

public class MonitorRequestBody {
    public String facilityName;
    public int interval;

    public MonitorRequestBody() {
    }

    public MonitorRequestBody(String facilityName, int interval) {
        this.facilityName = facilityName;
        this.interval = interval;
    }

}
