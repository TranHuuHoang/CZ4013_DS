package cz4013.server.service;

import cz4013.server.entity.Day;
import cz4013.server.entity.Facility;
import cz4013.server.storage.Database;
import cz4013.common.request.reqbody.*;
import cz4013.common.response.*;
import cz4013.common.response.respbody.*;
import cz4013.common.rpc.Transport;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.*;

public class FacilityService {
    private Database db = new Database();
    private Transport transport;
    private Map<SocketAddress, Instant> listeners = new HashMap<>();
    
    public FacilityService(Transport transport){
        this.transport = transport;
    }
    
    public AddFacilityResponseBody processAddFacility(AddFacilityRequestBody request){
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
        return new AddFacilityResponseBody(request.facilityName);
    }
    
    public QueryFacilityResponseBody processQueryFacility(QueryFacilityRequestBody request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return QueryFacilityResponseBody.failed("This facility does not exist!");
        }
        broadcast(String.format("Someone queries facility %s: %s availability", request.facilityName, request.day));
        return new QueryFacilityResponseBody(request.facilityName, facility.getAvailability(request.day), true, "");
    }
    
    public BookingResponseBody processBooking(BookingRequestBody request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return BookingResponseBody.failed("This facility does not exist!");
        }
        if (!facility.getAvailability(request.day).contains(request.timeslot)){
            return BookingResponseBody.failed("This timeslot is not available!");
        }
        
        String id = UUID.randomUUID().toString();
        
        facility.bookTimeslot(id, request.day, request.timeslot);
        
        db.store(
            request.facilityName,
            facility
        );
        broadcast(String.format("Booking successful! Confirmation ID: %s", id));
        return new BookingResponseBody(id, facility.getAvailability(request.day), true, "");
    }
    
    public ChangeBookingResponseBody processChangeBooking(ChangeBookingRequestBody request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return ChangeBookingResponseBody.failed("This facility does not exist!");
        }
        if (!facility.getBooking().containsKey(request.id)){
            return ChangeBookingResponseBody.failed("Invalid confirmation ID!");
        }
        
        String day = (String)facility.getBooking().get(request.id)[0];
        int timeslot = (int)facility.getBooking().get(request.id)[1];
        // Offset = 1 => advance 1 timeslot
        // Offset = 2 => postpone 1 timeslot
        int newTimeslot = timeslot + (2*request.offset - 3);
        
        if (!facility.getAvailability(day).contains(newTimeslot)){
            return ChangeBookingResponseBody.failed("This timeslot is not available!");
        }
        
        facility.changeBooking(request.id, request.offset);
        db.store(
            request.facilityName,
            facility
        );
        broadcast(String.format("Change booking timeslot successful! New booking slot at: %s", facility.stringFormat(newTimeslot)));
        return new ChangeBookingResponseBody(true, "");
    }

    public CancelBookingResponseBody processCancelBooking(CancelBookingRequestBody request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return CancelBookingResponseBody.failed("This facility does not exist!");
        }
        if (!facility.getBooking().containsKey(request.id)){
            return CancelBookingResponseBody.failed("Invalid confirmation ID!");
        }

        int timeslot = (int)facility.getBooking().get(request.id)[1];

        facility.cancelBooking(request.id);
        db.store(
                request.facilityName,
                facility
        );
        broadcast(String.format("Change booking timeslot successful! The booking slot at: %s is now free", facility.stringFormat(timeslot)));
        return new CancelBookingResponseBody(true, "");
    }

    public ShiftBookingResponseBody processShiftBooking(ShiftBookingRequestBody request){
        Facility facility = db.query(request.facilityName);
        if (facility == null){
            return ShiftBookingResponseBody.failed("This facility does not exist!");
        }
        if (!facility.getBooking().containsKey(request.id)){
            return ShiftBookingResponseBody.failed("Invalid confirmation ID!");
        }

        String day = (String)facility.getBooking().get(request.id)[0];
        int timeslot = (int)facility.getBooking().get(request.id)[1];

        List<String> weekDays = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        int currentDayIdx = weekDays.indexOf(day);
        String nextDay = weekDays.get((currentDayIdx + 1) % 7);

        if(!facility.getAvailability(nextDay).contains(timeslot)){
            return ShiftBookingResponseBody.failed("This timeslot is not available in the next day!");
        }

        facility.shiftBooking(request.id);
        db.store(
                request.facilityName,
                facility
        );
        broadcast(String.format("Successfully shift booking timeslot at: %s of %s to %s", facility.stringFormat(timeslot), day, nextDay));
        return new ShiftBookingResponseBody(true, "");
    }
    
    public MonitorStatusResponseBody processMonitor(MonitorRequestBody req, SocketAddress remote) {
        long interval = req.interval;
        listeners.put(remote, Instant.now().plusSeconds(interval));
        System.out.printf("User at %s starts to monitor for %d seconds\n", remote, interval);
        return new MonitorStatusResponseBody(true);
    }
    
    private void broadcast(String info) {
        purgeListeners();
        System.out.println(info);
        Response<MonitorUpdateResponseBody> resp = new Response<>(
            new ResponseHeader(UUID.randomUUID(), ResponseStatus.OK),
            Optional.of(new MonitorUpdateResponseBody(info))
        );
        listeners.forEach((socketAddress, x) -> {
            transport.send(socketAddress, resp);
        });
    }

    private void purgeListeners() {
        listeners.entrySet().removeIf(x -> x.getValue().isBefore(Instant.now()));
    }
}
