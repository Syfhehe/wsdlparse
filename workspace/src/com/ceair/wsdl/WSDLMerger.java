package com.ceair.wsdl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

public class WSDLMerger {
    
    //wsdl合并
    public static Definition mergeWSDL(String filepath1, String filepath2, String srvName, String portName,
            String targetNameSpace) {
        WSDLFactory factory;
        try {
            factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            WSDLWriter wirter = factory.newWSDLWriter();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition def1 = reader.readWSDL(filepath1);
            Definition def2 = reader.readWSDL(filepath2);

            removeImports(def1);
            removeImports(def2);
                
            def2 = mergeNamespace(def1, def2, targetNameSpace);
            def2 = mergeSchema(def1, def2);
            def2 = mergeMessage(def1, def2);
            def2 = mergePortType(def1, def2);
            def2 = mergeBinding(def1, def2);
            def2 = mergeService(def1, def2, srvName, portName);
            
            wirter.writeWSDL(def2, System.out);
            
            OutputStream wsdlString = new ByteArrayOutputStream();
            wirter.writeWSDL(def2,  wsdlString); 
            wsdlString.toString();
            
            //System.out.println(wsdlString.toString());

            return def2;

        } catch (WSDLException e) {
            e.printStackTrace();
        }
        return null;

    }

    //删除definition中的import
    private static void removeImports(Definition def){
        @SuppressWarnings("unchecked")
        Map<String, List<Import>> importMap = def.getImports();
        Iterator<Entry<String, List<Import>>> importMapItr = importMap.entrySet().iterator();
        while (importMapItr.hasNext()) {
            Entry<String, List<Import>> entry = importMapItr.next();
            List<Import> importList = entry.getValue();
            Iterator<Import> importItr = importList.iterator();
            while (importItr.hasNext()) {
                @SuppressWarnings("unused")
                Import importTemp = (Import) importItr.next();
                importItr.remove();
            }
            System.out.println();
        }
    } 
    
    // namespace合并主要考虑两点：
    // 1.要是前缀不同， 内容一样，直接添加
    // 2.如果前缀一样， 内容不同，修改前缀再添加
    @SuppressWarnings({ "unchecked" })
    private static Definition mergeNamespace(Definition def1, Definition def2, String targetNameSpace) {        
        Map<String, String> namespaceMap1 = def1.getNamespaces();
        Map<String, String> namespaceMap2 = def2.getNamespaces();        
        // 若两个map中相同key对应的value不相等的key集合
        List<String> keyList = new ArrayList<String>();
        Iterator<Entry<String, String>> iter1 = namespaceMap1.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry<String, String> entry1 = (Entry<String, String>) iter1.next();
            String m1value = entry1.getValue() == null ? "" : entry1.getValue();
            String m2value = (String) (namespaceMap2.get(entry1.getKey()) == null ? "" : namespaceMap2.get(entry1.getKey()));
            // 若两个map中相同key对应的value都存在 却不相等 则修改key 
            if (!m1value.equals(m2value) && (!m1value.equals("")) && (!m2value.equals(""))) {
                keyList.add(entry1.getKey());
            }
        }
        // 重命名前缀相同后缀不同的namespace删除原来map中名字重复的项目
        Iterator<String> keyListItr = keyList.iterator();
        while (keyListItr.hasNext()) {
            String key = keyListItr.next();
            String value1 = namespaceMap1.get(key);
            String value2 = namespaceMap2.get(key);
            namespaceMap1.remove(key);
            namespaceMap2.remove(key);
            String[] keyArray = genKeyString(namespaceMap1,namespaceMap2,key);
            namespaceMap1.put(keyArray[0], value1);
            namespaceMap2.put(keyArray[1], value2);
        }
        //把两个definition的namespace合并
        namespaceMap2.putAll(namespaceMap1);
        // 插入targetnamespace
        def2.addNamespace("tns", targetNameSpace);
        def2.setTargetNamespace(targetNameSpace);

        // 输出结果 查看是否正确
//        Iterator<Entry<String, String>> namespaceItr2 = namespaceMap2.entrySet().iterator();
//        while (namespaceItr2.hasNext()) {
//            Entry<String, String> namespaceEntry2 = namespaceItr2.next();
//            System.out.println(namespaceEntry2.getKey() + ":" + namespaceEntry2.getValue());
//        }
//        System.out.println("targetNamespace:" + def2.getTargetNamespace());
        return def2;
    }

    private static String[] genKeyString(Map<String, String> namespaceMap1, Map<String, String> namespaceMap2, String key) {
        int index=1;
        while(namespaceMap1.containsKey(key + index)||namespaceMap2.containsKey(key + index)){
            index++;
        }
        String[] keyTemp ={key+index, key+(index+1)};
        return keyTemp;
    }

    // 合并types下面的schema
    @SuppressWarnings("unchecked")
    private static Definition mergeSchema(Definition def1, Definition def2) {
        if(def1.getTypes()!=null){
            List<ExtensibilityElement> typeList1 = def1.getTypes().getExtensibilityElements();
            Iterator<ExtensibilityElement> typeItr1 = typeList1.iterator();
            if (def2.getTypes() == null) {
                def2.setTypes(def1.getTypes());
            } else {
                while (typeItr1.hasNext()) {
                    def2.getTypes().addExtensibilityElement((ExtensibilityElement) typeItr1.next());
                }
            }
        }
      
        return def2;
    }

    // 需要 考虑名称一样的情况 如果名称一样 那就需要改成不一样 输入参数一 加后缀“1”，参数二加后缀“2”
    @SuppressWarnings("unchecked")
    private static Definition mergeMessage(Definition def1, Definition def2) {
        Map<QName, Message> msgMap1 = def1.getMessages();
        Map<QName, Message> msgMap2 = def2.getMessages();
        msgMap2.putAll(msgMap1);
        return def2;
    }

    /*
     * 针对只有portType一个的情况
     * 需要考虑def2中没有porttype的情况
     */    
    @SuppressWarnings("unchecked")
    private static Definition mergePortType(Definition def1, Definition def2) {
        PortType portType1 = null;
        PortType portType2 = null;
        List<Operation> optlist1 = null;
        List<Operation> optlistAll = null;                
        // 获取def1下portType的所有operation 考虑只有一个portType的情况
        Map<QName, PortType> porttypesMap1 = def1.getAllPortTypes();
        Iterator<Entry<QName, PortType>> itr = porttypesMap1.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<QName, PortType> portTypeEntry = itr.next();
            portType1 = (PortType) portTypeEntry.getValue();
            optlist1 = portType1.getOperations();
            break;
        }
        // 添加到def2中去
        Map<QName, PortType> porttypesMap2 = def2.getAllPortTypes();
        Iterator<Entry<QName, PortType>> itr2 = porttypesMap2.entrySet().iterator();
        while (itr2.hasNext()) {
            Entry<QName, PortType> portTypeEntry = itr2.next();
            portType2 = (PortType) portTypeEntry.getValue();
            if(optlist1!=null){
                portType2.getOperations().addAll(optlist1);
            }
            break;
        }    
        //判断def2是否不包含porttype，不包含的话 为def2添加def1的porttype
        if(def2.getAllPortTypes().size()==0){
            def2.addPortType(portType1);
            portType2=portType1;
        }
        //input output 的 namespace 使用tns的namespace
        optlistAll = portType2.getOperations();
        Iterator<Operation> optItr = optlistAll.iterator();
        while (optItr.hasNext()) {
            Operation tempOpt = optItr.next();
            Message inputMessage = tempOpt.getInput().getMessage();
            Message outputMessage = tempOpt.getOutput().getMessage();
            Map<QName, Fault> faultMap = tempOpt.getFaults();
            Iterator<Entry<QName, Fault>> faultItr = faultMap.entrySet().iterator();
            while (faultItr.hasNext()) {
                Entry<QName, Fault> faultEntry = faultItr.next();
                Fault fault = (Fault) faultEntry.getValue();
                Message faultMessage = fault.getMessage();
                faultMessage.setQName(new QName(def2.getNamespace("tns"), faultMessage.getQName().getLocalPart()));
                break;
            }
            if (inputMessage != null && outputMessage != null) {
                inputMessage.setQName(new QName(def2.getNamespace("tns"), inputMessage.getQName().getLocalPart()));
                outputMessage.setQName(new QName(def2.getNamespace("tns"), outputMessage.getQName().getLocalPart()));
            }
        }
        return def2;
    }

    // 合并单个binding下面的Operation
    @SuppressWarnings("unchecked")
    private static Definition mergeBinding(Definition def1, Definition def2) {
        Binding binding1 = null;
        Binding binding2 = null;
        List<BindingOperation> optlist1 = null;

        Map<QName, Binding> bindingMap1 = def1.getAllBindings();
        Iterator<Entry<QName, Binding>> itr = bindingMap1.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<QName, Binding> bindingEntry = itr.next();
            binding1 = (Binding) bindingEntry.getValue();
            optlist1 = binding1.getBindingOperations();
            break;
        }
        // 添加到def2中去
        Map<QName, Binding> porttypesMap2 = def2.getAllBindings();
        Iterator<Entry<QName, Binding>> itr2 = porttypesMap2.entrySet().iterator();
        while (itr2.hasNext()) {
            Entry<QName, Binding> bindingEntry = itr2.next();
            binding2 = (Binding) bindingEntry.getValue();
            if(optlist1!=null){
                binding2.getBindingOperations().addAll(optlist1);
            }
            break;
        }
        //如果def2不包含binding，那就直接把def1的binding赋给def2
        if(def2.getAllBindings().size()==0){ 
            def2.addBinding(binding1);
            binding2 = binding1;
        }       
        //修改binding type的namespace
        binding2.getPortType().setQName(new QName(def2.getNamespace("tns"), binding2.getPortType().getQName().getLocalPart()));
        //binding2.getPortType().setQName(new QName(def2.getNamespace("tns"),  portName));
        return def2;
    }

    // 合并服务 用主的
    @SuppressWarnings("unchecked")
    private static Definition mergeService(Definition def1, Definition def2, String srvName, String portName) {
        if(def2.getServices().size()==0){
            Map<QName, Service> srvmap = def1.getAllServices();
            Iterator<Entry<QName, Service>> itr = srvmap.entrySet().iterator();
            while (itr.hasNext()) {
                Entry<QName, Service> srvEntry = itr.next();
                Service service = (Service) srvEntry.getValue();
                def2.addService(service);
            }
        }
        Map<QName, Service> srvmap = def2.getAllServices();
        Iterator<Entry<QName, Service>> itr = srvmap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<QName, Service> srvEntry = itr.next();
            Service service = (Service) srvEntry.getValue();
            service.setQName(new QName(def2.getNamespace("tns"), srvName));
            Map<String, Port> portMap = service.getPorts();
            Iterator<Entry<String, Port>> portItr = portMap.entrySet().iterator();
            while (portItr.hasNext()) {
                Entry<String, Port> portMapEntry = portItr.next();
                Port port = (Port) portMapEntry.getValue();
                port.setName(portName);
                //port.getBinding().setQName((new QName(def2.getNamespace("tns"),  portName)));
                break;
            }
            break;
        }
        return def2;
    }

}
