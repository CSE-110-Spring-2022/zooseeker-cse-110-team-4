package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "zoo_node_list")
public class ZooNode {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;
    public String itemType;
    public String[] tags;

    public ZooNode(@NonNull String name, String itemType, String[] tags ) {
        this.name = name;
        this.itemType = itemType;
        this.tags = tags;
    }

    public String[] getTags() {
        return tags;
    }
}
