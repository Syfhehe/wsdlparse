package com.ceair.wsdl.jdbc;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ceair.wsdl.domain.ServiceOperation;
import com.mysql.jdbc.Connection;

public class DBUtil {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        Properties properties = new Properties();
        try {
            Reader in = new FileReader("src\\config.properties");
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = properties.getProperty(driver);
        url = properties.getProperty(url);
        username = properties.getProperty(username);
        password = properties.getProperty(password);
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

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTable(String sql) {
        Connection connection = DBUtil.open();
        java.sql.Statement stmt;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insert(String sql) {
        Connection connection = DBUtil.open();
        java.sql.Statement stmt;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void delete(String sql) {
        insert(sql);
    }
    
    public static void update(String sql) {
        insert(sql);
    }
    
    public static List<ServiceOperation> select(String sql) {
        Connection connection = DBUtil.open();
        java.sql.Statement stmt;
        try {
            stmt = connection.createStatement();
            ResultSet rs=stmt.executeQuery(sql);
            List<ServiceOperation> list=new ArrayList<ServiceOperation>();
            while(rs.next()){
                int id =rs.getInt(1);
                System.out.println(id);
                ServiceOperation wsdl=new ServiceOperation();
                wsdl.setId(id);
                list.add(wsdl);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
