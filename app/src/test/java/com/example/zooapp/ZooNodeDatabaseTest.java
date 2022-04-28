package com.example.zooapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ZooNodeDatabaseTest {
    private ExhibitDao dao;
    private ExhibitDatabase db;
    private String[] gorillaTags, fishTags;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.ExhibitDao();
        gorillaTags = new String[] {"gorilla", "ape"};
        fishTags = new String[] {"fish", "ocean"};
    }

    @Test
    public void testInsert() {
        Exhibit item1 = new Exhibit("gorilla_exhibit", gorillaTags);
        Exhibit item2 = new Exhibit("fish_exhibit", fishTags);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        Exhibit insertedItem = new Exhibit("gorilla_exhibit", gorillaTags);
        long value = dao.insert(insertedItem);

        Exhibit item = dao.getbyId(value);
        assertEquals(value, item.value);
        assertEquals(insertedItem.id, item.id);
        for( int i = 0; i < gorillaTags.length; i++ ) {
            assertEquals(insertedItem.tags[i], item.tags[i]);
        }
    }

    @Test
    public void testUpdate() {
        Exhibit insertItem = new Exhibit("gorilla_exhibit", gorillaTags);
        long value = dao.insert(insertItem);

        Exhibit item = dao.getbyId(value);
        item.id = "fish_exhibit";
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);

        item = dao.getbyId(value);
        assertNotNull(item);
        assertEquals("fish_exhibit", item.id);
    }

    @Test
    public void testDelete() {
        Exhibit insertItem = new Exhibit("gorilla_exhibit", gorillaTags);
        long value = dao.insert(insertItem);

        Exhibit item = dao.getbyId(value);
        int itemsDeleted = dao.delete(item);
        assertEquals(1, itemsDeleted);
        assertNull(dao.getbyId(value));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
