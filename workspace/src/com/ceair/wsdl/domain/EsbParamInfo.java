package com.ceair.wsdl.domain;

public class EsbParamInfo {

    private int paramId;
    private int parentParamId;
    private int paramGroupId;
    private String nodeName;
    private String nodeDesc;
    private String dataType;
    private String valueRange;
    private String minoccurs;
    private String maxoccurs;
    private String sampleValue;

    public int getParamId() {
        return paramId;
    }

    public void setParamId(int paramId) {
        this.paramId = paramId;
    }

    public int getParentParamId() {
        return parentParamId;
    }

    public void setParentParamId(int parentParamId) {
        this.parentParamId = parentParamId;
    }

    public int getParamGroupId() {
        return paramGroupId;
    }

    public void setParamGroupId(int paramGroupId) {
        this.paramGroupId = paramGroupId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValueRange() {
        return valueRange;
    }

    public void setValueRange(String valueRange) {
        this.valueRange = valueRange;
    }

    public String getMinoccurs() {
        return minoccurs;
    }

    public void setMinoccurs(String minoccurs) {
        this.minoccurs = minoccurs;
    }

    public String getMaxoccurs() {
        return maxoccurs;
    }

    public void setMaxoccurs(String maxoccurs) {
        this.maxoccurs = maxoccurs;
    }

    public String getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(String sampleValue) {
        this.sampleValue = sampleValue;
    }

   

}