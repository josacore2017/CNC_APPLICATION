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
    private String userUsing;
    private long time;
    private String lastTime;
    private boolean state;
    private boolean connected;

    private Map<String, Object> userAllowed = new HashMap<>();

    public CNC() {
    }

    public CNC(String id, String uid, String name, String brand, String photo, String userUsing, long time, String lastTime, boolean state, boolean connected, Map<String, Object> userAllowed) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.brand = brand;
        this.photo = photo;
        this.userUsing = userUsing;
        this.time = time;
        this.lastTime=lastTime;
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

    public String getUserUsing() {
        return userUsing;
    }

    public void setUserUsing(String userUsing) {
        this.userUsing = userUsing;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("uid", uid);
        result.put("name", name);
        result.put("brand", brand);
        result.put("photo", photo);
        result.put("userUsing", userUsing);
        result.put("time", time);
        result.put("lastTime", lastTime);
        result.put("state", state);
        result.put("connected", connected);
        result.put("userAllowed", userAllowed);

        return result;
    }
}