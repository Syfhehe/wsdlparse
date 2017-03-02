package com.ceair.wsdl.jdbc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        // String sql = "insert into ESB_SVC_VERSION
        // (SERVICE_VER_ID,SERVICE_ID,SERVICE_VERSION,WSDL_LOCATION,DUBBO_LOCATION)
        // values(?,?,?,?,?)";
        String sql = "insert into ESB_SVC_VERSION (SERVICE_VER_ID,SERVICE_VERSION) values(?,?)";

        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceVersion.getServiceVerId());
            pstmt.setFloat(2, serviceVersion.getServiceVersion());
            // pstmt.setInt(1, serviceVersion.getServiceVerId());
            // pstmt.setInt(2, serviceVersion.getServiceId());
            // pstmt.setFloat(3, serviceVersion.getServiceVersion());
            // pstmt.setString(4, serviceVersion.getWsdlLocation());
            // pstmt.setString(5, serviceVersion.getDubboLocation());
            i = pstmt.executeUpdate();
            System.out.println("result: " + i);
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public static ResultSet selectServiceOperation(int serviceOperationID) {
        Connection conn = open();
        String sql = "select * from ESB_SVC_OPT where OPT_ID = ?";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceOperationID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("name: " + rs.getString("OPT_EN_NAME"));
                System.out.println("SERVICE_VER_ID: " + rs.getString("SERVICE_VER_ID"));
            }
            rs.close();
            pstmt.close();
            conn.close();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //选取
    public static ResultSet selectServiceVersion(int serviceVersionID) {
        BufferedReader reader = null;
        Connection conn = open();
        String sql = "select * from ESB_SVC_VERSION where SERVICE_VER_ID = ?";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceVersionID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("SERVICE_VERSION: " + rs.getString("SERVICE_VERSION"));
                Clob clob = rs.getClob("WSDL_CLOB");
                reader = new BufferedReader(new InputStreamReader(clob.getAsciiStream())); 
                String line = null;  
                while ((line = reader.readLine()) != null) {  
                    System.out.println(line);  
                } 
            }
            rs.close();
            pstmt.close();
            conn.close();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
