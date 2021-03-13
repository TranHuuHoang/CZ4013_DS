/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.shared.response;

import cz4013.server.entity.Day;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Dell
 */
public class BookingResponse {
    public String id;
    public ArrayList<Integer> availability;
    public boolean success;
    public String errorMessage;

    public BookingResponse() {
    }

    public BookingResponse(String id, ArrayList<Integer> availability, boolean success, String errorMessage) {
        this.id = id;
        this.availability = availability;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static BookingResponse failed(String errorMessage) {
        return new BookingResponse("", new ArrayList<>(), false, errorMessage);
    }
}
