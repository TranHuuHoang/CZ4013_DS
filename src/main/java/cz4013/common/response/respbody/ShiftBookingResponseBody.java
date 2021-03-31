package cz4013.common.response.respbody;

public class ShiftBookingResponseBody {
    public boolean success;
    public String errorMessage;

    public ShiftBookingResponseBody() {
    }

    public ShiftBookingResponseBody(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ShiftBookingResponseBody failed(String errorMessage) {
        return new ShiftBookingResponseBody(false, errorMessage);
    }
}
