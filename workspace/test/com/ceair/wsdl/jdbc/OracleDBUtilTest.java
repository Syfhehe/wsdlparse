package com.ceair.wsdl.jdbc;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class OracleDBUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOpen() {        
        assertNotNull(OracleDBUtil.open());
    }
    
    @Test
    public void testClose() {        
        Connection connection = (Connection) OracleDBUtil.open();
        assertTrue(OracleDBUtil.close(connection));
    }

}
