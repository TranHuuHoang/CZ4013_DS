/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.client;

import cz4013.shared.request.AddFacilityRequest;
import cz4013.shared.request.BookingRequest;
import cz4013.shared.request.ChangeBookingRequest;
import cz4013.shared.request.MonitorRequest;
import cz4013.shared.request.QueryFacilityRequest;
import cz4013.shared.response.AddFacilityResponse;
import cz4013.shared.response.BookingResponse;
import cz4013.shared.response.ChangeBookingResponse;
import cz4013.shared.response.MonitorStatusResponse;
import cz4013.shared.response.MonitorUpdateResponse;
import cz4013.shared.response.QueryFacilityResponse;
import cz4013.shared.response.Response;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Dell
 */
public class FacilityClient {
    private Client client;
    Scanner sc = new Scanner(System.in);
    
    public FacilityClient(Client client){
        this.client = client;
    }
    
    public void runAddFacility(){
        System.out.println("Enter the facility's name:");
        //sc.nextLine(); //consuming \n from previous print
        String name = sc.nextLine();
        
        AddFacilityResponse response = client.request(
            "addFacility",
            new AddFacilityRequest(name),
            new Response<AddFacilityResponse>() {}
        );
        
        System.out.printf("Successfully add facility: %s\n", response.facilityName);
    }
    
    public void runQueryFacility(){
        System.out.println("Enter the facility's name:");
        sc.nextLine(); //consuming \n from previous print
        String name = sc.nextLine();
        System.out.println("Enter a day in week:");
        String day = sc.nextLine().toUpperCase();
        
        QueryFacilityResponse response = client.request(
            "queryFacility",
            new QueryFacilityRequest(name, day),
            new Response<QueryFacilityResponse>() {}
        );
        
        if (response.success) {
            System.out.printf("Successfully query the availability of %s:\n%s", response.facilityName, printAvailability(response.availability));
        } else {
            System.out.printf("Failed to query the availability of %s with reason: %s\n", response.facilityName, response.errorMessage);
        }
    }
    
    public void runBooking(){
        System.out.println("Enter the facility's name:");
        //sc.nextLine(); //consuming \n from previous print
        String name = sc.nextLine();
        System.out.println("Enter a day in week:");
        String day = sc.nextLine().toUpperCase();
        System.out.println("Enter 1-5 to choose the booking time");
        System.out.println("1. 08:00 - 10:00");
        System.out.println("2. 10:00 - 12:00");
        System.out.println("3. 12:00 - 14:00");
        System.out.println("4. 14:00 - 16:00");
        System.out.println("5. 16:00 - 18:00");
        int slot = sc.nextInt();
        
        BookingResponse response = client.request(
            "booking",
            new BookingRequest(name, day, slot),
            new Response<BookingResponse>() {}
        );
        
        if (response.success) {
            System.out.printf("Successfully book this timeslot, confirmation ID: %s\n", response.id);
        } else {
            System.out.printf("Failed to book this slot with reason: %s\n", response.errorMessage);
        }
    }
    
    public void runChangeBooking(){
        System.out.println("Enter the facility's name:");
        sc.nextLine(); //consuming \n from previous prints
        String name = sc.nextLine();
        System.out.println("Enter booking confirmation ID:");
        //sc.nextLine(); //consuming \n from previous print
        String id = sc.nextLine();
        System.out.println("Choose how to change the booked slot:");
        System.out.println("1. Move 1 timeslot earlier");
        System.out.println("2. Move 1 timeslot later");
        int offset = sc.nextInt();
        
        ChangeBookingResponse response = client.request(
            "changeBooking",
            new ChangeBookingRequest(name, id, offset),
            new Response<ChangeBookingResponse>() {}
        );
        
        if (response.success) {
            System.out.printf("Successfully change the booking timeslot\n");
        } else {
            System.out.printf("Failed to change this booking timeslot with reason: %s\n", response.errorMessage);
        }
        
    }
    
    public void runMonitor() {
        System.out.println("Enter the facility's name:");
        //sc.nextLine(); //consuming \n from previous print
        String name = sc.nextLine();
        System.out.print("Monitor interval (s) = ");
        int interval = sc.nextInt();
        MonitorStatusResponse status = client.request(
            "monitor",
            new MonitorRequest(name, interval),
            new Response<MonitorStatusResponse>() {}
        );

        if (status.success) {
            System.out.println("Successfully requested to monitor, waiting for updates...");
            client.poll(
                new Response<MonitorUpdateResponse>() {},
                Duration.ofSeconds(interval),
                update -> System.out.println("Update: " + update.info)
            );
            System.out.println("Finished monitoring.");
        } else {
            System.out.println("Failed to request to monitor");
        }
    }

    public String printAvailability(ArrayList<Integer> availability){
        String result = "";
        for (int i: availability){
            // i=1 => 08:00 - 10:00  etc.
            result = result + String.format("%02d:%02d - %02d:%02d\n", 8 + (i-1)*2 , 0, 8 + i*2, 0);
        }
        return result;
    }

}
