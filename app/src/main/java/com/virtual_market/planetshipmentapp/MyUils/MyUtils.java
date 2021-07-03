package com.virtual_market.planetshipmentapp.MyUils;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MyUtils {

    private static HashMap<String,String> map=null;

    public static void createToast(Context context,String value){

        Toast.makeText(context, ""+value, Toast.LENGTH_SHORT).show();

    }

    public static HashMap<String, String> setHashmap(String key, String value) {
        map.put(key,value);
        return map;

    }

    public static void clearAllMap() {

        map=new HashMap<String,String>();

    }
}
