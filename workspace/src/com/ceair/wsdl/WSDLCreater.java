package com.ceair.wsdl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import com.ceair.wsdl.domain.ServiceOperation;
import com.ceair.wsdl.domain.ServiceVersion;
import com.ceair.wsdl.jdbc.FileUtil;
import com.ceair.wsdl.jdbc.OracleDBUtil;
import com.ibm.wsdl.OperationImpl;

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

            // QName qNameInput = new
            // QName(serviceOperation.getOptSrcInputMsgNs(),
            // serviceOperation.getOptSrcInputMsgName());
            // def.removeMessage(qNameInput);
            // QName qNameOutput = new
            // QName(serviceOperation.getOptSrcOutputMsgNs(),
            // serviceOperation.getOptSrcOutputMsgName());
            // def.removeMessage(qNameOutput);
            // QName qNameFault = new
            // QName(serviceOperation.getOptSrcFaultMsgNs(),
            // serviceOperation.getOptSrcFaultMsgName());
            // def.removeMessage(qNameFault);

            Map portTypeMap = def.getAllPortTypes();
            Iterator portTypeItr = portTypeMap.entrySet().iterator();
            while (portTypeItr.hasNext()) {
                Map.Entry portTypeEntry = (Map.Entry) portTypeItr.next();
                PortType portType = (PortType) portTypeEntry.getValue();
                List<Operation> portTypeOperationList = portType.getOperations();

                List<Operation> portTypeOperationListModify = new ArrayList<>();

                Iterator<Operation> portTypeOperationItr = portTypeOperationList.iterator();
                while (portTypeOperationItr.hasNext()) {
                    Operation portTypeOperation = (OperationImpl) portTypeOperationItr.next();
                    System.out.println("Operation Name:" + portTypeOperation.getName());
                    // test
                    Message messageInput = portTypeOperation.getInput().getMessage();
                    QName qNameInput = new QName(serviceOperation.getOptInputMsgNs(),
                            serviceOperation.getOptInputMsgName());
                    messageInput.setQName(qNameInput);
                    portTypeOperation.getInput().setMessage(messageInput);
                    
                    def.addNamespace("inputtns", serviceOperation.getOptInputMsgNs());
    

                    Message messageOutput = portTypeOperation.getOutput().getMessage();
                    QName qNameOutput = new QName(serviceOperation.getOptOutputMsgNs(),
                            serviceOperation.getOptOutputMsgName());
                    messageOutput.setQName(qNameOutput);
                    portTypeOperation.getOutput().setMessage(messageOutput);

                    def.addNamespace("outputtns", serviceOperation.getOptOutputMsgNs());
                    
                    Fault fault = portTypeOperation.getFault(serviceOperation.getOptSrcFaultMsgName());
                    Iterator faultItr = faultMap.entrySet().iterator();
                    //fault按照一个做
                    while (faultItr.hasNext()) {
                        Fault fault = (Fault) faultItr.next();
                        Message messageFault = fault.getMessage();
                        QName qNameFault = new QName(serviceOperation.getOptFaultMsgNs(),
                                serviceOperation.getOptFaultMsgName());
                        messageFault.setQName(qNameFault);
                        fault.setMessage(messageFault);
                        faultMap.clear();
                        
                        break;
                    }
                    
                    
                    portTypeOperationListModify.add(portTypeOperation);
                    
                    

                }
            }

            WSDLWriter wirter = wsdlFactory.newWSDLWriter();
            wirter.writeWSDL(def, System.out);
        } catch (WSDLException e) {
            e.printStackTrace();
        }

    }

    public static String getKeyByValue(Map<String, String> map, String value) {
        for (Entry entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

}
