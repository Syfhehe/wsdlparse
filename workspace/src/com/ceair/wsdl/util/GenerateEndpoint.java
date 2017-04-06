package com.ceair.wsdl.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import com.ceair.wsdl.domain.EsbSvc;
import com.ceair.wsdl.domain.EsbSvcVersion;


public class GenerateEndpoint {

    private static final String PROPERTIES_FILE_NAME = "endpoint.properties";

    private static String SOAPServer;
    private static String Port;
    private static String Protocol;
    private static String FormateType;

    private static void loadProperties(String folderPath) {
        Properties properties = new Properties();
        try {
            String filepath = folderPath + File.separator + PROPERTIES_FILE_NAME;
            Reader in = new FileReader(filepath);
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Protocol = properties.getProperty("Protocol");
        SOAPServer = properties.getProperty("SOAPServer");
        Port = properties.getProperty("Port");
        FormateType = properties.getProperty("FormateType");
    }

    private static String getResourcePath() {
        String className = GenerateEndpoint.class.getName();
        String classNamePath = className.replace(".", "/") + ".class";
        URL is = GenerateEndpoint.class.getClassLoader().getResource(classNamePath);
        String path = is.getFile();
        File file = new File(path);
        String folderPath = file.getParent();
        return folderPath;
    }

    public static void main(String[] args) {
        String folderPath = getResourcePath();
        loadProperties(folderPath);
        EsbSvc service = new EsbSvc("domain", "type", "name");
        EsbSvcVersion svcVersion = new EsbSvcVersion("version");
        String endpoint = genEndpoint(service, svcVersion);
        System.out.println(endpoint);
    }

    // Endpointå½¢å¼?ï¼š http://<SOAServer>ï¼š<Port>/SOAP/<æœ?åŠ¡åŸŸ>_<æœ?åŠ¡ç±»åˆ«>_<æœ?åŠ¡å??ç§°>_<ç‰ˆæœ¬å?·>
    public static String genEndpoint(EsbSvc service, EsbSvcVersion svcVersion) {
        String endpoint = null;
        endpoint = Protocol + SOAPServer + ":" + Port + "/" + FormateType + "/" + service.getServiceDomain() + "_"
                + service.getServiceType() + "_" + service.getServiceEnName() + "_" + svcVersion.getServiceVersion();
        return endpoint;
    }
}
