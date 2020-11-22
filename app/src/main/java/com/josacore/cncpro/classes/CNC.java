package com.josacore.cncpro.classes;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class CNC{

    private String id;
    private String uid;
    private String name;
    private String brand;
    private String photo;
    private boolean state;
    private boolean connected;

    private Map<String, Object> userAllowed = new HashMap<>();

    public CNC() {
    }

    public CNC(String id, String uid, String name, String brand, String photo, boolean state, boolean connected, Map<String, Object> userAllowed) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.brand = brand;
        this.photo = photo;
        this.state = state;
        this.connected = connected;
        this.userAllowed = userAllowed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Map<String, Object> getUserAllowed() {
        return userAllowed;
    }

    public void setUserAllowed(Map<String, Object> userAllowed) {
        this.userAllowed = userAllowed;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("uid", uid);
        result.put("name", name);
        result.put("brand", brand);
        result.put("photo", photo);
        result.put("state", state);
        result.put("connected", connected);
        result.put("userAllowed", userAllowed);

        return result;
    }
}