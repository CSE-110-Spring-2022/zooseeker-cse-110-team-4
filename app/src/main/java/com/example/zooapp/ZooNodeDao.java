package com.example.zooapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ZooNodeDao {
    @Insert
    long insert(ZooNode zooNode);

    @Insert
    List<Long> insertAll(List<ZooNode> zooNodeList);

    @Query("SELECT * FROM `zoo_node_list` WHERE `value`=:value")
    ZooNode getById(long value);

    @Query("SELECT * FROM `zoo_node_list` WHERE `id`=:id")
    ZooNode getByName(String id);

    @Query("SELECT * FROM `zoo_node_list` ORDER BY `value`")
    List<ZooNode> getAll();

    @Update
    int update(ZooNode exhibit);

    @Delete
    int delete(ZooNode exhibit);
}
