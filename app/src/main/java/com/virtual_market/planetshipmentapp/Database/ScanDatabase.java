package com.virtual_market.planetshipmentapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.virtual_market.planetshipmentapp.Modal.ScanModel;
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel;

@Database(entities = {ScanModel.class, SerialProductListModel.class}, version = 3)
public abstract class ScanDatabase extends RoomDatabase {

    private static final String DB_NAME="scan_db";
    private static ScanDatabase instanse;

    public static synchronized ScanDatabase getInstance(Context context){
        if(instanse==null){

            instanse= Room.databaseBuilder(context.getApplicationContext(),ScanDatabase.class,DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instanse;
    }

    public abstract ScanDao scanDao();

    public abstract SerialDao serialDao();

}
