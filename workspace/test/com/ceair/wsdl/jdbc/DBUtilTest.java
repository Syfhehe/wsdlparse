package com.ceair.wsdl.jdbc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.Connection;

public class DBUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOpen() {
        Connection connection= DBUtil.open();
    }

    @Test
    public void testClose() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateTable() {
        fail("Not yet implemented");
    }

    @Test
    public void testInsert() {
        fail("Not yet implemented");
    }

}
