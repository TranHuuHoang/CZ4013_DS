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
public class AddFacilityResponse {
    public String facilityName;
    
    public AddFacilityResponse(){
    }
    
    public AddFacilityResponse(String facilityName){
        this.facilityName = facilityName;
    }
}
