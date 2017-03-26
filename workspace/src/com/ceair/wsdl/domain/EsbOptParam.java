package com.ceair.wsdl.domain;

public class EsbOptParam {
    
    private int optParamId;
    private int paramGroupId;
    private String paramName;
    private String paramDesc;
    private String paramType;
    private EsbParamInfo paramsInfoParam;//外键到ESB_PARAMS_INFO表中的PARAM_ID
    
    public int getOptParamId() {
        return optParamId;
    }
    public void setOptParamId(int optParamId) {
        this.optParamId = optParamId;
    }
    public int getParamGroupId() {
        return paramGroupId;
    }
    public void setParamGroupId(int paramGroupId) {
        this.paramGroupId = paramGroupId;
    }
    public String getParamName() {
        return paramName;
    }
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
    public String getParamDesc() {
        return paramDesc;
    }
    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }
    public String getParamType() {
        return paramType;
    }
    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
    public EsbParamInfo getParamsInfoParam() {
        return paramsInfoParam;
    }
    public void setParamsInfoParam(EsbParamInfo paramsInfoParam) {
        this.paramsInfoParam = paramsInfoParam;
    }

}
