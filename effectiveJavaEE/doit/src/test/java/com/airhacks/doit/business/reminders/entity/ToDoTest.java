package com.airhacks.doit.business.reminders.entity;

import static org.junit.Assert.*;
import org.junit.Test;

public class ToDoTest {

    public ToDoTest() {
    }

    /**
     * Test of isValid method, of class ToDo.
     */
    @Test
    public void testIsValid() {
        ToDo valid = new ToDo("", "available", 11);
        assertTrue(valid.isValid());
    }

    @Test
    public void testIsInvalid() {
        ToDo valid = new ToDo("", null, 11);
        assertFalse(valid.isValid());
    }

    @Test
    public void testTodoWithoutDescription() {
        ToDo valid = new ToDo("implement", null, 10);
        assertTrue(valid.isValid());
    }
}
