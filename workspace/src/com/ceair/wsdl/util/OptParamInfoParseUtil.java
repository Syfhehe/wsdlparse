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

            List<String> filelist = FileUtil.getFileList(
                    "E:\\1learningmaterials\\maven\\WSDL\\workspace\\wsdlfile\\todo", new ArrayList<String>());
            Iterator<String> fileNameItr = filelist.iterator();
            while (fileNameItr.hasNext()) {
                String fileName = fileNameItr.next();
                Definition def = reader.readWSDL(fileName);
                List<EsbParamInfo> paramInfosList = ParamInfoParseUtil.parseParamInfo(fileName);
                List<EsbOptParam> list = parseOptParamInfo(def, paramInfosList);
                Iterator<EsbOptParam> iterator = list.iterator();
                while (iterator.hasNext()) {
                    EsbOptParam esbOptParam = (EsbOptParam) iterator.next();
                    System.out.println("+++++++++++++++++++++++++++++++++++++++");
                    System.out.println("paramName:" + esbOptParam.getParamName());
                    System.out.println("paramType:" + esbOptParam.getParamType());
                    System.out.println("optName:" + esbOptParam.getOptEnName());

                    if (esbOptParam.getParamsInfoParam() != null) {
                        System.out.println("paramsInfoParam:" + esbOptParam.getParamsInfoParam().getNodeName());
                    }
                    System.out.println("+++++++++++++++++++++++++++++++++++++++");

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
                    optParamMap.put(portTypeOperation.getName() + ":" + INPUT_PARAMETER, qnameInput);
                }
                if (qnameOutput != null) {
                    optParamMap.put(portTypeOperation.getName() + ":" + OUTPUT_PARAMETER, qnameOutput);
                }
                if (qnameFault != null) {
                    optParamMap.put(portTypeOperation.getName() + ":" + FAULT_PARAMETER, qnameFault);
                }

                Iterator<Entry<String, QName>> optParamItr = optParamMap.entrySet().iterator();
                while (optParamItr.hasNext()) {
                    Entry<String, QName> optParamEntry = optParamItr.next();
                    if (msgMap.size() != 0) {
                        Message message = msgMap.get(optParamEntry.getValue());
                        Map<QName, Part> msgPartMap = message.getParts();
                        Iterator<Entry<QName, Part>> msgPartItr = msgPartMap.entrySet().iterator();
                        while (msgPartItr.hasNext()) {
                            EsbOptParam esbOptParam = new EsbOptParam();
                            Entry<QName, Part> partEntry = msgPartItr.next();
                            Part part = partEntry.getValue();
                            //<wsdl:part name="arg0" type="xsd:string">
                            if (paramInfoMap.containsKey(part.getName())) {
                                EsbParamInfo esbParamInfo = paramInfoMap.get(part.getName());
                                esbOptParam.setParamName(part.getName());
                                String[] array = spileString(optParamEntry.getKey());
                                esbOptParam.setOptEnName(array[0]);
                                esbOptParam.setParamType(array[1]);
                                esbOptParam.setParamsInfoParam(esbParamInfo);
                            }
                            //<wsdl:part name="return" type="tns:masterData"></wsdl:part>
                            else if (part.getTypeName() != null
                                    && paramInfoMap.containsKey(part.getTypeName().getLocalPart())) {
                                EsbParamInfo esbParamInfo = paramInfoMap.get(part.getTypeName().getLocalPart());
                                esbOptParam.setParamName(part.getName());
                                String[] array = spileString(optParamEntry.getKey());
                                esbOptParam.setOptEnName(array[0]);
                                esbOptParam.setParamType(array[1]);
                                esbOptParam.setParamsInfoParam(esbParamInfo);
                            } 
                            //<wsdl:part name="parameters" element="tns:getInfoByMobilePhone" />
                            else if (part.getElementName() != null
                                    && paramInfoMap.containsKey(part.getElementName().getLocalPart())) {
                                EsbParamInfo esbParamInfo = paramInfoMap.get(part.getElementName().getLocalPart());
                                esbOptParam.setParamName(part.getElementName().getLocalPart());
                                String[] array = spileString(optParamEntry.getKey());
                                esbOptParam.setOptEnName(array[0]);
                                esbOptParam.setParamType(array[1]);
                                esbOptParam.setParamsInfoParam(esbParamInfo);
                            }
                            optParamsList.add(esbOptParam);
                        }
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
    
    private static String[] spileString(String string){
        String[] array = string.split(":");
        return array; 
    }

}
