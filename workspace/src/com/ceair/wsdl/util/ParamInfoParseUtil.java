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

        int index = 1;
        String filepath = "./wsdlfile/M1.wsdl";
        List<EsbParamInfo> paramInfosList = parseParamInfo(filepath);
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

    @SuppressWarnings("unchecked")
    public static List<EsbParamInfo> parseParamInfo(String filepath) throws Exception {

        List<EsbParamInfo> paramInfosList = new ArrayList<EsbParamInfo>();

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(filepath));
        // 获取根元素
        Element root = document.getRootElement();
        System.out.println("Root: " + root.getName());

        // 获取types
        Element types = root.element("types");
        // 考虑多个schema的情况
        List<Element> schemaList = types.elements();
        System.out.println("schemaElementList count: " + schemaList.size());
        Iterator<Element> schemaItr = schemaList.iterator();
        // 遍历每个schema下的parameter
        while (schemaItr.hasNext()) {
            Element schema = schemaItr.next();
            List<Element> paramsList = schema.elements();
            System.out.println("paramsList count: " + paramsList.size());
            Iterator<Element> paramItr = paramsList.iterator();
            // 解析每个element或者complexType
            while (paramItr.hasNext()) {
                Element parameter = paramItr.next();
                parseParam(parameter, paramInfosList, new EsbParamInfo());
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
                    System.out.println("++++++++++++++++++++++++++++++++++++++++");

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
