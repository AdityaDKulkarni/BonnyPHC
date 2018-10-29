package com.bonny.bonnyphc.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bonny.bonnyphc.ui.activities.LoginActivity;

import java.util.HashMap;

/**
 * @author Aditya Kulkarni
 */

public class SessionManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    String PREFERENCE_NAME = "Login Preference";
    String IS_LOGGED_IN = "IsLoggedIn";
    String IS_FIRST_TIME_LAUNCHED = "isFirstTimeLaunched";

    public SessionManager(Context ctx) {
        this.context = ctx;
        preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        editor = preferences.edit();
    }

    public void createLoginSession(String username, String key) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString("username", username);
        editor.putString("key", key);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put("username", preferences.getString("username", null));
        user.put("key", preferences.getString("key", null));
        return user;
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }

    public void setIsFirstTimeLaunch(boolean isFirstTimeLaunch){
        editor.putBoolean(IS_FIRST_TIME_LAUNCHED, isFirstTimeLaunch);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCHED, true);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }

    public void logOutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
