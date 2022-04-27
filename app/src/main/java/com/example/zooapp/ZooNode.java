package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "zoo_node_list")
public class ZooNode {
    @NonNull
    public String id;
    public String itemType;
    public String[] tags;

    public ZooNode(@NonNull String id, String itemType, String[] tags ) {
        this.id = id;
        this.itemType = itemType;
        this.tags = tags;
    }
}
