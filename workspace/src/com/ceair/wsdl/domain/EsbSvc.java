package com.ceair.wsdl.domain;

public class EsbSvc {
    public EsbSvc(String serviceDomain, String serviceType, String serviceEnName) {
        super();
        this.serviceDomain = serviceDomain;
        this.serviceType = serviceType;
        this.serviceEnName = serviceEnName;
    }

    private String serviceDomain;
    
    private String serviceType;

    private String serviceEnName;

    public String getServiceDomain() {
        return serviceDomain;
    }

    public void setServiceDomain(String serviceDomain) {
        this.serviceDomain = serviceDomain;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceEnName() {
        return serviceEnName;
    }

    public void setServiceEnName(String serviceEnName) {
        this.serviceEnName = serviceEnName;
    }

}
