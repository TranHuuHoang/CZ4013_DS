/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.shared.request;

import cz4013.server.entity.Day;

/**
 *
 * @author Dell
 */
public class BookingRequest {
    public String facilityName;
    public String day;
    public int timeslot;
    
    public BookingRequest(){
    }
    
    public BookingRequest(String facilityName, String day, int timeslot){
        this.facilityName = facilityName;
        this.day = day;
        this.timeslot = timeslot;
    }
}
