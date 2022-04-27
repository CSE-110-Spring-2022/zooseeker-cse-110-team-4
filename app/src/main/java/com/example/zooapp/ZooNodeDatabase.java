package com.example.zooapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ZooNode.class}, version = 1)
public abstract class ZooNodeDatabase extends RoomDatabase {
    public abstract ZooNodeDao ZooNodeDao();
}
