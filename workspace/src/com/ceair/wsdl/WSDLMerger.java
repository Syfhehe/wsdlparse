package com.ceair.wsdl;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

public class WSDLMerger {
    public static Definition mergeWSDL(String filepath1, String filepath2) {
        WSDLFactory factory;
        try {
            factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition def1 = reader.readWSDL(filepath1);
            Definition def2 = reader.readWSDL(filepath2);
            
            def2 = mergeSchema(def1, def2);
            def2 = mergeMessage(def1, def2);
            def2 = mergePortType(def1, def2);
            def2 = mergeBinding(def1, def2);
            
            return def2;

        } catch (WSDLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    private static Definition mergeBinding(Definition def1, Definition def2) {
        return def2;
        // TODO Auto-generated method stub

    }

    private static Definition mergePortType(Definition def1, Definition def2) {
        return def2;
        // TODO Auto-generated method stub

    }

    private static Definition mergeMessage(Definition def1, Definition def2) {
        return def2;
        // TODO Auto-generated method stub

    }

    private static Definition mergeSchema(Definition def1, Definition def2) {
        return def2;
        // TODO Auto-generated method stub

    }
}
