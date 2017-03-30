package com.ceair.wsdl;

import javax.wsdl.*;
import javax.wsdl.extensions.*;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.QName;

import com.ceair.wsdl.domain.ServiceOperation;
import com.ceair.wsdl.domain.ServiceVersion;
import com.ceair.wsdl.util.FileUtil;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12AddressImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12BindingImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12OperationImpl;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class WSDLParser {
    
    public static void main(String args[]){

        List<String> filelist = FileUtil.getFileList("E:\\1learningmaterials\\maven\\WSDL\\workspace\\wsdlfile\\todo", new ArrayList<String>());
        Iterator<String> fileNameItr = filelist.iterator();
        while(fileNameItr.hasNext()){
            String fileName = fileNameItr.next();
            Map<String, ServiceOperation> map = WSDLParser.parseWSDL(fileName,7,5);
            System.out.println("Map Size:"+map.size());
            Iterator<Entry<String, ServiceOperation>> iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<String, ServiceOperation> serviceOperationEntry = iterator.next();
                ServiceOperation serviceOperation = (ServiceOperation) serviceOperationEntry.getValue();
                System.out.println("serviceOperation optId:"+serviceOperation.getOptId());
                System.out.println("serviceOperation originalOptId:"+serviceOperation.getOriginalOptId());
                System.out.println("serviceOperation serviceVerId:"+serviceOperation.getServiceVerId());
                System.out.println("serviceOperation optEnName:"+serviceOperation.getOptEnName());
                System.out.println("serviceOperation optSrcEnName:"+serviceOperation.getOptSrcEnName());
                System.out.println("serviceOperation protocolType:"+serviceOperation.getProtocolType());
                System.out.println("serviceOperation formateType:"+serviceOperation.getFormateType());
                System.out.println("serviceOperation optInputMsgName:"+serviceOperation.getOptInputMsgName());
                System.out.println("serviceOperation optInputMsgNs:"+serviceOperation.getOptInputMsgNs());
                System.out.println("serviceOperation optOutputMsgName:"+serviceOperation.getOptOutputMsgName());
                System.out.println("serviceOperation optOutputMsgNs:"+serviceOperation.getOptOutputMsgNs());
                System.out.println("serviceOperation optFaultMsgName:"+serviceOperation.getOptFaultMsgName());
                System.out.println("serviceOperation optFaultMsgNs:"+serviceOperation.getOptFaultMsgNs());   
                System.out.println("serviceOperation optSoapAction:"+serviceOperation.getOptSoapAction());            
                System.out.println("serviceOperation optSrcInputMsgName:"+serviceOperation.getOptSrcInputMsgName());
                System.out.println("serviceOperation optSrcInputMsgNs:"+serviceOperation.getOptSrcInputMsgNs());
                System.out.println("serviceOperation optSrcOutputMsgName:"+serviceOperation.getOptSrcOutputMsgName());
                System.out.println("serviceOperation optSrcOutputMsgNs:"+serviceOperation.getOptOutputMsgNs());
                System.out.println("serviceOperation optSrcFaultMsgName:"+serviceOperation.getOptSrcFaultMsgName());
                System.out.println("serviceOperation optSrcFaultMsgNs:"+serviceOperation.getOptSrcFaultMsgNs());
                System.out.println("serviceOperation optSrcSoapAction:"+serviceOperation.getOptSrcSoapAction());
                System.out.println("serviceOperation endpoint:"+serviceOperation.getEndpoint());           
            }
        }
       
        
    }
    
    public static void saveSrvVerWSDLClob(String wsdlLocation){
        ServiceVersion serviceVersion = new ServiceVersion();
        File file = new File(wsdlLocation);
        String wsdlclob = FileUtil.file2String(file, "utf-8");
        serviceVersion.setWsdlClob(wsdlclob);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, ServiceOperation> parseWSDL(String wsdlLocation, int srvID, int srvVerID) {

        Map<String, ServiceOperation> srvOptMap = new HashMap<String, ServiceOperation>();
        
        try {
            //对应SOAP_ACTION, PROTOCOL_TYPE, ENDPOINT
            String soapActionURI = null;
            String transportURI = null;
            String addressURI = null;
            //读取 WSDL到def
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);
            
            Definition def = reader.readWSDL(wsdlLocation);
            //解析service————port（获取ENDPOINT）————binding（获取PROTOCOL_TYPE）——operation（获取 SOAP_ACTION）            
                      
            Map<QName, Service> serviceMap = def.getAllServices();
            Iterator<Entry<QName, Service>> serviceItr = serviceMap.entrySet().iterator();
            while (serviceItr.hasNext()) {
                Entry<QName, Service> svcEntry = serviceItr.next();
                Service svc = (Service) svcEntry.getValue();
                Map<String, Binding> portMap = svc.getPorts();
                Iterator<Entry<String, Binding>> portItr = portMap.entrySet().iterator();
                while (portItr.hasNext()) {
                    Entry<String, Binding> portEntry =  portItr.next();
                    Port port = (Port) portEntry.getValue();
                    ExtensibilityElement extensibilityElement = (ExtensibilityElement) port.getExtensibilityElements()
                            .get(0);
                    addressURI = getAddressUrl(extensibilityElement);
                    System.out.println("addressURI:" + addressURI);                   
                    
                    Binding binding = port.getBinding();
                    ExtensibilityElement bindingElement = (ExtensibilityElement) binding.getExtensibilityElements()
                            .get(0);
                    transportURI = getTransportURI(bindingElement);
                    System.out.println("transportURI:" + transportURI);
                    List<BindingOperation> bindingOperations = binding.getBindingOperations();
                    Iterator<BindingOperation> bindingOperationItr = bindingOperations.iterator();
                    while (bindingOperationItr.hasNext()) {
                        ServiceOperation tempServiceOperation = new ServiceOperation();
                        BindingOperation bindingOperation = (BindingOperation) bindingOperationItr.next();
                        ExtensibilityElement bindingOperationElement = (ExtensibilityElement) bindingOperation
                                .getExtensibilityElements().get(0);
                        soapActionURI = getSOAPActionUrl(bindingOperationElement);

                        System.out.println("soapActionURI:" + soapActionURI);
                        System.out.println("bindingOperationName:" + bindingOperation.getName().toString());
                        String optEnName = bindingOperation.getName().toString();

                        tempServiceOperation.setOptSrcSoapAction(soapActionURI);
                        tempServiceOperation.setOptSoapAction(soapActionURI);
                        String protocolTypeString = getProtocolTypeString(addressURI);
                        tempServiceOperation.setProtocolType(protocolTypeString);
                        tempServiceOperation.setEndpoint(addressURI);
                        tempServiceOperation.setFormateType("SOAP");
                        tempServiceOperation.setOptSrcEnName(bindingOperation.getName().toString());
                        tempServiceOperation.setOptEnName(optEnName);
                        tempServiceOperation.setServiceVerId(srvVerID);
                        tempServiceOperation.setOptId(srvID);
                        tempServiceOperation.setOriginalOptId(srvID);
                        srvOptMap.put(optEnName, tempServiceOperation);
                    }
                }
            }
            //解析service————port（获取ENDPOINT）————binding（获取PROTOCOL_TYPE）——operation（获取 SOAP_ACTION）
            Map<QName, PortType> portTypeMap = def.getAllPortTypes();
            Iterator<Entry<QName, PortType>> portTypeItr = portTypeMap.entrySet().iterator();
            while (portTypeItr.hasNext()) {
                Entry<QName, PortType> portTypeEntry =  portTypeItr.next();
                PortType portType = (PortType) portTypeEntry.getValue();
                List<Operation> portTypeOperationList = portType.getOperations();
                Iterator<Operation> portTypeOperationItr = portTypeOperationList.iterator();
                while (portTypeOperationItr.hasNext()) {   
                    Operation portTypeOperation = (OperationImpl) portTypeOperationItr.next();
                    
                    ServiceOperation tempServiceOperation = srvOptMap.get(portTypeOperation.getName());
                    if(tempServiceOperation==null){
                        tempServiceOperation = new ServiceOperation();
                    }
                    
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
                    while (faultItr.hasNext()) {
                        Entry<QName, Fault> faultEntry =  faultItr.next();
                        Fault fault = (Fault) faultEntry.getValue();
                        System.out.println("FaultNameSpace:" + fault.getMessage().getQName().getNamespaceURI());
                        System.out.println("FaultName:" + fault.getMessage().getQName().getLocalPart());
                        
                        tempServiceOperation.setOptSrcFaultMsgNs(fault.getMessage().getQName().getNamespaceURI());
                        tempServiceOperation.setOptFaultMsgNs(fault.getMessage().getQName().getNamespaceURI());                    
                        tempServiceOperation.setOptSrcFaultMsgName(fault.getMessage().getQName().getLocalPart());
                        tempServiceOperation.setOptFaultMsgName(fault.getMessage().getQName().getLocalPart());
                        
                        break;
                    }                    
                    tempServiceOperation.setOptSrcInputMsgNs(qnameInput.getNamespaceURI());
                    tempServiceOperation.setOptInputMsgNs(qnameInput.getNamespaceURI());                    
                    tempServiceOperation.setOptSrcInputMsgName(qnameInput.getLocalPart());
                    tempServiceOperation.setOptInputMsgName(qnameInput.getLocalPart());
                    
                    tempServiceOperation.setOptSrcOutputMsgNs(qnameOutput.getNamespaceURI());
                    tempServiceOperation.setOptOutputMsgNs(qnameOutput.getNamespaceURI());                    
                    tempServiceOperation.setOptSrcOutputMsgName(qnameOutput.getLocalPart());
                    tempServiceOperation.setOptOutputMsgName(qnameOutput.getLocalPart());                 
                  
                    srvOptMap.put(portTypeOperation.getName(), tempServiceOperation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return srvOptMap;
    }

    private static String getProtocolTypeString(String addressURI) {
        String[] temp = addressURI.split(":");        
        return temp[0];
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
        if (extensibilityElement instanceof SOAP12OperationImpl) {
            return ((SOAP12OperationImpl) extensibilityElement).getSoapActionURI();
        } else if (extensibilityElement instanceof SOAPOperationImpl) {
            return ((SOAPOperationImpl) extensibilityElement).getSoapActionURI();
        }
        return null;
    }

    private static String getAddressUrl(ExtensibilityElement extensibilityElement) {
        if (extensibilityElement instanceof SOAP12AddressImpl) {
            return ((SOAP12AddressImpl) extensibilityElement).getLocationURI();
        } else if (extensibilityElement instanceof SOAPAddressImpl) {
            return ((SOAPAddressImpl) extensibilityElement).getLocationURI();
        } else {
            return null;
        }
    }

}