package com.ceair.wsdl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WSDLMergerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() {
        WSDLMerger.mergeWSDL("./wsdlfile/S1.wsdl", "./wsdlfile/M1.wsdl", "SMService", "SMServiceSoap", "www.cea.com");
    }
    
    @Test
    public void test2() {
        WSDLMerger.mergeWSDL("./wsdlfile/M1.wsdl", "./wsdlfile/S1.wsdl", "SMService", "SMServiceSoap", "www.cea.com");
    }
    
    @Test
    public void test3() {
        WSDLMerger.mergeWSDL("./wsdlfile/Online/continent-WService-soap.wsdl", "./wsdlfile/Online/continent.wsdl", "ContinentWServiceImplService", "ContinentWServiceImplPort", "http://impl.sysdata.provider.service.ceair.com/");
    }
    
    @Test
    public void test4() {
        WSDLMerger.mergeWSDL( "./wsdlfile/Online/continent.wsdl", "./wsdlfile/Online/continent-WService-soap.wsdl","ContinentWServiceImplService", "ContinentWServiceImplPort", "http://impl.sysdata.provider.service.ceair.com/");
    }
    

}
