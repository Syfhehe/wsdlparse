package com.ceair.wsdl.domain;

import java.io.File;

public class ServiceVersion {

    public ServiceVersion(String wsdlClob) {
        super();
        this.wsdlClob = wsdlClob;
    }

    public ServiceVersion() {
        super();
    }

    public ServiceVersion(int serviceVerId, int serviceVersion, String wsdlLocation, String wsdlClob) {
        super();
        this.serviceVerId = serviceVerId;
        this.serviceVersion = serviceVersion;
        this.wsdlLocation = wsdlLocation;
        this.wsdlClob = wsdlClob;
    }

    private int serviceVerId;
    private int serviceId;
    private int serviceVersion;
    private String wsdlLocation;
    private String dubboLocation;
    private String wsdlClob;

    public int getServiceVerId() {
        return serviceVerId;
    }

    public void setServiceVerId(int serviceVerId) {
        this.serviceVerId = serviceVerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(int serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public String getDubboLocation() {
        return dubboLocation;
    }

    public void setDubboLocation(String dubboLocation) {
        this.dubboLocation = dubboLocation;
    }

    public String getWsdlClob() {
        return wsdlClob;
    }

    public void setWsdlClob(String wsdlClob) {
        this.wsdlClob = wsdlClob;
    }
}
