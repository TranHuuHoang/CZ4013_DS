package cz4013.server.storage;

import cz4013.server.entity.Facility;

import java.util.HashMap;

/**
 * An in-memory key-value based database which holds bank accounts records.
 * key = account number
 * value = account detail
 */
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
