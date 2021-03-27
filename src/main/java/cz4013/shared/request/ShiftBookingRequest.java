/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.shared.request;

/**
 *
 * @author Dell
 */
public class ShiftBookingRequest {
    public String facilityName;
    public String id;

    public ShiftBookingRequest(){
    }

    public ShiftBookingRequest(String facilityName, String id){
        this.facilityName = facilityName;
        this.id = id;
    }
}
