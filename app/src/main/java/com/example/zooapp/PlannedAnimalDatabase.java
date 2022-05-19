package com.example.zooapp;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {ZooNode.class}, version = 1)
public abstract class PlannedAnimalDatabase extends RoomDatabase{

    private static PlannedAnimalDatabase singleton = null;
    public abstract PlannedAnimalDao plannedAnimalDao();

    public synchronized static PlannedAnimalDatabase getSingleton(Context context) {
        if( singleton == null ) {
            singleton = PlannedAnimalDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static PlannedAnimalDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, PlannedAnimalDatabase.class, "planned_list.db")
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        List<ZooNode> plannedList = new ArrayList<>();
//                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
//                            List<ZooNode> plannedList = new ArrayList<>();
//                            //getSingleton(context).plannedAnimalDao().
//                        });
                    }
                })
                .build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(PlannedAnimalDatabase testDatabase) {
        if( singleton != null ) {
            singleton.close();
        }
        singleton = testDatabase;
    }

}
