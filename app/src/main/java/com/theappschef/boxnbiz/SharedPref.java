package com.theappschef.boxnbiz;



import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Set;


public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("offline_data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public String getOff() {
        return sharedPreferences.getString("ss", "");
    }

    public void setOff(String choice) {
        editor.putString("ss", choice);
        editor.apply();
    }
    public String getEmail() {
        return sharedPreferences.getString("ems", "");
    }

    public void setEmail(String choice) {
        editor.putString("ems", choice);
        editor.apply();
    }


}
