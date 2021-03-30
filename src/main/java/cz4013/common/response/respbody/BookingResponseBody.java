package cz4013.common.response.respbody;

import java.util.ArrayList;

public class BookingResponseBody {
    public String id;
    public ArrayList<Integer> availability;
    public boolean success;
    public String errorMessage;

    public BookingResponseBody(String id, ArrayList<Integer> availability, boolean success, String errorMessage) {
        this.id = id;
        this.availability = availability;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static BookingResponseBody failed(String errorMessage) {
        return new BookingResponseBody("", new ArrayList<>(), false, errorMessage);
    }
}
