package com.hive.run;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * 连接需要修改的内容：
 * 1、kerberos - principal
 * 2、kerberos - krbConf
 * 3、kerberos - keytab
 * 4、hive - username
 * 5、hive - jdbcUrl
 * 6、hive - dbName
 */
public class HiveRun {

    private static final Logger logger = LoggerFactory.getLogger(HiveRun.class);

    private static final String driverName = "org.apache.hive.jdbc.HiveDriver";

    private static final String CONF_PATH = "conf/";

    public static void main(String[] args) {

        Properties env = new Properties();
        try {
            env.load(new FileInputStream("conf/common.properties"));
            logger.info("env : {}", env);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String krbConf = CONF_PATH + env.getProperty("kerConfFileName"); // 配置文件
        System.setProperty("java.security.krb5.conf", krbConf);
        String kerPath = CONF_PATH + env.getProperty("keyTabFileName"); // 配置文件
        String principal = env.getProperty("principal");
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

        String username = env.getProperty("username");
        String password = env.getProperty("password");
        String jdbcUrl = env.getProperty("jdbcUrl");

        Connection conn = null;
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            statement = conn.createStatement();
            String dbName = env.getProperty("dbName");
            String tableName = env.getProperty("tableName");
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
