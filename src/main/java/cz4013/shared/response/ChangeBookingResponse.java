/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.shared.response;

import cz4013.server.entity.Day;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Dell
 */
public class ChangeBookingResponse {
    //public HashMap<Day, ArrayList<Integer>> availability;
    public boolean success;
    public String errorMessage;

    public ChangeBookingResponse() {
    }

    public ChangeBookingResponse(boolean success, String errorMessage) {
        //this.availability = availability;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ChangeBookingResponse failed(String errorMessage) {
        return new ChangeBookingResponse(false, errorMessage);
    }
}
