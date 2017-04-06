package com.ceair.wsdl.domain;

public class EsbSvcVersion {
    public EsbSvcVersion(String serviceVersion) {
        super();
        this.serviceVersion = serviceVersion;
    }

    private String serviceVersion;

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
}
