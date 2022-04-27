package com.example.zooapp;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ExhibitDao {
    @Insert
    long insert(Exhibit exhibit);

    @Insert
    long insert(ZooNode zooNode);

    @Insert
    List<Long> insertAll(List<ZooNode> zooNodeList);

    @Query("SELECT * FROM `zoo_node_list` WHERE `id`=:id")
    ZooNode getById(String id);

    @Query("SELECT * FROM `zoo_node_list` ORDER BY `id`")
    List<ZooNode> getAll();

    @Update
    int update(ZooNode exhibit);

    @Delete
    int delete(ZooNode exhibit);
}
