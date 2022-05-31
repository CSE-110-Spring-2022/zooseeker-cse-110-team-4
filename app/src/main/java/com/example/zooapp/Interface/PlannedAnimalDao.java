package com.example.zooapp.Interface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zooapp.Data.ZooNode;

import java.util.List;

/**
 * This interface is used to store and manage the planned list of animals
 */
@Dao
public interface PlannedAnimalDao {
    @Insert
    long insert(ZooNode zooNode);

    @Query("SELECT * FROM `zoo_node_list` WHERE `value`=:value")
    ZooNode getById(long value);

    @Query("SELECT * FROM `zoo_node_list` WHERE `name`=:name")
    ZooNode getByName(String name);

    @Query("SELECT * FROM `zoo_node_list` ORDER BY `value`")
    List<ZooNode> getAll();

    @Query("SELECT * FROM `zoo_node_list` WHERE `kind` IN (:kind) ORDER BY `value`")
    List<ZooNode> getZooNodeKind(String kind);

    @Query("DELETE FROM `zoo_node_list`")
    void deleteAll();

    @Update
    int update(ZooNode exhibit);

    @Delete
    int delete(ZooNode exhibit);

}
