package com.virtual_market.planetshipmentapp.MyUils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class MySharedPreferences {

    private static final String KEY_PREFERENCE_NAME = "VIRTUAL MARKET";
    public static final String oneSignalUserId="one_signal_user_id";
    public static final String user_id="user_id";
    public static final String driver_id="driver_id";
    public static final String token="token";
    public static final String name="name";
    public static final String email="email";
    public static final String mapsKey="mapsKey";
    public static final String myLatitude="Mylatitude";
    public static final String myLongitude="Mylongitude";
    public static final String uid="uid";
    public static final String id="id";
    public static final String isUpdate ="isUpdate";
    public static final String shippedOrDelivered ="shippedOrDelivered";
    public static final String isUpdateImages ="isUpdateImages";

    //instance field

    private static SharedPreferences mSharedPreference;
    private static MySharedPreferences mInstance = null;
    private static Context mContext;
    private final SharedPreferences.Editor editor;

    public MySharedPreferences() {
        mSharedPreference = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreference.edit();
    }

    public static void removeValueFromKey(String keyname) {
        mSharedPreference.edit().remove(keyname).apply();
    }

    public static void removeAllData() {

        SharedPreferences preferences = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = preferences.getAll();
        SharedPreferences.Editor editor = preferences.edit();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

            if(!entry.getKey().contains(oneSignalUserId)) {
                editor.remove(entry.getKey());
            }

        }

        editor.apply();

    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    public void clearAll() {

        editor.clear();

    }

    //Method to set boolean for (AppIntro)
    public void setBooleanKey(String keyname) {
        mSharedPreference.edit().putBoolean(keyname, true).apply();
    }

    public void setBooleanKey(String keyname, boolean state) {
        mSharedPreference.edit().putBoolean(keyname, state).apply();
    }

    /*
     * Method to get boolan key
     * true = means set
     * false = not set (show app intro)
     * */
    public boolean getBooleanKey(String keyname) {
        return mSharedPreference.getBoolean(keyname, false);
    }

    public void setStringKey(String keyname, String state) {
        mSharedPreference.edit().putString(keyname,state).apply();
    }

    public void setLongKey(String keyname, long state) {
        mSharedPreference.edit().putLong(keyname,state).apply();
    }

    public long getLongKey(String keyname) {
        return mSharedPreference.getLong(keyname,0);
    }


    /*
     * Method to get boolan key
     * true = means set
     * false = not set (show app intro)
     * */
    public String getStringkey(String keyname) {
        return mSharedPreference.getString(keyname, "");
    }

    public void setIntKey(String keyname, int state) {
        mSharedPreference.edit().putInt(keyname,state).apply();
    }

    /*
     * Method to get boolan key
     * true = means set
     * false = not set (show app intro)
     * */
    public int getIntkey(String keyname) {
        return mSharedPreference.getInt(keyname, 0);
    }

}

