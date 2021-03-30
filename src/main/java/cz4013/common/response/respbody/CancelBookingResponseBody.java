package cz4013.common.response.respbody;

public class CancelBookingResponseBody {
    public boolean success;
    public String errorMessage;

    public CancelBookingResponseBody(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CancelBookingResponseBody failed(String errorMessage) {
        return new CancelBookingResponseBody(false, errorMessage);
    }
}
