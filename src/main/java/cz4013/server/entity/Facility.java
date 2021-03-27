/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz4013.server.entity;

import java.util.*;

/**
 *
 * @author Dell
 */
public class Facility {
    private String name;
    private HashMap<Day, ArrayList<Integer>> availability;
    private HashMap<String, Object[]> bookings;
    
    public Facility(String name, HashMap<Day, ArrayList<Integer>> defaultTimeSlot){
        this.name = name;
        this.availability = defaultTimeSlot;
        this.bookings = new HashMap<>();
    }
    
    public String getName(){
        return this.name;
    }
    
    public ArrayList<Integer> getAvailability(String day){
        return this.availability.get(Day.valueOf(day));
    }
    
    public void bookTimeslot(String id, String day, int timeslot){
        if(!this.getAvailability(day).contains(timeslot)){
            return;
        }
        else{
            this.getAvailability(day).remove(new Integer(timeslot));
            this.bookings.put(id, new Object[]{day, timeslot});
        }
    }
    
    public HashMap<String, Object[]> getBooking(){
        return this.bookings;
    }
    
    public void changeBooking(String id, int offset){
        String day = (String)this.bookings.get(id)[0];
        int timeslot = (int)this.bookings.get(id)[1];
        // Offset = 1 => advance 1 timeslot
        // Offset = 2 => postpone 1 timeslot
        int newTimeslot = timeslot + (2*offset - 3);
        
        if(!this.getAvailability(day).contains(newTimeslot)){
            return;
        }
        else{
            this.getAvailability(day).remove(new Integer(newTimeslot));
            this.getAvailability(day).add(timeslot);
            this.bookings.put(id, new Object[]{day, newTimeslot}); 
        }

        Collections.sort(getAvailability(day));
    }

    public void shiftBooking(String id){
        String day = (String)this.bookings.get(id)[0];
        int timeslot = (int)this.bookings.get(id)[1];
        // Offset = 1 => advance 1 timeslot
        // Offset = 2 => postpone 1 timeslot
        List<String> weekDays = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        int currentDayIdx = weekDays.indexOf(day);
        String nextDay = weekDays.get((currentDayIdx + 1) % 7);

        if(!this.getAvailability(nextDay).contains(timeslot)){
            return;
        }
        else{
            this.getAvailability(nextDay).remove(new Integer(timeslot));
            this.getAvailability(day).add(timeslot);
            this.bookings.put(id, new Object[]{nextDay, timeslot});
        }

        Collections.sort(getAvailability(day));
        Collections.sort(getAvailability(nextDay));
    }

    public void cancelBooking(String id){
        String day = (String)this.bookings.get(id)[0];
        int timeslot = (int)this.bookings.get(id)[1];

        getAvailability(day).add(timeslot);
        this.bookings.remove(id);

        Collections.sort(getAvailability(day));
    }
    
    public String stringFormat(int timeslot){
        return String.format("%02d:%02d - %02d:%02d", 8 + (timeslot-1)*2 , 0, 8 + timeslot*2, 0);
    }
    
    public void printAvailability(String day){
        for(Integer i: getAvailability(day)){
            System.out.println(stringFormat(i));
        }
    }
}
