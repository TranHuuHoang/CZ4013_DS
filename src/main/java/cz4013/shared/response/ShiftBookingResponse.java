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
public class ShiftBookingResponse {
    //public HashMap<Day, ArrayList<Integer>> availability;
    public boolean success;
    public String errorMessage;

    public ShiftBookingResponse() {
    }

    public ShiftBookingResponse(boolean success, String errorMessage) {
        //this.availability = availability;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ShiftBookingResponse failed(String errorMessage) {
        return new ShiftBookingResponse(false, errorMessage);
    }
}
