package com.ceair.wsdl.jdbc;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ceair.wsdl.domain.ServiceOperation;
import com.ceair.wsdl.domain.ServiceVersion;


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
    
    @Test
    public void testInsertServiceOperation() {        
        int result = OracleDBUtil.insertServiceOperation(new ServiceOperation(4,1,"enname","srcenname","inputMsgName","inputMsgNameSpace","inputMsgName","inputMsgNameSpace","endpoint"));
        assertEquals(1, result);
    }
    
    @Test
    public void testInsertServiceVersion() {        
        int result = OracleDBUtil.insertServiceVersion(new ServiceVersion(4, 1));
        assertEquals(1, result);
    }
    
    @Test
    public void testSelectServiceOperation() {    
        int serviceOperationID =1;
        assertNotNull(OracleDBUtil.selectServiceOperation(serviceOperationID));
    }
    
    @Test
    public void testSelectServiceVersion() {
        int serviceVersionID =1;
        assertNotNull(OracleDBUtil.selectServiceVersion(serviceVersionID));
    }

}
