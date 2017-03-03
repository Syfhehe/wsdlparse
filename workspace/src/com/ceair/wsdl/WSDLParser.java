package com.ceair.wsdl;

import javax.wsdl.*;
import javax.wsdl.extensions.*;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.QName;

import com.ceair.wsdl.domain.ServiceOperation;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12AddressImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12BindingImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12OperationImpl;

import java.net.URL;
import java.util.*;

public class WSDLParser {

    public static List<ServiceOperation> parseWSDL(String wsdlLocation) {

        List<ServiceOperation> srvOptList = new ArrayList<ServiceOperation>();

        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);
            // read wsdl file
            Definition def = reader.readWSDL(wsdlLocation);

            Map serviceMap = def.getAllServices();
            Iterator serviceItr = serviceMap.entrySet().iterator();
            String soapActionURI = null;
            String transportURI = null;
            URL addressURI = null;

            while (serviceItr.hasNext()) {
                Map.Entry svcEntry = (Map.Entry) serviceItr.next();
                Service svc = (Service) svcEntry.getValue();
                Map portMap = svc.getPorts();
                Iterator portItr = portMap.entrySet().iterator();
                while (portItr.hasNext()) {
                    Map.Entry portEntry = (Map.Entry) portItr.next();
                    Port port = (Port) portEntry.getValue();
                    ExtensibilityElement extensibilityElement = (ExtensibilityElement) port.getExtensibilityElements()
                            .get(0);
                    addressURI = new URL(getAddressUrl(extensibilityElement));
                    System.out.println("addressURI:" + addressURI);
                    Binding binding = port.getBinding();
                    ExtensibilityElement bindingElement = (ExtensibilityElement) binding.getExtensibilityElements()
                            .get(0);
                    transportURI = getTransportURI(bindingElement);
                    System.out.println("transportURI:" + transportURI);
                    List bindingOperations = binding.getBindingOperations();
                    Iterator bindingOperationItr = bindingOperations.iterator();
                    while (bindingOperationItr.hasNext()) {
                        BindingOperation bindingOperation = (BindingOperation) bindingOperationItr.next();
                        ExtensibilityElement bindingOperationElement = (ExtensibilityElement) bindingOperation
                                .getExtensibilityElements().get(0);
                        soapActionURI = getSOAPActionUrl(bindingOperationElement);
                        System.out.println("soapActionURI:" + soapActionURI);
                        System.out.println("bindingOperationName:" + bindingOperation.getName().toString());
                    }
                }
            }

            Map portTypeMap = def.getAllPortTypes();
            Iterator portTypeItr = portTypeMap.entrySet().iterator();
            while (portTypeItr.hasNext()) {
                Map.Entry portTypeEntry = (Map.Entry) portTypeItr.next();
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

                    Map faultMap = portTypeOperation.getFaults();
                    Iterator faultItr = faultMap.entrySet().iterator();
                    while (faultItr.hasNext()) {
                        Fault fault = (Fault) faultItr.next();
                        System.out.println("FaultNameSpace:" + fault.getMessage().getQName().getNamespaceURI());
                        System.out.println("FaultName:" + fault.getMessage().getQName().getLocalPart());
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getTransportURI(ExtensibilityElement bindingElement) {
        if (bindingElement instanceof SOAPBindingImpl) {
            return ((SOAPBindingImpl) bindingElement).getTransportURI();
        } else if (bindingElement instanceof SOAP12BindingImpl) {
            return ((SOAP12BindingImpl) bindingElement).getTransportURI();
        }
        return null;
    }

    private static String getSOAPActionUrl(ExtensibilityElement extensibilityElement) {
        if (extensibilityElement instanceof SOAP12Operation) {
            return ((SOAP12Operation) extensibilityElement).getSoapActionURI();
        } else if (extensibilityElement instanceof SOAPOperation) {
            return ((SOAPOperation) extensibilityElement).getSoapActionURI();
        } else if (extensibilityElement instanceof SOAP12OperationImpl) {
            return ((SOAP12OperationImpl) extensibilityElement).getSoapActionURI();
        } else if (extensibilityElement instanceof SOAPOperationImpl) {
            return ((SOAPOperationImpl) extensibilityElement).getSoapActionURI();
        }
        return null;
    }

    private static String getAddressUrl(ExtensibilityElement extensibilityElement) {
        if (extensibilityElement instanceof SOAP12AddressImpl) {
            return ((SOAP12AddressImpl) extensibilityElement).getLocationURI();
        } else if (extensibilityElement instanceof SOAP12Address) {
            return ((SOAP12Address) extensibilityElement).getLocationURI();
        } else if (extensibilityElement instanceof SOAPAddressImpl) {
            return ((SOAPAddressImpl) extensibilityElement).getLocationURI();
        } else if (extensibilityElement instanceof SOAPAddress) {
            return ((SOAPAddress) extensibilityElement).getLocationURI();
        } else {
            return null;
        }
    }

}