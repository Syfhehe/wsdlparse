package com.ceair.wsdl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import com.ceair.wsdl.domain.ServiceOperation;
import com.ceair.wsdl.domain.ServiceVersion;
import com.ceair.wsdl.util.FileUtil;
import com.ceair.wsdl.util.OracleDBUtil;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12OperationImpl;


public class WSDLCreater {

    public static void main(String args[]) {
//        ServiceOperation serviceOperation = OracleDBUtil.selectServiceOperation(4);
//        ServiceVersion serviceVersion = OracleDBUtil.selectServiceVersion(serviceOperation.getServiceVerId());
//        createWSDL(serviceOperation, serviceVersion);
        
        List<ServiceOperation> serviceOperationList = new ArrayList<ServiceOperation>();
        ServiceOperation serviceOperation1 = new ServiceOperation("getStockInfo", "getStockInfo", 
                "optInputMsgName1", "optInputMsgNs1", "optOutputMsgName1", "optOutputMsgNs1", "optFaultMsgName1", "optFaultMsgNs1", "optSoapAction1",
                "getStockInfoSoapIn", "www.cea.com", "getStockInfoSoapOut","www.cea.com", "", "", "http://webxml.com.cn/getStockInfo");
        ServiceOperation serviceOperation2 = new ServiceOperation("getInfoByMobilePhone", "getInfoByMobilePhone", 
                "optInputMsgName2", "optInputMsgNs2", "optOutputMsgName2", "optOutputMsgNs2", "optFaultMsgName2", "optFaultMsgNs2", "optSoapAction2", 
                "getInfoByMobilePhoneSoapIn", "www.cea.com", "getInfoByMobilePhoneSoapOut", "www.cea.com", "", "", "Yangzhili/getInfoByMobilePhone");
        serviceOperationList.add(serviceOperation1);
        serviceOperationList.add(serviceOperation2);
        ServiceVersion serviceVersion = new ServiceVersion(FileUtil.file2String(new File("./wsdlfile/SM1.wsdl"), "utf-8"));
        createWSDL(serviceOperationList,serviceVersion);
                
    }

    public static void createWSDL(List<ServiceOperation> serviceOperation, ServiceVersion serviceVersion) {

        try {
            String wsdlString = serviceVersion.getWsdlClob();
            InputStream in_withcode = new ByteArrayInputStream(wsdlString.getBytes("UTF-8"));  
            //读取刚才保存的wsdl文件 文件内容和数据库中的wsdl_clob一致
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader reader = wsdlFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);
            Definition def = reader.readWSDL("",new InputSource(in_withcode));
            String aString = null;
            //遍历整个PortTypes
            Map portTypeMap = def.getAllPortTypes();
            Iterator portTypeItr = portTypeMap.entrySet().iterator();
            while (portTypeItr.hasNext()) {
                //新的Operation List用来替换原来的Operation
                List<Operation> portTypeOperationListModify = new ArrayList();
                Map.Entry portTypeEntry = (Map.Entry) portTypeItr.next();
                PortType portType = (PortType) portTypeEntry.getValue();
                List<Operation> portTypeOperationList = portType.getOperations();
                Iterator<Operation> portTypeOperationItr = portTypeOperationList.iterator();
                while (portTypeOperationItr.hasNext()) {
                    Operation portTypeOperation = (OperationImpl) portTypeOperationItr.next();
                    System.out.println("Operation Name:" + portTypeOperation.getName());
                    // 修改 input message
                    if (portTypeOperation.getInput() != null) {
                        Message messageInput = portTypeOperation.getInput().getMessage();
                        QName qNameInput = new QName(serviceOperation.getOptInputMsgNs(),
                                serviceOperation.getOptInputMsgName());
                        messageInput.setQName(qNameInput);
                        portTypeOperation.getInput().setMessage(messageInput);
                        def.addNamespace("inputtns", serviceOperation.getOptInputMsgNs());
                    }
                    // 修改 output message
                    if (portTypeOperation.getOutput() != null) {
                        Message messageOutput = portTypeOperation.getOutput().getMessage();
                        QName qNameOutput = new QName(serviceOperation.getOptOutputMsgNs(),
                                serviceOperation.getOptOutputMsgName());
                        messageOutput.setQName(qNameOutput);
                        portTypeOperation.getOutput().setMessage(messageOutput);
                        def.addNamespace("outputtns", serviceOperation.getOptOutputMsgNs());
                    }
                    // 修改 fault message
                    if (portTypeOperation.getFault(serviceOperation.getOptSrcFaultMsgName()) != null) {
                        Fault fault = portTypeOperation.getFault(serviceOperation.getOptSrcFaultMsgName());
                        Message messageFault = fault.getMessage();
                        QName qNameFault = new QName(serviceOperation.getOptFaultMsgNs(),
                                serviceOperation.getOptFaultMsgName());
                        messageFault.setQName(qNameFault);
                        fault.setMessage(messageFault);
                        portTypeOperation.removeFault(serviceOperation.getOptSrcFaultMsgName());
                        portTypeOperation.addFault(fault);
                        def.addNamespace("faulttns", serviceOperation.getOptFaultMsgName());

                    }
                    portTypeOperationListModify.add(portTypeOperation);
                }
            }
            // 修改 soap action, binding--operation--bindingOperationElement(替换SoapAction)--operation（替换BindingOperation）--binding（替换binding）
            Map bindingMap = def.getAllBindings();
            Iterator bindingItr = bindingMap.entrySet().iterator();
            while (bindingItr.hasNext()) {
                Map.Entry bindingEntry = (Map.Entry) bindingItr.next();
                def.removeBinding((QName) bindingEntry.getKey());
                javax.wsdl.Binding binding = (javax.wsdl.Binding) bindingEntry.getValue();
                List bindingOperations = binding.getBindingOperations();
                Iterator bindingOperationItr = bindingOperations.iterator();
                while (bindingOperationItr.hasNext()) {
                    BindingOperation bindingOperation = (BindingOperation) bindingOperationItr.next();
                    if(bindingOperation.getName().equals(serviceOperation.getOptSrcEnName())){
                        binding.getBindingOperations().remove(bindingOperation);
                        ExtensibilityElement bindingOperationElement = (ExtensibilityElement) bindingOperation.getExtensibilityElements().get(0);                   
                        ExtensibilityElement newBindingOperationElement = setSoapAction(bindingOperationElement,
                                serviceOperation.getOptSoapAction());
                        bindingOperation.removeExtensibilityElement(bindingOperationElement);
                        bindingOperation.addExtensibilityElement(newBindingOperationElement); 
                        binding.addBindingOperation(bindingOperation);
                    }
                }
                def.addBinding(binding);
            }

            WSDLWriter wirter = wsdlFactory.newWSDLWriter();
            wirter.writeWSDL(def, System.out);
        } catch (WSDLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static ExtensibilityElement setSoapAction(ExtensibilityElement extensibilityElement, String targetSoapAction) {

        if (extensibilityElement instanceof SOAP12OperationImpl) {
             ((SOAP12OperationImpl) extensibilityElement).setSoapActionURI(targetSoapAction);
            return extensibilityElement;

        } else if (extensibilityElement instanceof SOAPOperationImpl) {
             ((SOAPOperationImpl) extensibilityElement).setSoapActionURI(targetSoapAction);
             return extensibilityElement;

        }
        return null;
    }

}
