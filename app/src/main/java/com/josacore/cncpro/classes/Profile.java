package com.josacore.cncpro.classes;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by santiago on 2/15/18.
 */

@IgnoreExtraProperties
public class Profile {

    public String uid;
    public String identityCard;
    public String firstName;
    public String lastName;
    public String photo;
    public String nationality;
    public String phone;

    public Profile() {
    }

    public Profile(String uid, String identityCard, String firstName, String lastName, String photo, String nationality,String phone) {
        this.uid = uid;
        this.identityCard = identityCard;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.nationality = nationality;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("identityCard", identityCard);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("photo", photo);
        result.put("nationality", nationality);
        result.put("phone", phone);
        return result;
    }
}
