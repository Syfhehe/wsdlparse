package com.ceair.wsdl.domain;

import java.util.List;

public class ServiceOperation {
    private int id;
    private String inputMessageName;
    private String inputMessageNameSpace;
    private String outputMessageName;
    private String outputMessageNameSpace;
    private List<String> faultMessageName;
    private List<String> faultMessageNameSpace;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInputMessageName() {
        return inputMessageName;
    }

    public void setInputMessageName(String inputMessageName) {
        this.inputMessageName = inputMessageName;
    }

    public String getInputMessageNameSpace() {
        return inputMessageNameSpace;
    }

    public void setInputMessageNameSpace(String inputMessageNameSpace) {
        this.inputMessageNameSpace = inputMessageNameSpace;
    }

    public String getOutputMessageName() {
        return outputMessageName;
    }

    public void setOutputMessageName(String outputMessageName) {
        this.outputMessageName = outputMessageName;
    }

    public String getOutputMessageNameSpace() {
        return outputMessageNameSpace;
    }

    public void setOutputMessageNameSpace(String outputMessageNameSpace) {
        this.outputMessageNameSpace = outputMessageNameSpace;
    }

    public List<String> getFaultMessageName() {
        return faultMessageName;
    }

    public void setFaultMessageName(List<String> faultMessageName) {
        this.faultMessageName = faultMessageName;
    }

    public List<String> getFaultMessageNameSpace() {
        return faultMessageNameSpace;
    }

    public void setFaultMessageNameSpace(List<String> faultMessageNameSpace) {
        this.faultMessageNameSpace = faultMessageNameSpace;
    }
}
