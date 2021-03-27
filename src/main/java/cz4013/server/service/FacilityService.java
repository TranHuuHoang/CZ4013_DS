/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.server.service;

import cz4013.server.entity.Day;
import cz4013.server.entity.Facility;
import cz4013.server.storage.Database;
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
import cz4013.shared.response.ResponseHeader;
import cz4013.shared.response.Status;
import cz4013.shared.rpc.Transport;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author Dell
 */
public class FacilityService {
    private Database db = new Database();
    private Transport transport;
    private Map<SocketAddress, Instant> listeners = new HashMap<>();
    
    public FacilityService(Transport transport){
        this.transport = transport;
    }
    
    public AddFacilityResponse processAddFacility(AddFacilityRequest request){
        // Initialize timeslots
        HashMap<Day, ArrayList<Integer>> initialAvailability = new HashMap<>();
        ArrayList<Integer> listTimeSlot = new ArrayList<>();

        for (Day day : Day.values()){
            for(int i = 1; i <= 5; i++){
                listTimeSlot.add(i);
            }
            initialAvailability.put(day, listTimeSlot);
            listTimeSlot = new ArrayList<>();
        }
        
        db.store(
            request.facilityName,
            new Facility(
                request.facilityName,
                initialAvailability
            )
        );
        
        broadcast(String.format("New facility: %s is added", request.facilityName));
        return new AddFacilityResponse(request.facilityName);
    }
    
    public QueryFacilityResponse processQueryFacility(QueryFacilityRequest request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return QueryFacilityResponse.failed("This facility does not exist!");
        }
        broadcast(String.format("Someone queries facility %s: %s availability", request.facilityName, request.day));
        return new QueryFacilityResponse(request.facilityName, facility.getAvailability(request.day), true, "");
    }
    
    public BookingResponse processBooking(BookingRequest request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return BookingResponse.failed("This facility does not exist!");
        }
        if (!facility.getAvailability(request.day).contains(request.timeslot)){
            return BookingResponse.failed("This timeslot is not available!");
        }
        
        String id = UUID.randomUUID().toString();
        
        facility.bookTimeslot(id, request.day, request.timeslot);
        
        db.store(
            request.facilityName,
            facility
        );
        broadcast(String.format("Booking successful! Confirmation ID: %s", id));
        return new BookingResponse(id, facility.getAvailability(request.day), true, "");
    }
    
    public ChangeBookingResponse processChangeBooking(ChangeBookingRequest request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return ChangeBookingResponse.failed("This facility does not exist!");
        }
        if (!facility.getBooking().containsKey(request.id)){
            return ChangeBookingResponse.failed("Invalid confirmation ID!");
        }
        
        String day = (String)facility.getBooking().get(request.id)[0];
        int timeslot = (int)facility.getBooking().get(request.id)[1];
        // Offset = 1 => advance 1 timeslot
        // Offset = 2 => postpone 1 timeslot
        int newTimeslot = timeslot + (2*request.offset - 3);
        
        if (!facility.getAvailability(day).contains(newTimeslot)){
            return ChangeBookingResponse.failed("This timeslot is not available!");
        }
        
        facility.changeBooking(request.id, request.offset);
        db.store(
            request.facilityName,
            facility
        );
        broadcast(String.format("Change booking timeslot successful! New booking slot at: %s", facility.stringFormat(newTimeslot)));
        return new ChangeBookingResponse(true, "");
    }
    
    public MonitorStatusResponse processMonitor(MonitorRequest req, SocketAddress remote) {
        long interval = req.interval;
        listeners.put(remote, Instant.now().plusSeconds(interval));
        System.out.printf("User at %s starts to monitor for %d seconds\n", remote, interval);
        return new MonitorStatusResponse(true);
    }
    
    private void broadcast(String info) {
        purgeListeners();
        System.out.println(info);
        Response<MonitorUpdateResponse> resp = new Response<>(
            new ResponseHeader(UUID.randomUUID(), Status.OK),
            Optional.of(new MonitorUpdateResponse(info))
        );
        listeners.forEach((socketAddress, x) -> {
            transport.send(socketAddress, resp);
        });
    }

    private void purgeListeners() {
        listeners.entrySet().removeIf(x -> x.getValue().isBefore(Instant.now()));
    }
}
