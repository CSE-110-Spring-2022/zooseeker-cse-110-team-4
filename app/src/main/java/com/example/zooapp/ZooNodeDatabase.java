package com.example.zooapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {ZooNode.class},
        version = 2)
public abstract class ZooNodeDatabase extends RoomDatabase {
    public static ZooNodeDatabase singleton = null;

//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE new_zoo_list (name TEXT, id TEXT NOT NULL, group_id TEXT," +
//                    "value INTEGER NOT NULL, kind TEXT, tags TEXT, lat TEXT, lng TEXT, PRIMARY KEY(value))");
//
//            database.execSQL("DROP TABLE zoo_node_list");
//
//            database.execSQL("ALTER TABLE new_zoo_list RENAME TO zoo_node_list");
//        }
//    };

    public abstract ZooNodeDao ZooNodeDao();

    public synchronized static ZooNodeDatabase getSingleton(Context context) {
        if( singleton == null ) {
            singleton = ZooNodeDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ZooNodeDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ZooNodeDatabase.class, "zoo_app.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<ZooNode> zooNodeList = ZooNode
                                    .loadJSON(context, "sample_node_info.json");
                            getSingleton(context).ZooNodeDao().insertAll(zooNodeList);
                        });
                    }
                })
                .build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(ZooNodeDatabase testDatabase) {
        if( singleton != null ) {
            singleton.close();
        }
        singleton = testDatabase;
    }

}
