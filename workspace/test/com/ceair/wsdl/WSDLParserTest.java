package com.ceair.wsdl;

import static org.junit.Assert.*;

import java.util.List;

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
        List<ServiceOperation> list1 = WSDLParser.parseWSDL("./wsdlfile/M1.wsdl");
        assertEquals(1, list1.size());

    }

    @Test
    public void testParseWSDL2() {
        List<ServiceOperation> list2 = WSDLParser.parseWSDL("./wsdlfile/S1.wsdl");
        assertEquals(1, list2.size());
    }

    @Test
    public void testParseWSDL3() {
        ;
        List<ServiceOperation> list3 = WSDLParser.parseWSDL("./wsdlfile/SM1.wsdl");
        assertEquals(3, list3.size());
    }

    @Test
    public void testParseWSDL4() {
        List<ServiceOperation> list4 = WSDLParser.parseWSDL("./wsdlfile/test.wsdl");
        assertEquals(1, list4.size());
    }

}
