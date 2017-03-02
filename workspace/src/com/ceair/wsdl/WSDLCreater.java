package com.ceair.wsdl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import com.ceair.wsdl.domain.ServiceOperation;
import com.ceair.wsdl.domain.ServiceVersion;
import com.ceair.wsdl.jdbc.OracleDBUtil;
import com.ibm.wsdl.OperationImpl;

import oracle.jpub.runtime.OracleDataUtil;

public class WSDLCreater {
    
    public static void main(String args[]){
//        ServiceOperation serviceOperation = OracleDBUtil.
//        createWSDL();
    }

    public static void createWSDL(ServiceOperation serviceOperation, String wsdlLocation) {

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
                Iterator<Operation> portTypeOperationItr = portTypeOperationList.iterator();
                while (portTypeOperationItr.hasNext()) {
                    
                    Operation portTypeOperation = (OperationImpl) portTypeOperationItr.next();
                    System.out.println("Operation Name:"+portTypeOperation.getName());
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
            
            WSDLWriter wirter = wsdlFactory.newWSDLWriter();
            wirter.writeWSDL(def, System.out);
        } catch (WSDLException e) {
            e.printStackTrace();
        }

    }

}
