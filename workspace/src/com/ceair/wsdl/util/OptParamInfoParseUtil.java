package com.ceair.wsdl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import com.ceair.wsdl.domain.EsbOptParam;
import com.ceair.wsdl.domain.EsbParamInfo;
import com.ibm.wsdl.OperationImpl;

public class OptParamInfoParseUtil {

    private static final String INPUT_PARAMETER = "Operation Input Parameter";
    private static final String OUTPUT_PARAMETER = "Operation Output Parameter";
    private static final String FAULT_PARAMETER = "Operation Fault Parameter";

    public static void main(String[] args) {
        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            String filepath = "./wsdlfile/todo/E-HR主数据.wsdl";
            
            Definition def = reader.readWSDL(filepath);
            List<EsbParamInfo> paramInfosList = ParamInfoParseUtil.parseParamInfo(filepath);

            List<EsbOptParam> list = parseOptParamInfo(def, paramInfosList);
            Iterator<EsbOptParam> iterator = list.iterator();
            while (iterator.hasNext()) {
                EsbOptParam esbOptParam = (EsbOptParam) iterator.next();
                System.out.println("+++++++++++++++++++++++++++++++++++++++");
                System.out.println("paramName:" + esbOptParam.getParamName());
                System.out.println("paramType:" + esbOptParam.getParamType());
                if(esbOptParam.getParamsInfoParam()!= null){
                    System.out.println("paramsInfoParam:" + esbOptParam.getParamsInfoParam().getNodeName());
                }
            }
        } catch (WSDLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<EsbOptParam> parseOptParamInfo(Definition def, List<EsbParamInfo> paramInfoList) {

        Map<String, EsbParamInfo> paramInfoMap = convertListToMap(paramInfoList);
        List<EsbOptParam> optParamsList = new ArrayList<EsbOptParam>();
        // 获取所有的message
        Map<QName, Message> msgMap = def.getMessages();

        Map<QName, PortType> portTypeMap = def.getAllPortTypes();
        Iterator<Entry<QName, PortType>> portTypeItr = portTypeMap.entrySet().iterator();
        while (portTypeItr.hasNext()) {
            Entry<QName, PortType> portTypeEntry = portTypeItr.next();
            PortType portType = (PortType) portTypeEntry.getValue();
            List<Operation> portTypeOperationList = portType.getOperations();
            Iterator<Operation> portTypeOperationItr = portTypeOperationList.iterator();
            while (portTypeOperationItr.hasNext()) {
                Operation portTypeOperation = (OperationImpl) portTypeOperationItr.next();
                System.out.println("Operation Name:" + portTypeOperation.getName());
                QName qnameInput = portTypeOperation.getInput().getMessage().getQName();
                System.out.println("InputNameSpace:" + qnameInput.getNamespaceURI());
                System.out.println("InputName:" + qnameInput.getLocalPart());

                QName qnameOutput = portTypeOperation.getOutput().getMessage().getQName();
                System.out.println("OutputNameSpace:" + qnameOutput.getNamespaceURI());
                System.out.println("OutputName:" + qnameOutput.getLocalPart());

                Map<QName, Fault> faultMap = portTypeOperation.getFaults();
                Iterator<Entry<QName, Fault>> faultItr = faultMap.entrySet().iterator();
                // fault按照一个做
                QName qnameFault = null;
                while (faultItr.hasNext()) {
                    Entry<QName, Fault> faultEntry = faultItr.next();
                    Fault fault = (Fault) faultEntry.getValue();
                    qnameFault = fault.getMessage().getQName();
                    System.out.println("FaultNameSpace:" + fault.getMessage().getQName().getNamespaceURI());
                    System.out.println("FaultName:" + fault.getMessage().getQName().getLocalPart());
                    break;
                }
                
                Map<String, QName> optParamMap = new HashMap<String, QName>();
                if (qnameInput != null) {
                    optParamMap.put(INPUT_PARAMETER, qnameInput);
                }
                if (qnameOutput != null) {
                    optParamMap.put(OUTPUT_PARAMETER, qnameOutput);
                }
                if (qnameFault != null) {
                    optParamMap.put(FAULT_PARAMETER, qnameFault);
                }
                
                Iterator<Entry<String, QName>> optParamItr = optParamMap.entrySet().iterator();
                while (optParamItr.hasNext()) {
                    Entry<String, QName> optParamEntry = optParamItr.next();
                    Message message = msgMap.get(optParamEntry.getValue());
                    Map<QName, Part> msgPartMap = message.getParts();
                    Iterator<Entry<QName, Part>> msgPartItr = msgPartMap.entrySet().iterator();
                    while (msgPartItr.hasNext()) {
                        EsbOptParam esbOptParam = new EsbOptParam();
                        Entry<QName, Part> partEntry = msgPartItr.next();
                        Part part = partEntry.getValue();
                        if(part.getTypeName()!=null){
                            if (paramInfoMap.containsKey(part.getTypeName().getLocalPart())) {
                                EsbParamInfo esbParamInfo = paramInfoMap.get(part.getTypeName().getLocalPart());
                                esbOptParam.setParamName(part.getTypeName().getLocalPart());
                                esbOptParam.setParamType(optParamEntry.getKey());
                                esbOptParam.setParamsInfoParam(esbParamInfo);
                            } else {
                                esbOptParam.setParamName(part.getName());
                                esbOptParam.setParamType(optParamEntry.getKey());
                            }
                        }
                        optParamsList.add(esbOptParam);
                    }
                }
            }
        }
        return optParamsList;
    }

    private static Map<String, EsbParamInfo> convertListToMap(List<EsbParamInfo> list) {
        Map<String, EsbParamInfo> map = new HashMap<String, EsbParamInfo>();
        Iterator<EsbParamInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            EsbParamInfo esbParamInfo = iterator.next();
            map.put(esbParamInfo.getNodeName(), esbParamInfo);
        }
        return map;
    }

}
