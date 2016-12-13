package com.example.j0nee.mini;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by j0nee on 7/12/16.
 */
@IgnoreExtraProperties
public class AppData {


    public String appName;
    public String hashValue;

    public AppData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AppData(String appName, String hashValue) {
        this.appName = appName;
        this.hashValue = hashValue;
    }

}