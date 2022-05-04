package com.example.zooapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {ZooNode.class}, version = 1)
public abstract class ZooNodeDatabase extends RoomDatabase {
    public static ZooNodeDatabase singleton = null;

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
