package com.ceair.wsdl;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

import com.ceair.wsdl.jdbc.FileUtil;

public class WSDLMerger {

    public static void main(String args[]) {
        mergeWSDL("./wsdlfile/M1.wsdl", "./wsdlfile/S1.wsdl");
    }

    public static Definition mergeWSDL(String filepath1, String filepath2) {
        WSDLFactory factory;
        try {
            factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);
           
            
            String string1 = FileUtil.file2String(new File(filepath1), "utf-8");
            String string2 = FileUtil.file2String(new File(filepath2), "utf-8");
            string1 = string1.replace("tns:", "n1:").replace("xmlns:tns", "xmlns:n1");
            string2 = string2.replace("tns:", "n2:").replace("xmlns:tns", "xmlns:n2");
            
            
            String filepath1_replace = FileUtil.string2File(string1, filepath1);
            String filepath2_replace = FileUtil.string2File(string2, filepath2);

            Definition def1 = reader.readWSDL(filepath1);
            Definition def2 = reader.readWSDL(filepath2);
            Definition def1_replace = reader.readWSDL(filepath1_replace);
            Definition def2_replace = reader.readWSDL(filepath2_replace);

            def2 = mergeNamespace(def1, def2);
            def2 = mergeSchema(def1_replace, def2_replace, def2);
            def2 = mergeMessage(def1_replace, def2_replace,def2);
            def2 = mergePortType(def1, def2);
            def2 = mergeBinding(def1, def2);
            
            WSDLWriter wirter = factory.newWSDLWriter();
            wirter.writeWSDL(def2, System.out);
            
            return def2;

        } catch (WSDLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    
    private static Definition mergeNamespace(Definition def1, Definition def2) {
        Map namespaceMap1 = def1.getNamespaces();
        Map namespaceMap2 = def2.getNamespaces();
        namespaceMap2.putAll(namespaceMap1);

        String targetNS1 = def1.getTargetNamespace();
        String targetNS2 = def2.getTargetNamespace();

        def2.setTargetNamespace("www.cea.com");
        def2.addNamespace("n1", targetNS1);
        def2.addNamespace("n2", targetNS2);
        def2.addNamespace("tns", "www.cea.com");

        Iterator namespaceItr2 = namespaceMap2.entrySet().iterator();
        while (namespaceItr2.hasNext()) {
            Entry namespaceEntry2 = (Map.Entry) namespaceItr2.next();
            System.out.println(namespaceEntry2.getKey() + ":" + namespaceEntry2.getValue());
        }
        System.out.println("targetNamespace:" + def2.getTargetNamespace());
        return def2;

    }
    

    private static Definition mergeSchema(Definition def1_replace, Definition def2_replace, Definition targetDef) {
        
        List<ExtensibilityElement> targetDefList = targetDef.getTypes().getExtensibilityElements();
        Iterator targetDefItr = targetDefList.iterator();
        while(targetDefItr.hasNext()){
            targetDefItr.next();
            targetDefItr.remove();
        }
        
        List eleList1 = def1_replace.getTypes().getExtensibilityElements();
        Iterator eleItr1 = eleList1.iterator();
        while(eleItr1.hasNext()){
            targetDef.getTypes().addExtensibilityElement((ExtensibilityElement) eleItr1.next());
        }
        List eleList2 = def2_replace.getTypes().getExtensibilityElements();
        Iterator eleItr2 = eleList2.iterator();
        while(eleItr2.hasNext()){
            targetDef.getTypes().addExtensibilityElement((ExtensibilityElement) eleItr2.next());
        }        
        
        return targetDef;
    }
    
    private static Definition mergeMessage(Definition def1_replace, Definition def2_replace, Definition targetDef) {
        Map messageMap = targetDef.getMessages();
        Iterator messageKeyItr = messageMap.keySet().iterator();
        while(messageKeyItr.hasNext()){
            messageKeyItr.next();
            messageKeyItr.remove();
        }        
        targetDef.getMessages().putAll(def1_replace.getMessages());
        targetDef.getMessages().putAll(def2_replace.getMessages());
        return targetDef;
    }
    
    //针对只有porttype一个的情况
    private static Definition mergePortType(Definition def1, Definition def2) {
        PortType portType1 =null;
        PortType portType2 =null;
        QName portTypeQname2 =null;
        List optlist1 = null;
        
        //获取def1下portType的所有operation
        Map porttypesMap1 = def1.getAllPortTypes();
        Iterator itr= porttypesMap1.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry portTypeEntry = (Entry) itr.next();
            portType1 = (PortType) portTypeEntry.getValue();   
            optlist1 = portType1.getOperations();
            break;
        }
        
        //添加到def2中去
        Map porttypesMap2 = def2.getAllPortTypes();
        Iterator itr2= porttypesMap2.entrySet().iterator();
        while(itr2.hasNext()){
            Map.Entry portTypeEntry = (Entry) itr2.next();
            portTypeQname2 = (QName) portTypeEntry.getKey();   
            portType2 = (PortType) portTypeEntry.getValue();   
            portType2.getOperations().addAll(optlist1);
            break;
        }

        def2.getAllPortTypes().put(portTypeQname2, portType2);       
        return def2;

    }
    
    //合并单个binding下面的Operation
    private static Definition mergeBinding(Definition def1, Definition def2) {
        Binding binding1 = null;
        Binding binding2 = null;
        QName bindingQname2 =null;
        List optlist1 = null;
        
        Map bindingMap1 = def1.getAllBindings();
        Iterator itr = bindingMap1.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry bindingEntry = (Entry) itr.next();
            binding1 = (Binding) bindingEntry.getValue();
            optlist1 = binding1.getBindingOperations();
            break;
        }
      //添加到def2中去
        Map porttypesMap2 = def2.getAllBindings();
        Iterator itr2= porttypesMap2.entrySet().iterator();
        while(itr2.hasNext()){
            Map.Entry bindingEntry = (Entry) itr2.next();
            bindingQname2 = (QName) bindingEntry.getKey();   
            binding2 = (Binding) bindingEntry.getValue();   
            binding2.getBindingOperations().addAll(optlist1);
            break;
        }
        def2.getAllBindings().put(bindingQname2, binding2);       

        
        return def2;
    }


}
