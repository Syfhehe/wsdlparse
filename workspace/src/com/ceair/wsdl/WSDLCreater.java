package com.ceair.wsdl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Binding;
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
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12OperationImpl;

public class WSDLCreater {

    public static void main(String args[]) {
        // ServiceOperation serviceOperation =
        // OracleDBUtil.selectServiceOperation(4);
        // ServiceVersion serviceVersion =
        // OracleDBUtil.selectServiceVersion(serviceOperation.getServiceVerId());
        // createWSDL(serviceOperation, serviceVersion);
        
        List<ServiceOperation> serviceOperationList = new ArrayList<ServiceOperation>();
        ServiceOperation serviceOperation1 = new ServiceOperation("getStockInfo", "getStockInfo", 
                "optInputMsgName1", "Yangzhili", "optOutputMsgName1", "Yangzhili", "fault1", "Yangzhili", "optSoapAction1", 
                "getStockInfoSoapIn", "www.cea.com", "getStockInfoSoapOut", "www.cea.com", "getStockInfoSoapFault", "www.cea.com", "http://webxml.com.cn/getStockInfo");
        ServiceOperation serviceOperation2 = new ServiceOperation("getInfoByMobilePhone", "getInfoByMobilePhone",
                "optInputMsgName2", "Yangzhili", "optOutputMsgName2", "Yangzhili", "fault2", "Yangzhili", "optSoapAction2",
                "getInfoByMobilePhoneSoapIn", "www.cea.com", "getInfoByMobilePhoneSoapOut", "www.cea.com", "getInfoByMobilePhoneSoapFault", "www.cea.com", "Yangzhili/getInfoByMobilePhone");
        serviceOperationList.add(serviceOperation1);
        serviceOperationList.add(serviceOperation2);
        ServiceVersion serviceVersion = new ServiceVersion(
                FileUtil.file2String(new File("./wsdlfile/SM1_withfault.wsdl"), "utf-8"));
        createWSDL(serviceOperationList, serviceVersion);

    }

    @SuppressWarnings("unchecked")
    public static void createWSDL(List<ServiceOperation> serviceOperationList, ServiceVersion serviceVersion) {
        try {
            // 读取wsdl原始数据
            String wsdlString = serviceVersion.getWsdlClob();
            InputStream in_withcode = new ByteArrayInputStream(wsdlString.getBytes("UTF-8"));
            
            // 读取刚才保存的wsdl文件 文件内容和数据库中的wsdl_clob一致
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader reader = wsdlFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);
            Definition def = reader.readWSDL("", new InputSource(in_withcode));
            
            //遍历整个PortTypes
            Iterator<Entry<QName, PortType>> portTypeItr = def.getAllPortTypes().entrySet().iterator();
            while (portTypeItr.hasNext()) {          
                Entry<QName, PortType> portTypeEntry = portTypeItr.next();
                PortType portType = (PortType) portTypeEntry.getValue();                                
                // 新的Operation List用来替换原来的Operation
                List<Operation> portTypeOperationListModify = new ArrayList<Operation>();
                //遍历porttype对应的Operation
                Iterator<Operation> portTypeOperationItr = portType.getOperations().iterator();
                while (portTypeOperationItr.hasNext()) {
                    Operation portTypeOperation = (OperationImpl) portTypeOperationItr.next();
                    // 根据wsdl的OperationName查找OperationList中对应的srvOpt
                    ServiceOperation srvOpt = getSvrOptByOptName(serviceOperationList, portTypeOperation.getName());
                    portTypeOperation = replaceOptElement(srvOpt, portTypeOperation);
                    portTypeOperationListModify.add(portTypeOperation);
                }
                portType = replaceOptsOfPortType(portType, portTypeOperationListModify);
            }
            
            // 修改 soap action,
            // binding--operation--bindingOperationElement(替换SoapAction)--operation（替换BindingOperation）--binding（替换binding）
            Map<QName, Binding> bindingMap = def.getAllBindings();
            Iterator<Entry<QName, Binding>> bindingItr = bindingMap.entrySet().iterator();
            while (bindingItr.hasNext()) {
                Entry<QName, Binding> bindingEntry = bindingItr.next();
                Binding binding = (Binding) bindingEntry.getValue();
                // 新的Operation List用来替换原来的Operation
                List<BindingOperation> bindingOperationListModify = new ArrayList<BindingOperation>();
                Iterator<Entry<QName, BindingOperation>> bindingOperationItr = binding.getBindingOperations().iterator();
                while (bindingOperationItr.hasNext()) {
                    BindingOperation bindingOperation = (BindingOperation) bindingOperationItr.next();
                    ServiceOperation serviceOperation = getSvrOptByOptName(serviceOperationList, bindingOperation.getName());
                    if (serviceOperation!=null) {
                        ExtensibilityElement bindingOperationElement = (ExtensibilityElement) bindingOperation
                                .getExtensibilityElements().get(0);
                        bindingOperationElement = setSoapAction(bindingOperationElement,
                                serviceOperation.getOptSoapAction());
                        bindingOperationListModify.add(bindingOperation);
                    }
                }
                binding = replaceOptsOfBinding(binding, bindingOperationListModify);
            }

            WSDLWriter wirter = wsdlFactory.newWSDLWriter();
            wirter.writeWSDL(def, System.out);
        } catch (WSDLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    //删除PortType中已有的Operations 增加修改完的Operations
    @SuppressWarnings("unchecked")
    private static PortType replaceOptsOfPortType(PortType portType, List<Operation> portTypeOptListModified){
        Iterator<Operation> portTypeItr = portType.getOperations().iterator();
        while(portTypeItr.hasNext()){
            portTypeItr.next();
            portTypeItr.remove();
        }
        Iterator<Operation> portTypeOptListItr = portTypeOptListModified.iterator();
        while(portTypeOptListItr.hasNext()){
            portType.addOperation((Operation) portTypeOptListItr.next());
        }
        return portType;      
    }
    
    //删除binding中已有的Binding Operations 增加修改完的Operations
    @SuppressWarnings("unchecked")
    private static Binding replaceOptsOfBinding(Binding binding, List<BindingOperation> bindingOperationListModify){
        Iterator<Entry<QName, BindingOperation>> bindingOptsItr = binding.getBindingOperations().iterator();
        while(bindingOptsItr.hasNext()){
            bindingOptsItr.next();
            bindingOptsItr.remove();
        }
        Iterator<BindingOperation> bindingOperationListItr = bindingOperationListModify.iterator();
        while(bindingOperationListItr.hasNext()){
            binding.addBindingOperation((BindingOperation) bindingOperationListItr.next());
        }
        return binding;      
    }
    
    //替换Operation中的input output fault信息，但是要是修改的namespace没有定义 那就会报错
    @SuppressWarnings("unchecked")
    private static Operation replaceOptElement(ServiceOperation srvOpt, Operation portTypeOperation){
        if (srvOpt != null) {
            // 修改 input message
            if (portTypeOperation.getInput() != null) {
                Message messageInput = portTypeOperation.getInput().getMessage();
                QName qNameInput = new QName(srvOpt.getOptInputMsgNs(), srvOpt.getOptInputMsgName());
                messageInput.setQName(qNameInput);
            }
            // 修改 output message
            if (portTypeOperation.getOutput() != null) {
                Message messageOutput = portTypeOperation.getOutput().getMessage();
                QName qNameOutput = new QName(srvOpt.getOptOutputMsgNs(), srvOpt.getOptOutputMsgName());
                messageOutput.setQName(qNameOutput);
            }
            // 修改 fault message
            Map<QName, Fault> faultMap = portTypeOperation.getFaults();
            Iterator<Entry<QName, Fault>> faultItr = faultMap.entrySet().iterator();
            while (faultItr.hasNext()) {
                Entry<QName, Fault> faultEntry = faultItr.next();
                Fault fault = (Fault) faultEntry.getValue();
                Message faultMessage = fault.getMessage();
                faultMessage.setQName(new QName(srvOpt.getOptFaultMsgNs(), srvOpt.getOptFaultMsgName()));
                break;
            }
        }
        return portTypeOperation;
    }
    //从输入的ServiceOperation List中找到wsdl原始代码里对应的名字的Operation
    private static ServiceOperation getSvrOptByOptName(List<ServiceOperation> serviceOperationList, String optName) {
        Iterator<ServiceOperation> iterator = serviceOperationList.iterator();
        while (iterator.hasNext()) {
            ServiceOperation serviceOperation = iterator.next();
            if (serviceOperation.getOptSrcEnName().equals(optName)) {
                return serviceOperation;
            }
        }
        return null;
    }

    //修改Soap_Action的值
    private static ExtensibilityElement setSoapAction(ExtensibilityElement extensibilityElement,
            String targetSoapAction) {
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
