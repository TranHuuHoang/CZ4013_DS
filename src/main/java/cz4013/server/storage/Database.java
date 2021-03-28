package cz4013.server.storage;

import cz4013.server.entity.Facility;

import java.util.HashMap;

public class Database {
    private HashMap<String, Facility> db = new HashMap<>();

    public void store(String facilityName, Facility facility) {
        db.put(facilityName, facility);
    }
  
    public void delete(String facilityName) {
        db.remove(facilityName);
    }

    public Facility query(String facilityName) {
        return db.get(facilityName);
    }
}
