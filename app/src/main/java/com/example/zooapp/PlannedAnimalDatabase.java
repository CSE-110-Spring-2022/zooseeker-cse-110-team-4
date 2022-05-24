package com.example.zooapp;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {ZooNode.class}, version = 1)
public abstract class PlannedAnimalDatabase extends RoomDatabase{
    private static PlannedAnimalDatabase singleton = null;

//    static final Migration MIGRATION_1_2 = new Migration(2, 1) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE new_zoo_list (name TEXT, id TEXT NOT NULL, parent_id TEXT," +
//                    "value INTEGER NOT NULL, kind TEXT, tags TEXT, lat TEXT, lng TEXT, PRIMARY KEY(value))");
//
//            database.execSQL("DROP TABLE zoo_node_list");
//
//            database.execSQL("ALTER TABLE new_zoo_list RENAME TO zoo_node_list");
//        }
//    };

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
