package com.example.zooapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Exhibit.class}, version = 1)
public abstract class ZooSeekerDatabase extends RoomDatabase {
    public abstract ExhibitDao ExhibitDao();
}
