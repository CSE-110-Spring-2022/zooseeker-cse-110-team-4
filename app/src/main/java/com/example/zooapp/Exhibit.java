package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exhibit_list")
public class Exhibit {

    @NonNull
    public String id;
    public String itemType;
    public String[] tag;

    public Exhibit(@NonNull String id, String itemType, String[] tag) {
        this.id = id;
        this.itemType = itemType;
        this.tag = tag;
    }
}
