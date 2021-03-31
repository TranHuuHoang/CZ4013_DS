package cz4013.common.response.respbody;

public class ChangeBookingResponseBody {
    public boolean success;
    public String errorMessage;

    public ChangeBookingResponseBody() {
    }

    public ChangeBookingResponseBody(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ChangeBookingResponseBody failed(String errorMessage) {
        return new ChangeBookingResponseBody(false, errorMessage);
    }
}
