package com.ceair.wsdl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
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
import javax.xml.transform.Templates;

public class WSDLMerger {

    public static void main(String args[]) {
        mergeWSDL("./wsdlfile/S1.wsdl", "./wsdlfile/M1.wsdl", "SMService", "SMServiceSoap","www.cea.com");
    }

    public static Definition mergeWSDL(String filepath1, String filepath2, String srvName, String portName, String targetNameSpace) {
        WSDLFactory factory;
        try {
            factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            WSDLWriter wirter = factory.newWSDLWriter();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition def1 = reader.readWSDL(filepath1);
            Definition def2 = reader.readWSDL(filepath2);

            def2 = mergeNamespace(def1, def2, targetNameSpace);
            def2 = mergeSchema(def1, def2);
            def2 = mergeMessage(def1, def2);
            def2 = mergePortType(def1, def2, portName);            
            def2 = mergeBinding(def1, def2, portName);
            def2 = mergeService(def2, srvName, portName);
            wirter.writeWSDL(def2, System.out);

            return def2;

        } catch (WSDLException e) {
            e.printStackTrace();
        }
        return null;

    }

    // namespace合并主要考虑两点：
    // 1.要是前缀不同， 内容一样，直接添加
    // 2.如果前缀一样， 内容不同，修改前缀再添加
    private static Definition mergeNamespace(Definition def1, Definition def2, String targetNameSpace) {
        Map<String, String> namespaceMap1 = def1.getNamespaces();
        Map<String, String> namespaceMap2 = def2.getNamespaces();
        // 若两个map中相同key对应的value不相等的key集合
        List<String> keyList = new ArrayList<String>();
        Iterator<Entry<String, String>> iter1 = namespaceMap1.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry<String, String> entry1 = (Entry<String, String>) iter1.next();
            String m1value = entry1.getValue() == null ? "" : entry1.getValue();
            String m2value = (String) (namespaceMap2.get(entry1.getKey()) == null ? ""
                    : namespaceMap2.get(entry1.getKey()));
            // 若两个map中相同key对应的value都存在 却不相等 x修改value
            if (!m1value.equals(m2value) && (!m1value.equals("")) && (!m2value.equals(""))) {
                keyList.add(entry1.getKey());
            }
        }
        // 重命名前缀前缀相同后缀不同的namespace 删除原来map中名字重复的项目
        Iterator<String> keyListItr = keyList.iterator();
        while (keyListItr.hasNext()) {
            String key = keyListItr.next();
            String value1 = namespaceMap1.get(key);
            String value2 = namespaceMap2.get(key);
            namespaceMap1.remove(key);
            namespaceMap2.remove(key);
            // 如果是tns 就改名n1或者n2
            if (key.indexOf("tns") != -1) {
                key = "n";
            }
            namespaceMap1.put(key + "1", value1);
            namespaceMap2.put(key + "2", value2);
        }
        namespaceMap2.putAll(namespaceMap1);
        // 插入设定的namespace
        def2.setTargetNamespace(targetNameSpace);
        if (def2.getNamespace("tns") != null) {
            def2.addNamespace("tns1", def2.getNamespace("tns"));
            def2.removeNamespace("tns");
        }
        def2.addNamespace("tns", targetNameSpace);
        // 输出结果 查看是否正确
        Iterator namespaceItr2 = namespaceMap2.entrySet().iterator();
        while (namespaceItr2.hasNext()) {
            Entry namespaceEntry2 = (Map.Entry) namespaceItr2.next();
            System.out.println(namespaceEntry2.getKey() + ":" + namespaceEntry2.getValue());
        }
        System.out.println("targetNamespace:" + def2.getTargetNamespace());
        return def2;
    }

    // 合并types下面的schema
    private static Definition mergeSchema(Definition def1, Definition def2) {
        List eleList1 = def1.getTypes().getExtensibilityElements();
        Iterator eleItr1 = eleList1.iterator();
        while (eleItr1.hasNext()) {
            def2.getTypes().addExtensibilityElement((ExtensibilityElement) eleItr1.next());
        }
        return def2;
    }

    // 需要 考虑名称一样的情况 如果名称一样 那就需要改成不一样 输入参数一 加后缀“1”，参数二加后缀“2”
    private static Definition mergeMessage(Definition def1, Definition def2) {
        Map msgMap1 = def1.getMessages();
        Map msgMap2 = def2.getMessages();
        // message的名称和实际内容不一样的情况 把key都保存下来
        List keyList = new ArrayList<String>();
        Iterator<Entry> iter1 = msgMap1.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry entry1 = (Entry) iter1.next();
            String m1value = entry1.getValue() == null ? "" : entry1.getValue().toString();
            String m2value = msgMap2.get(entry1.getKey()) == null ? "" : msgMap2.get(entry1.getKey()).toString();
            if (!m1value.equals(m2value) && (!m1value.equals("")) && (!m2value.equals(""))) {// 若两个map中相同key对应的value不相等
                                                                                             // x修改value
                keyList.add(entry1.getKey());
            }
        }
        // 如果有名字一样的情况 如果namespace也一样 就更名 不一样就不管
        Iterator keyListItr = keyList.iterator();
        while (keyListItr.hasNext()) {
            // 所有可能重名的key
            QName key = (QName) keyListItr.next();
            // 拿到message 替换message的Qname和key对应的Qname
            Message msg1 = (Message) msgMap1.get(key);
            QName tempQname1 = msg1.getQName();
            msg1.setQName(new QName(tempQname1.getNamespaceURI(), tempQname1.getLocalPart() + "1"));
            Message msg2 = (Message) msgMap2.get(key);
            QName tempQname2 = msg2.getQName();
            msg2.setQName(new QName(tempQname2.getNamespaceURI(), tempQname2.getLocalPart() + "2"));
            QName key1 = msg1.getQName();
            QName key2 = msg2.getQName();
            msgMap1.put(key1, msg1);
            msgMap2.put(key2, msg2);
            msgMap1.remove(key);
            msgMap2.remove(key);
        }
        msgMap2.putAll(msgMap1);
        return def2;
    }

    // 针对只有porttype一个的情况
    private static Definition mergePortType(Definition def1, Definition def2, String portName) {
        PortType portType1 = null;
        PortType portType2 = null;
        QName portTypeQname2 = null;
        List<Operation> optlist1 = null;
        List<Operation> optlistAll = null;

        // 获取def1下portType的所有operation
        Map porttypesMap1 = def1.getAllPortTypes();
        Iterator itr = porttypesMap1.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry portTypeEntry = (Entry) itr.next();
            portType1 = (PortType) portTypeEntry.getValue();
            optlist1 = portType1.getOperations();
            break;
        }

        // 添加到def2中去
        Map porttypesMap2 = def2.getAllPortTypes();
        Iterator itr2 = porttypesMap2.entrySet().iterator();
        while (itr2.hasNext()) {
            Map.Entry portTypeEntry = (Entry) itr2.next();
            portTypeQname2 = (QName) portTypeEntry.getKey();
            portType2 = (PortType) portTypeEntry.getValue();
            portType2.getOperations().addAll(optlist1);
            break;
        }

        optlistAll = portType2.getOperations();
        Iterator<Operation> optItr = optlistAll.iterator();
        while (optItr.hasNext()) {
            Operation tempOpt = optItr.next();
            Message inputMessage = tempOpt.getInput().getMessage();
            Message outputMessage = tempOpt.getOutput().getMessage();

            Map faultMap = tempOpt.getFaults();
            Iterator faultItr = faultMap.entrySet().iterator();
            while (faultItr.hasNext()) {
                Map.Entry faultEntry = (Map.Entry) faultItr.next();
                Fault fault = (Fault) faultEntry.getValue();
                Message faultMessage = fault.getMessage();
                faultMessage.setQName(new QName(def2.getNamespace("tns"), faultMessage.getQName().getLocalPart()));
                break;
            }

            inputMessage.setQName(new QName(def2.getNamespace("tns"), inputMessage.getQName().getLocalPart()));
            outputMessage.setQName(new QName(def2.getNamespace("tns"), outputMessage.getQName().getLocalPart()));

        }
        
        portType2.setQName(new QName(def2.getNamespace("tns"), portName));
        def2.getAllPortTypes().put(portTypeQname2, portType2);
        return def2;
    }

    // 合并单个binding下面的Operation
    private static Definition mergeBinding(Definition def1, Definition def2, String portName) {
        Binding binding1 = null;
        Binding binding2 = null;
        QName bindingQname2 = null;
        List optlist1 = null;

        Map bindingMap1 = def1.getAllBindings();
        Iterator itr = bindingMap1.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry bindingEntry = (Entry) itr.next();
            binding1 = (Binding) bindingEntry.getValue();
            optlist1 = binding1.getBindingOperations();
            break;
        }
        // 添加到def2中去
        Map porttypesMap2 = def2.getAllBindings();
        Iterator itr2 = porttypesMap2.entrySet().iterator();
        while (itr2.hasNext()) {
            Map.Entry bindingEntry = (Entry) itr2.next();
            bindingQname2 = (QName) bindingEntry.getKey();
            binding2 = (Binding) bindingEntry.getValue();
            binding2.getBindingOperations().addAll(optlist1);
            break;
        }
        binding2.setQName(new QName(def2.getNamespace("tns"), portName));
        def2.getAllBindings().put(bindingQname2, binding2);
        return def2;
    }
    
    // 合并服务 用主的
    private static Definition mergeService(Definition def2, String srvName, String portName) {
        Map srvmap = def2.getAllServices();
        Iterator itr = srvmap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry srvEntry = (Entry) itr.next();
            Service service = (Service) srvEntry.getValue();
            service.setQName(new QName(def2.getNamespace("tns"), srvName));
            Map portMap = service.getPorts();
            Iterator portItr = portMap.entrySet().iterator();
            while (portItr.hasNext()) {
                Map.Entry portMapEntry = (Entry) portItr.next();
                Port port = (Port) portMapEntry.getValue();
                port.setName(portName);
                break;
            }
            break;
        }
        return def2;
    }

}
