package com.hive.run;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

/**
 * 连接需要修改的内容：
 * 1、principal
 * 2、hive - username
 * 3、hive - jdbcUrl
 * 4、hive - dbName
 */
public class HiveRun {

    private static final Logger logger = LoggerFactory.getLogger(HiveRun.class);

    private static final String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) {

        String CONF_PATH;

        try {
            CONF_PATH = args[0];
        } catch (Exception e) {
            throw new RuntimeException("No CONF_PATH input !");
        }

        if (!CONF_PATH.endsWith("/") && !CONF_PATH.endsWith("\\")) {
            if (CONF_PATH.contains("/")) {
                CONF_PATH = CONF_PATH + "/";
            } else if (CONF_PATH.contains("\\")) {
                CONF_PATH = CONF_PATH + "\\";
            }
            logger.info("Fixed the CONF_PATH !");
        }

        logger.info("CONF_PATH = {}", CONF_PATH);

        String winUtilsPath = CONF_PATH;
        System.setProperty("hadoop.home.dir", winUtilsPath);

        String confPath = CONF_PATH + "krb5.conf"; // 配置文件
        logger.info("\"krb5.conf\"'s path is : " + confPath);
        System.setProperty("java.security.krb5.conf", confPath);
        String kerPath = CONF_PATH + "testuser01.keytab"; // 配置文件
        logger.info("\"testuser01.keytab\"'s path is : " + kerPath);
        String principal = "testuser01@CDH5-DATA.COM";
        Configuration config = new Configuration();
        config.set("hadoop.security.authentication", "Kerberos");
        config.set("keytab.file", kerPath);
        config.set("kerberos.principal", principal);
        UserGroupInformation.setConfiguration(config);
        try {
            UserGroupInformation.loginUserFromKeytab(principal, kerPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String username = "testuser01@CDH5-DATA.COM";
        String password = "";
//        String jdbcUrl = "jdbc:hive2://192.168.150.135:10000/default";
        String jdbcUrl = "jdbc:hive2://master01.cdh5-data.com:10000/testdb01;principal=hive/master01.cdh5-data.com@CDH5-DATA.COM";

        Connection conn = null;
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            statement = conn.createStatement();
            String dbName = "testdb01";
            String tableName = "product";
            String createTableSQL = "create table if not exists " + dbName + "." + tableName + " (id int , name varchar(255))";
            statement.execute(createTableSQL);
            logger.info("create table sql is : {}", createTableSQL);
            String insertSQL = "insert into " + dbName + "." + tableName + " values(1,'iphone13 pro max')";
            statement.execute(insertSQL);
            logger.info("insert table sql is : {}", insertSQL);
            String selectSQL = "select * from " + dbName + "." + tableName;
            ResultSet resultSet = statement.executeQuery(selectSQL);
            logger.info("select sql is : " + selectSQL + ";");
            while (resultSet.next()) {
                logger.info("result >>>> id: {}, name: {}", resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                    logger.info("sql statement is closed...");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                    logger.info("connection is closed...");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
