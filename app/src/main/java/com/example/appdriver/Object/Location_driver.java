package com.example.appdriver.Object;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Location_driver {
    GeoPoint location;
    String driver_ref;
    Timestamp created_at;
    String token;

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getDriver_ref() {
        return driver_ref;
    }

    public void setDriver_ref(String driver_ref) {
        this.driver_ref = driver_ref;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    Boolean status;

    public Location_driver(GeoPoint location, String driver_ref, Timestamp created_at, String token, Boolean status) {
        this.location = location;
        this.driver_ref = driver_ref;
        this.created_at = created_at;
        this.token = token;
        this.status = status;
    }
}