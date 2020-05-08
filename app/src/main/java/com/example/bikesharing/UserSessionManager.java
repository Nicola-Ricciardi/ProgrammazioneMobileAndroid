package com.example.bikesharing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.HashMap;

public class UserSessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREFER_NAME = "BikeSharingPref";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID_USER = "idUser";

    public UserSessionManager (Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createUserLoginSession(String email, String idUser)
    {

        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ID_USER, idUser);
        editor.commit();
    }


    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL,null));
        user.put(KEY_ID_USER, pref.getString(KEY_ID_USER, null));
        return user;
    }


    public void logoutUser()
    {
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }



    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
