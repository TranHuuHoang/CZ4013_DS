/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.shared.response;

/**
 *
 * @author Dell
 */
public class CancelBookingResponse {
    //public HashMap<Day, ArrayList<Integer>> availability;
    public boolean success;
    public String errorMessage;

    public CancelBookingResponse() {
    }

    public CancelBookingResponse(boolean success, String errorMessage) {
        //this.availability = availability;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CancelBookingResponse failed(String errorMessage) {
        return new CancelBookingResponse(false, errorMessage);
    }
}
