package com.ceair.wsdl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ceair.wsdl.domain.ServiceOperation;
import com.ceair.wsdl.domain.ServiceVersion;

public class OracleDBUtil {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        Properties properties = new Properties();
        try {
            Reader in = new FileReader("src\\oracle_config.properties");
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    public static Connection open() {
        try {
            Class.forName(driver);
            return (Connection) DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static int insertServiceOperation(ServiceOperation serviceOperation) {
        Connection conn = open();
        int i = 0;
        String sql = "insert into ESB_SVC_OPT (OPT_ID,SERVICE_VER_ID,OPT_EN_NAME,OPT_SRC_EN_NAME,OPT_INPUT_MSG_NAME,OPT_INPUT_MSG_NS,OPT_SRC_INPUT_MSG_NAME,OPT_SRC_INPUT_MSG_NS,ENDPOINT) values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceOperation.getOptId());
            pstmt.setInt(2, serviceOperation.getServiceVerId());
            pstmt.setString(3, serviceOperation.getOptEnName());
            pstmt.setString(4, serviceOperation.getOptSrcEnName());
            pstmt.setString(5, serviceOperation.getOptInputMsgName());
            pstmt.setString(6, serviceOperation.getOptInputMsgNs());
            pstmt.setString(7, serviceOperation.getOptSrcInputMsgName());
            pstmt.setString(8, serviceOperation.getOptSrcInputMsgNs());
            pstmt.setString(9, serviceOperation.getEndpoint());
            i = pstmt.executeUpdate();
            System.out.println("result: " + i);

            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public static int insertServiceVersion(ServiceVersion serviceVersion) {
        Connection conn = open();
        int i = 0;
        String sql = "insert into ESB_SVC_VERSION (SERVICE_VER_ID,SERVICE_VERSION,WSDL_LOCATION,WSDL_CLOB) values(?,?,?,?)";
        PreparedStatement pstmt;
        try {
            String clobContent = serviceVersion.getWsdlClob();
            StringReader reader = new StringReader(clobContent);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceVersion.getServiceVerId());
            pstmt.setFloat(2, serviceVersion.getServiceVersion());
            pstmt.setString(3, serviceVersion.getWsdlLocation());
            pstmt.setCharacterStream(4, reader, clobContent.length());

            i = pstmt.executeUpdate();
            System.out.println("result: " + i);
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    // æ ¹æ?®OPT_IDé€‰å?–ServiceOperation
    public static ServiceOperation selectServiceOperation(int serviceOperationID) {
        List<ServiceOperation> optList = new ArrayList();
        Connection conn = open();
        String sql = "select * from ESB_SVC_OPT where OPT_ID = ?";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceOperationID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ServiceOperation optTemp = new ServiceOperation();
                System.out.println("name: " + rs.getString("OPT_EN_NAME"));
                System.out.println("SERVICE_VER_ID: " + rs.getString("SERVICE_VER_ID"));
                optTemp.setOptId(rs.getInt("OPT_ID"));
                optTemp.setServiceVerId(rs.getInt("SERVICE_VER_ID"));
                optTemp.setOptEnName(rs.getString("OPT_EN_NAME"));
                optTemp.setOptSrcEnName(rs.getString("OPT_SRC_EN_NAME"));

                optTemp.setOptInputMsgName(rs.getString("OPT_INPUT_MSG_NAME"));
                optTemp.setOptInputMsgNs(rs.getString("OPT_INPUT_MSG_NS"));
                optTemp.setOptOutputMsgName(rs.getString("OPT_OUTPUT_MSG_NAME"));
                optTemp.setOptOutputMsgNs(rs.getString("OPT_OUTPUT_MSG_NS"));
                optTemp.setOptFaultMsgName(rs.getString("OPT_FAULT_MSG_NAME"));
                optTemp.setOptFaultMsgNs(rs.getString("OPT_FAULT_MSG_NS"));
                
                optTemp.setOptSoapAction(rs.getString("OPT_SOAP_ACTION"));
                
                optTemp.setOptSrcInputMsgName(rs.getString("OPT_SRC_INPUT_MSG_NAME"));
                optTemp.setOptSrcInputMsgNs(rs.getString("OPT_SRC_INPUT_MSG_NS"));
                optTemp.setOptSrcOutputMsgName(rs.getString("OPT_SRC_OUTPUT_MSG_NAME"));
                optTemp.setOptSrcOutputMsgNs(rs.getString("OPT_SRC_OUTPUT_MSG_NS"));
                optTemp.setOptSrcFaultMsgName(rs.getString("OPT_SRC_FAULT_MSG_NAME"));
                optTemp.setOptSrcFaultMsgNs(rs.getString("OPT_SRC_FAULT_MSG_NS"));
                
                optTemp.setOptSoapAction(rs.getString("OPT_SOAP_ACTION"));
                optList.add(optTemp);
            }
            rs.close();
            pstmt.close();
            conn.close();
            return optList.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // æ ¹æ?®SERVICE_VER_IDé€‰å?–
    public static ServiceVersion selectServiceVersion(int serviceVersionID) {
        List<ServiceVersion> svrVerList = new ArrayList();
        BufferedReader reader = null;
        Connection conn = open();
        String sql = "select * from ESB_SVC_VERSION where SERVICE_VER_ID = ?";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceVersionID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ServiceVersion svrVer = new ServiceVersion();
                System.out.println("SERVICE_VERSION: " + rs.getString("SERVICE_VERSION"));
                Clob clob = rs.getClob("WSDL_CLOB");
                String value = clob.getSubString(1, (int) clob.length());  
                System.out.println("CLOB valueï¼š" + value);  
                svrVer.setServiceVerId(rs.getInt("SERVICE_VER_ID"));
                svrVer.setServiceVersion(rs.getInt("SERVICE_VERSION"));
                svrVer.setWsdlLocation(rs.getString("WSDL_LOCATION"));
                svrVer.setWsdlClob(value);
                svrVerList.add(svrVer);
            }
            rs.close();
            pstmt.close();
            conn.close();
            return svrVerList.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
