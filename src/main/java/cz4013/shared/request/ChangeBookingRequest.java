/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.shared.request;

import java.util.UUID;

/**
 *
 * @author Dell
 */
public class ChangeBookingRequest {
    public String facilityName;
    public String id;
    public int offset;
    
    public ChangeBookingRequest(){
    }
    
    public ChangeBookingRequest(String facilityName, String id, int offset){
        this.facilityName = facilityName;
        this.id = id;
        this.offset = offset;
    }
}
