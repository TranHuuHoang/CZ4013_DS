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
public class DeleteFacilityResponse {
    public boolean success;
    public String errorMessage;

    public DeleteFacilityResponse() {
    }

    public DeleteFacilityResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static DeleteFacilityResponse failed(String errorMessage) {
        return new DeleteFacilityResponse(false, errorMessage);
    }
}
