package com.example.zooapp.Interface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zooapp.Data.ZooNode;

import java.util.List;

/**
 * This interface is used to store and manage the list of exhibits
 */
@Dao
public interface ZooNodeDao {
    @Insert
    long insert(ZooNode zooNode);

    @Insert
    List<Long> insertAll(List<ZooNode> zooNodeList);

    @Query("SELECT * FROM `zoo_node_list` WHERE `value`=:value")
    ZooNode getByValue(long value);

    @Query("SELECT * FROM `zoo_node_list` WHERE `id`=:id")
    ZooNode getById(String id);

    @Query("SELECT * FROM `zoo_node_list` WHERE `name`=:name")
    ZooNode getByName(String name);

    @Query("SELECT * FROM `zoo_node_list` ORDER BY `value`")
    List<ZooNode> getAll();

    @Query("SELECT * FROM `zoo_node_list` WHERE `kind` IN (:kind) ORDER BY `value`")
    List<ZooNode> getZooNodeKind(String kind);

    @Update
    int update(ZooNode exhibit);

    @Delete
    int delete(ZooNode exhibit);
}
