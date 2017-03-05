package com.ceair.wsdl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.naming.Binding;
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

import com.ceair.wsdl.domain.ServiceOperation;
import com.ceair.wsdl.domain.ServiceVersion;
import com.ceair.wsdl.jdbc.FileUtil;
import com.ceair.wsdl.jdbc.OracleDBUtil;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12BindingImpl;

import oracle.jpub.runtime.OracleDataUtil;

public class WSDLCreater {

    public static void main(String args[]) {
        ServiceOperation serviceOperation = OracleDBUtil.selectServiceOperation(4);
        createWSDL(serviceOperation);
    }

    public static void createWSDL(ServiceOperation serviceOperation) {

        ServiceVersion serviceVersion = OracleDBUtil.selectServiceVersion(serviceOperation.getServiceVerId());
        String wsdlString = serviceVersion.getWsdlClob();
        String wsdlLocation = serviceVersion.getWsdlLocation();
        FileUtil.string2File(wsdlString, wsdlLocation);

        try {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader reader = wsdlFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition def = reader.readWSDL(wsdlLocation);

            Map portTypeMap = def.getAllPortTypes();
            Iterator portTypeItr = portTypeMap.entrySet().iterator();
            while (portTypeItr.hasNext()) {
                Map.Entry portTypeEntry = (Map.Entry) portTypeItr.next();
                PortType portType = (PortType) portTypeEntry.getValue();
                List<Operation> portTypeOperationList = portType.getOperations();
                // new port list
                List<Operation> portTypeOperationListModify = new ArrayList();
                Iterator<Operation> portTypeOperationItr = portTypeOperationList.iterator();
                while (portTypeOperationItr.hasNext()) {
                    Operation portTypeOperation = (OperationImpl) portTypeOperationItr.next();
                    System.out.println("Operation Name:" + portTypeOperation.getName());
                    // modify input message
                    if (portTypeOperation.getInput() != null) {
                        Message messageInput = portTypeOperation.getInput().getMessage();
                        QName qNameInput = new QName(serviceOperation.getOptInputMsgNs(),
                                serviceOperation.getOptInputMsgName());
                        messageInput.setQName(qNameInput);
                        portTypeOperation.getInput().setMessage(messageInput);

                        def.addNamespace("inputtns", serviceOperation.getOptInputMsgNs());
                    }
                    // modify output message
                    if (portTypeOperation.getOutput() != null) {
                        Message messageOutput = portTypeOperation.getOutput().getMessage();
                        QName qNameOutput = new QName(serviceOperation.getOptOutputMsgNs(),
                                serviceOperation.getOptOutputMsgName());
                        messageOutput.setQName(qNameOutput);
                        portTypeOperation.getOutput().setMessage(messageOutput);

                        def.addNamespace("outputtns", serviceOperation.getOptOutputMsgNs());
                    }
                    // modify fault message
                    if (portTypeOperation.getFault(serviceOperation.getOptSrcFaultMsgName()) != null) {
                        Fault fault = portTypeOperation.getFault(serviceOperation.getOptSrcFaultMsgName());
                        Message messageFault = fault.getMessage();
                        QName qNameFault = new QName(serviceOperation.getOptFaultMsgNs(),
                                serviceOperation.getOptFaultMsgName());
                        messageFault.setQName(qNameFault);
                        fault.setMessage(messageFault);
                        portTypeOperation.removeFault(serviceOperation.getOptSrcFaultMsgName());
                        portTypeOperation.addFault(fault);
                    }
                    portTypeOperationListModify.add(portTypeOperation);
                }
            }
            // replace
            Map bindingMap = def.getAllBindings();
            Iterator bindingItr = bindingMap.entrySet().iterator();
            while (bindingItr.hasNext()) {
                Map.Entry bindingEntry = (Map.Entry) bindingItr.next();
                def.removeBinding((QName) bindingEntry.getKey());
                javax.wsdl.Binding binding = (javax.wsdl.Binding) bindingEntry.getValue();
                if (binding.getQName().getLocalPart().equals(serviceOperation.getOptSrcEnName()) ) {
                    ExtensibilityElement bindingElement = (ExtensibilityElement) binding.getExtensibilityElements()
                            .get(0);
                    ExtensibilityElement newBindingElement = setTransportURI(bindingElement,
                            serviceOperation.getOptSoapAction());
                    binding.removeExtensibilityElement(bindingElement);
                    binding.addExtensibilityElement(newBindingElement);
                }
                def.addBinding(binding);
            }
            
            WSDLWriter wirter = wsdlFactory.newWSDLWriter();
            wirter.writeWSDL(def, System.out);
        } catch (WSDLException e) {
            e.printStackTrace();
        }

    }

    private static ExtensibilityElement setTransportURI(ExtensibilityElement bindingElement, String targetSoapAction) {
        if (bindingElement instanceof SOAPBindingImpl) {
            ((SOAPBindingImpl) bindingElement).setTransportURI(targetSoapAction);
            return bindingElement;
        } else if (bindingElement instanceof SOAP12BindingImpl) {
            ((SOAP12BindingImpl) bindingElement).setTransportURI(targetSoapAction);
            return bindingElement;
        }
        return null;
    }

}
