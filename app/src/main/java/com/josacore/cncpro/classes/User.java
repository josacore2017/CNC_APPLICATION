package com.josacore.cncpro.classes;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by santiago on 2/9/18.
 */


@IgnoreExtraProperties
public class User{

    public String username;
    public String email;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


}