/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.shared.response;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Dell
 */
public class QueryFacilityResponse {
    public String facilityName;
    public ArrayList<Integer> availability;
    public boolean success;
    public String errorMessage;

    public QueryFacilityResponse() {
    }

    public QueryFacilityResponse(String facilityName, ArrayList<Integer> availability, boolean success, String errorMessage) {
        this.facilityName = facilityName;
        this.availability = availability;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static QueryFacilityResponse failed(String errorMessage) {
        return new QueryFacilityResponse("", new ArrayList<>(), false, errorMessage);
    }

}
