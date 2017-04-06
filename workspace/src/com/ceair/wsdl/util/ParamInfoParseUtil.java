package com.ceair.wsdl.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ceair.wsdl.domain.EsbParamInfo;

public class ParamInfoParseUtil {

    public static final String COMPLEX_TPYE = "complexType";
    public static final String ELEMENT = "element";
    public static final String ELEMENT_MINOCCURS = "minOccurs";
    public static final String ELEMENT_MAXOCCURS = "maxOccurs";
    public static final String ELEMENT_NAME = "name";
    public static final String ELEMENT_TYPE = "type";

    public static void main(String[] args) throws Exception {
        List<String> filelist = FileUtil.getFileList("E:\\1learningmaterials\\maven\\WSDL\\workspace\\wsdlfile\\todo",
                new ArrayList<String>());
        Iterator<String> fileNameItr = filelist.iterator();
        while (fileNameItr.hasNext()) {
            int index = 1;
            String fileName = fileNameItr.next();
            System.out.println("**************************************************************************************************");
            System.out.println("***********************fileName=" + fileName + "******************************");
            System.out.println("**************************************************************************************************");

            List<EsbParamInfo> paramInfosList = parseParamInfo(fileName);
            Iterator<EsbParamInfo> iterator = paramInfosList.iterator();
            while (iterator.hasNext()) {
                EsbParamInfo paramInfo = iterator.next();
                System.out.println("Object" + index + ":");
                System.out.println("--------------------------------------");

                if (paramInfo.getParentParam() != null) {
                    System.out.println("parentParamName:" + paramInfo.getParentParam().getNodeName());
                }
                System.out.println("nodeName:" + paramInfo.getNodeName());
                System.out.println("dataType:" + paramInfo.getDataType());
                System.out.println("minoccurs:" + paramInfo.getMinoccurs());
                System.out.println("maxoccurs:" + paramInfo.getMaxoccurs());
                System.out.println("--------------------------------------");
                index++;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static List<EsbParamInfo> parseParamInfo(String filepath) throws Exception {

        
        List<EsbParamInfo> paramInfosList = new ArrayList<EsbParamInfo>();
        List<EsbParamInfo> paramInfosFromSchemaList = new ArrayList<EsbParamInfo>();
        List<EsbParamInfo> paramInfosFromMsgList = new ArrayList<EsbParamInfo>();

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(filepath));
        // 获取根元素
        Element root = document.getRootElement();
        System.out.println("Root: " + root.getName());

        // 获取types
        Element types = root.element("types");
        // 考虑多个schema的情况
        if(types!=null){
            List<Element> schemaList = types.elements();
            System.out.println("schemaElementList count: " + schemaList.size());
            Iterator<Element> schemaItr = schemaList.iterator();
            // step1 遍历每个schema下的parameter
            while (schemaItr.hasNext()) {
                Element schema = schemaItr.next();
                List<Element> paramsList = schema.elements();
                System.out.println("paramsList count: " + paramsList.size());
                Iterator<Element> paramItr = paramsList.iterator();
                // 解析每个element或者complexType
                while (paramItr.hasNext()) {
                    Element parameter = paramItr.next();
                    parseParam(parameter, paramInfosFromSchemaList, new EsbParamInfo());
                }
            }
        }
        

        //step2 遍历所有的message下面的part 为了应对直接将参数写在message里的情况
        List<Element> messageList = root.elements("message");
        Iterator<Element> iterator = messageList.iterator();
        while (iterator.hasNext()) {
            Element message = (Element) iterator.next();
            List<Element> partList = message.elements("part");
            Iterator<Element> partItr = partList.iterator();
            while (partItr.hasNext()) {
                Element part = (Element) partItr.next();
                EsbParamInfo tempParamInfo = new EsbParamInfo();
                for (int attrCount = 0; attrCount < part.attributeCount(); attrCount++) { 
                    Attribute attr = part.attribute(attrCount);
                    setData(tempParamInfo, attr);
                }
                if(tempParamInfo.getDataType()!=null){
                    paramInfosFromMsgList.add(tempParamInfo);

                }
            }
        }
        
        //step3 删除掉重复的内容 因为有的在message-part里的参数不是公共参数 是输入输出参数
        Iterator<EsbParamInfo> paramInfoFromSchemaItr =  paramInfosFromSchemaList.iterator();
        while (paramInfoFromSchemaItr.hasNext()) {
            EsbParamInfo esbParamFromSchemaInfo = (EsbParamInfo) paramInfoFromSchemaItr.next();
            String nodeName = esbParamFromSchemaInfo.getNodeName();
            Iterator<EsbParamInfo> paramInfoFromMsgItr =  paramInfosFromMsgList.iterator();
            while (paramInfoFromMsgItr.hasNext()) {
                EsbParamInfo esbParamFromMsgInfo = (EsbParamInfo) paramInfoFromMsgItr.next();
                if(esbParamFromMsgInfo.getDataType()!=null){
                    if(esbParamFromMsgInfo.getDataType().indexOf(nodeName)!=-1){
                        paramInfoFromMsgItr.remove();
                    }
                }
            }

        }
        paramInfosList.addAll(paramInfosFromSchemaList);
        paramInfosList.addAll(paramInfosFromMsgList);
        
        //step4 遍历所有对象 若父类对象存在 但是名字是空的  就移除父类对象
        Iterator<EsbParamInfo> allParamItr = paramInfosList.iterator();
        while (allParamItr.hasNext()) {
            EsbParamInfo esbParamInfo = (EsbParamInfo) allParamItr.next();
            if(esbParamInfo.getParentParam()!=null&&esbParamInfo.getParentParam().getNodeName()==null){
                esbParamInfo.setParentParam(null);
            }            
        }
        return paramInfosList;
    }

    @SuppressWarnings("unchecked")
    private static void parseParam(Element parameter, List<EsbParamInfo> paramInfosList, EsbParamInfo parentParamInfo) {
        if (parameter.getName() != null) {
            if (parameter.getName().equals(ELEMENT)) {
                // 新建一个对
                EsbParamInfo tempParamInfo = new EsbParamInfo();
                for (int attrCount = 0; attrCount < parameter.attributeCount(); attrCount++) {
                    Attribute attr = parameter.attribute(attrCount);
                    setData(tempParamInfo, attr);
                }
                if (parentParamInfo.getNodeName() == null && parentParamInfo.getDataType() == null) {
                    tempParamInfo.setParentParam(parentParamInfo.getParentParam());
                } else {
                    tempParamInfo.setParentParam(parentParamInfo);
                }

                List<Element> paramList = parameter.elements();
                Iterator<Element> paramItr = paramList.iterator();
                // 将对象添加到list中
                paramInfosList.add(tempParamInfo);

                while (paramItr.hasNext()) {
                    Element parameterTemp = paramItr.next();
                    parseParam(parameterTemp, paramInfosList, tempParamInfo);
                }

            } else if (parameter.getName().equals(COMPLEX_TPYE)) {
                EsbParamInfo tempParamInfo = new EsbParamInfo();
                for (int attrCount = 0; attrCount < parameter.attributeCount(); attrCount++) {
                    Attribute attr = parameter.attribute(0);
                    setData(tempParamInfo, attr);
                    paramInfosList.add(tempParamInfo);
                }
                tempParamInfo.setParentParam(parentParamInfo);

                List<Element> sequenceList = parameter.elements();
                Iterator<Element> sequenceItr = sequenceList.iterator();
                while (sequenceItr.hasNext()) {
                    Element sequence = sequenceItr.next();
                    if (sequence.getQName().getName().equals("sequence")) {
                        List<Element> elementList = sequence.elements();
                        Iterator<Element> elementItr = elementList.iterator();
                        while (elementItr.hasNext()) {
                            Element elementTemp = elementItr.next();
                            parseParam(elementTemp, paramInfosList, tempParamInfo);
                        }
                    }
                }
            }
        }
    }

    private static void setData(EsbParamInfo paramInfo, Attribute attr) {
        if (attr.getQName().getName().equals(ELEMENT_MAXOCCURS)) {
            paramInfo.setMaxoccurs(attr.getValue());
        } else if (attr.getQName().getName().equals(ELEMENT_MINOCCURS)) {
            paramInfo.setMinoccurs(attr.getValue());
        } else if (attr.getQName().getName().equals(ELEMENT_NAME)) {
            paramInfo.setNodeName(attr.getValue());
        } else if (attr.getQName().getName().equals(ELEMENT_TYPE)) {
            paramInfo.setDataType(attr.getValue());
        }
    }

}
