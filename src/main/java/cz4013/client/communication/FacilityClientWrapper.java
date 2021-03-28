package cz4013.client.communication;

import cz4013.common.request.reqbody.*;
import cz4013.common.response.*;
import cz4013.common.response.respbody.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

public class FacilityClientWrapper {
    private Client client;
    Scanner sc = new Scanner(System.in);
    
    public FacilityClientWrapper(Client client){
        this.client = client;
    }
    
    public void runAddFacility(){
        System.out.println("Enter the facility's name:");
        String name = sc.nextLine();
        
        AddFacilityResponseBody response = client.request(
            "addFacility",
            new AddFacilityRequestBody(name),
            new Response<AddFacilityResponseBody>() {}
        );
        
        System.out.printf("Successfully add facility: %s\n", response.facilityName);
    }
    
    public void runQueryFacility(){
        System.out.println("Enter the facility's name:");
        String name = sc.nextLine();
        System.out.println("Enter a day in week:");
        String day = sc.nextLine().toUpperCase();
        
        QueryFacilityResponseBody response = client.request(
            "queryFacility",
            new QueryFacilityRequestBody(name, day),
            new Response<QueryFacilityResponseBody>() {}
        );
        
        if (response.success) {
            System.out.printf("Successfully query the availability of %s:\n%s", response.facilityName, printAvailability(response.availability));
        } else {
            System.out.printf("Failed to query the availability of %s with reason: %s\n", response.facilityName, response.errorMessage);
        }
    }
    
    public void runBooking(){
        System.out.println("Enter the facility's name:");
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
        
        BookingResponseBody response = client.request(
            "booking",
            new BookingRequestBody(name, day, slot),
            new Response<BookingResponseBody>() {}
        );
        
        if (response.success) {
            System.out.printf("Successfully book this timeslot, confirmation ID: %s\n", response.id);
        } else {
            System.out.printf("Failed to book this slot with reason: %s\n", response.errorMessage);
        }
    }
    
    public void runChangeBooking(){
        System.out.println("Enter the facility's name:");
        String name = sc.nextLine();
        System.out.println("Enter booking confirmation ID:");
        String id = sc.nextLine();
        System.out.println("Choose how to change the booked slot:");
        System.out.println("1. Move 1 timeslot earlier");
        System.out.println("2. Move 1 timeslot later");
        int offset = sc.nextInt();

        if (offset != 1 && offset != 2){
            System.out.println("Failed to change this booking timeslot with reason: Invalid choice!\n");
            return;
        }

        ChangeBookingResponseBody response = client.request(
            "changeBooking",
            new ChangeBookingRequestBody(name, id, offset),
            new Response<ChangeBookingResponseBody>() {}
        );
        
        if (response.success) {
            System.out.printf("Successfully change the booking timeslot\n");
        } else {
            System.out.printf("Failed to change this booking timeslot with reason: %s\n", response.errorMessage);
        }
        
    }

    public void runCancelBooking(){
        System.out.println("Enter the facility's name:");
        String name = sc.nextLine();
        System.out.println("Enter booking confirmation ID:");
        String id = sc.nextLine();

        CancelBookingResponseBody response = client.request(
                "cancelBooking",
                new CancelBookingRequestBody(name, id),
                new Response<CancelBookingResponseBody>() {}
        );

        if (response.success) {
            System.out.printf("Successfully cancel the booking timeslot\n");
        } else {
            System.out.printf("Failed to change this booking timeslot with reason: %s\n", response.errorMessage);
        }

    }

    public void runShiftBooking(){
        System.out.println("Enter the facility's name:");
        String name = sc.nextLine();
        System.out.println("Enter booking confirmation ID:");
        String id = sc.nextLine();

        ShiftBookingResponseBody response = client.request(
                "shiftBooking",
                new ShiftBookingRequestBody(name, id),
                new Response<ShiftBookingResponseBody>() {}
        );

        if (response.success) {
            System.out.printf("Successfully shift the booking timeslot to the next day\n");
        } else {
            System.out.printf("Failed to change this booking timeslot with reason: %s\n", response.errorMessage);
        }

    }

    public void runMonitor() {
        System.out.println("Enter the facility's name:");
        String name = sc.nextLine();
        System.out.print("Monitor interval (s) = ");
        int interval = sc.nextInt();
        MonitorStatusResponseBody status = client.request(
            "monitor",
            new MonitorRequestBody(name, interval),
            new Response<MonitorStatusResponseBody>() {}
        );

        if (status.success) {
            System.out.println("Successfully requested to monitor, waiting for updates...");
            client.poll(
                new Response<MonitorUpdateResponseBody>() {},
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
