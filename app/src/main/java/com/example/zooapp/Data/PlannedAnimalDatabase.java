package com.example.zooapp.Data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zooapp.Interface.PlannedAnimalDao;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates the database to store the list of planned animals
 */
@Database(entities = {ZooNode.class}, version = 2)
public abstract class PlannedAnimalDatabase extends RoomDatabase{

    //Public fields
    public abstract PlannedAnimalDao plannedAnimalDao();

    //Private fields
    private static PlannedAnimalDatabase singleton = null;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE new_zoo_list (name TEXT, id TEXT NOT NULL, group_id TEXT," +
                    "value INTEGER NOT NULL, kind TEXT, tags TEXT, lat TEXT, lng TEXT, PRIMARY KEY(value))");

            database.execSQL("DROP TABLE zoo_node_list");

            database.execSQL("ALTER TABLE new_zoo_list RENAME TO zoo_node_list");
        }
    };

    /**
     * Creates a new Room database if one has not yet been created
     *
     * @param Context
     * @return PlannedAnimalDatabase the created or existing database
     */
    public synchronized static PlannedAnimalDatabase getSingleton(Context context) {
        if( singleton == null ) {
            singleton = PlannedAnimalDatabase.makeDatabase(context);
        }
        return singleton;
    }

    /**
     * Creates a new Room database to store the list of planned animals
     *
     * @param PlannedAnimalDatabase
     * @return PlannedAnimalDatabase the created database
     */
    private static PlannedAnimalDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, PlannedAnimalDatabase.class, "planned_list.db")
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2)
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

    /**
     * Creates a new test database if one has not already been made
     *
     * @param PlannedAnimalDatabase
     */
    @VisibleForTesting
    public static void injectTestDatabase(PlannedAnimalDatabase testDatabase) {
        if( singleton != null ) {
            singleton.close();
        }
        singleton = testDatabase;
    }

}
