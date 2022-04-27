package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "exhibit_list")
public class Exhibit {
    // Public Fields
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;
    public String[] tags;

    public Exhibit(@NonNull String name, String[] tags) {
        this.name = name;
        this.tags = tags;
    }

    public String[] getTags() {
        return tags;
    }
}
