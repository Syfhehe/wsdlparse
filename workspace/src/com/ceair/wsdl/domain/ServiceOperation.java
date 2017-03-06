package com.ceair.wsdl.domain;

public class ServiceOperation {

    public ServiceOperation(String optEnName, String optSrcEnName, String optInputMsgName, String optInputMsgNs,
            String optOutputMsgName, String optOutputMsgNs, String optFaultMsgName, String optFaultMsgNs,
            String optSoapAction, String optSrcInputMsgName, String optSrcInputMsgNs, String optSrcOutputMsgName,
            String optSrcOutputMsgNs, String optSrcFaultMsgName, String optSrcFaultMsgNs, String optSrcSoapAction) {
        super();
        this.optEnName = optEnName;
        this.optSrcEnName = optSrcEnName;
        this.optInputMsgName = optInputMsgName;
        this.optInputMsgNs = optInputMsgNs;
        this.optOutputMsgName = optOutputMsgName;
        this.optOutputMsgNs = optOutputMsgNs;
        this.optFaultMsgName = optFaultMsgName;
        this.optFaultMsgNs = optFaultMsgNs;
        this.optSoapAction = optSoapAction;
        this.optSrcInputMsgName = optSrcInputMsgName;
        this.optSrcInputMsgNs = optSrcInputMsgNs;
        this.optSrcOutputMsgName = optSrcOutputMsgName;
        this.optSrcOutputMsgNs = optSrcOutputMsgNs;
        this.optSrcFaultMsgName = optSrcFaultMsgName;
        this.optSrcFaultMsgNs = optSrcFaultMsgNs;
        this.optSrcSoapAction = optSrcSoapAction;
    }

    public ServiceOperation(int optId, int serviceVerId, String optEnName, String optSrcEnName, String optInputMsgName,
            String optInputMsgNs, String optSrcInputMsgName, String optSrcInputMsgNs, String endpoint) {
        super();
        this.optId = optId;
        this.serviceVerId = serviceVerId;
        this.optEnName = optEnName;
        this.optSrcEnName = optSrcEnName;
        this.optInputMsgName = optInputMsgName;
        this.optInputMsgNs = optInputMsgNs;
        this.optSrcInputMsgName = optSrcInputMsgName;
        this.optSrcInputMsgNs = optSrcInputMsgNs;
        this.endpoint = endpoint;
    }

    public ServiceOperation() {
        super();
    }

    private int optId;// not null
    private int originalOptId;
    private int serviceVerId;// not null
    private String optCnName;
    private String optEnName;// not null
    private String optSrcEnName;// not null
    private String optDesc;
    private String protocolType;
    private String formateType;
    private String providerSysId;
    private String optInputMsgName;// not null
    private String optInputMsgNs;// not null
    private String optOutputMsgName;
    private String optOutputMsgNs;
    private String optFaultMsgName;
    private String optFaultMsgNs;
    private String optSoapAction;
    private String optSrcInputMsgName;// not null
    private String optSrcInputMsgNs;// not null
    private String optSrcOutputMsgName;
    private String optSrcOutputMsgNs;
    private String optSrcFaultMsgName;
    private String optSrcFaultMsgNs;
    private String optSrcSoapAction;
    private String endpoint;// not null

    public int getOptId() {
        return optId;
    }

    public void setOptId(int optId) {
        this.optId = optId;
    }

    public int getOriginalOptId() {
        return originalOptId;
    }

    public void setOriginalOptId(int originalOptId) {
        this.originalOptId = originalOptId;
    }

    public int getServiceVerId() {
        return serviceVerId;
    }

    public void setServiceVerId(int serviceVerId) {
        this.serviceVerId = serviceVerId;
    }

    public String getOptCnName() {
        return optCnName;
    }

    public void setOptCnName(String optCnName) {
        this.optCnName = optCnName;
    }

    public String getOptEnName() {
        return optEnName;
    }

    public void setOptEnName(String optEnName) {
        this.optEnName = optEnName;
    }

    public String getOptSrcEnName() {
        return optSrcEnName;
    }

    public void setOptSrcEnName(String optSrcEnName) {
        this.optSrcEnName = optSrcEnName;
    }

    public String getOptDesc() {
        return optDesc;
    }

    public void setOptDesc(String optDesc) {
        this.optDesc = optDesc;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public String getFormateType() {
        return formateType;
    }

    public void setFormateType(String formateType) {
        this.formateType = formateType;
    }

    public String getProviderSysId() {
        return providerSysId;
    }

    public void setProviderSysId(String providerSysId) {
        this.providerSysId = providerSysId;
    }

    public String getOptInputMsgName() {
        return optInputMsgName;
    }

    public void setOptInputMsgName(String optInputMsgName) {
        this.optInputMsgName = optInputMsgName;
    }

    public String getOptInputMsgNs() {
        return optInputMsgNs;
    }

    public void setOptInputMsgNs(String optInputMsgNs) {
        this.optInputMsgNs = optInputMsgNs;
    }

    public String getOptOutputMsgName() {
        return optOutputMsgName;
    }

    public void setOptOutputMsgName(String optOutputMsgName) {
        this.optOutputMsgName = optOutputMsgName;
    }

    public String getOptOutputMsgNs() {
        return optOutputMsgNs;
    }

    public void setOptOutputMsgNs(String optOutputMsgNs) {
        this.optOutputMsgNs = optOutputMsgNs;
    }

    public String getOptFaultMsgName() {
        return optFaultMsgName;
    }

    public void setOptFaultMsgName(String optFaultMsgName) {
        this.optFaultMsgName = optFaultMsgName;
    }

    public String getOptFaultMsgNs() {
        return optFaultMsgNs;
    }

    public void setOptFaultMsgNs(String optFaultMsgNs) {
        this.optFaultMsgNs = optFaultMsgNs;
    }

    public String getOptSoapAction() {
        return optSoapAction;
    }

    public void setOptSoapAction(String optSoapAction) {
        this.optSoapAction = optSoapAction;
    }

    public String getOptSrcInputMsgName() {
        return optSrcInputMsgName;
    }

    public void setOptSrcInputMsgName(String optSrcInputMsgName) {
        this.optSrcInputMsgName = optSrcInputMsgName;
    }

    public String getOptSrcInputMsgNs() {
        return optSrcInputMsgNs;
    }

    public void setOptSrcInputMsgNs(String optSrcInputMsgNs) {
        this.optSrcInputMsgNs = optSrcInputMsgNs;
    }

    public String getOptSrcOutputMsgName() {
        return optSrcOutputMsgName;
    }

    public void setOptSrcOutputMsgName(String optSrcOutputMsgName) {
        this.optSrcOutputMsgName = optSrcOutputMsgName;
    }

    public String getOptSrcOutputMsgNs() {
        return optSrcOutputMsgNs;
    }

    public void setOptSrcOutputMsgNs(String optSrcOutputMsgNs) {
        this.optSrcOutputMsgNs = optSrcOutputMsgNs;
    }

    public String getOptSrcFaultMsgName() {
        return optSrcFaultMsgName;
    }

    public void setOptSrcFaultMsgName(String optSrcFaultMsgName) {
        this.optSrcFaultMsgName = optSrcFaultMsgName;
    }

    public String getOptSrcFaultMsgNs() {
        return optSrcFaultMsgNs;
    }

    public void setOptSrcFaultMsgNs(String optSrcFaultMsgNs) {
        this.optSrcFaultMsgNs = optSrcFaultMsgNs;
    }

    public String getOptSrcSoapAction() {
        return optSrcSoapAction;
    }

    public void setOptSrcSoapAction(String optSrcSoapAction) {
        this.optSrcSoapAction = optSrcSoapAction;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

}
