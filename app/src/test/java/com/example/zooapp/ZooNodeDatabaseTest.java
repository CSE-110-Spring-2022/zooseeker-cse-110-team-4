package com.example.zooapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ZooNodeDatabaseTest {
    private ZooNodeDao dao;
    private ZooNodeDatabase db;
    private String[] gorillaTags, fishTags;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ZooNodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.ZooNodeDao();
        gorillaTags = new String[] {"gorilla", "ape"};
        fishTags = new String[] {"fish", "ocean"};
    }

    @Test
    public void testInsert() {
        ZooNode item1 = new ZooNode("gorilla_exhibit", "exhibit", "Gorillas", gorillaTags);
        ZooNode item2 = new ZooNode("fish_exhibit", "exhibit", "Fish", fishTags);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        ZooNode insertedItem = new ZooNode("gorilla_exhibit", "exhibit", "Gorillas", gorillaTags);
        long value = dao.insert(insertedItem);

        ZooNode item = dao.getById(value);
        assertEquals(value, item.value);
        assertEquals(insertedItem.id, item.id);
        assertEquals(insertedItem.kind, item.kind);
        assertEquals(insertedItem.name, item.name);
        for( int i = 0; i < gorillaTags.length; i++ ) {
            assertEquals(insertedItem.tags[i], item.tags[i]);
        }
    }

    @Test
    public void testUpdate() {
        ZooNode insertItem = new ZooNode("gorilla_exhibit", "exhibit", "Gorillas", gorillaTags);
        long value = dao.insert(insertItem);

        ZooNode item = dao.getById(value);
        item.id = "fish_exhibit";
        item.kind = "gate";
        item.name = "Fish";
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);

        item = dao.getById(value);
        assertNotNull(item);
        assertEquals("fish_exhibit", item.id);
        assertEquals("gate", item.kind);
        assertEquals("Fish", item.name);
    }

    @Test
    public void testDelete() {
        ZooNode insertItem = new ZooNode("gorilla_exhibit", "exhibit", "Gorillas", gorillaTags);
        long value = dao.insert(insertItem);

        ZooNode item = dao.getById(value);
        int itemsDeleted = dao.delete(item);
        assertEquals(1, itemsDeleted);
        assertNull(dao.getById(value));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
