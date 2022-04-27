package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "exhibit_list")
public class Exhibit {
    // Public Fields
    @NonNull
    public String id;
    public String[] tags;

    public Exhibit(@NonNull String id, String[] tags) {
        this.id = id;
        this.tags = tags;
    }

    public void randFunction(String something) {
        // This does nothing
    }

    public String[] getTags() {
        return tags;
    }
}
