package com.ceair.wsdl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ceair.wsdl.domain.ServiceOperation;

public class WSDLParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParseWSDL1() {
        Map<String, ServiceOperation> map = WSDLParser.parseWSDL("./wsdlfile/SM1_merger.wsdl",7,5);
        assertEquals(1, map.size());

    }

}
