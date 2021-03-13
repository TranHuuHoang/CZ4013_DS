package cz4013.shared.request;

/**
 * The request to monitor updates from other clients.
 */
public class MonitorRequest {
    public String facilityName;
    public int interval;

    public MonitorRequest() {
    }

    public MonitorRequest(String facilityName, int interval) {
        this.facilityName = facilityName;
        this.interval = interval;
    }

}
