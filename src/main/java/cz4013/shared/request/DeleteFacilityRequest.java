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
public class DeleteFacilityRequest {
    public String facilityName;
    
    public DeleteFacilityRequest(){
    }
    
    public DeleteFacilityRequest(String facilityName){
        this.facilityName = facilityName;
    }
    
}
