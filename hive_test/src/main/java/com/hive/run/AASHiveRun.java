package com.hive.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class AASHiveRun {

    private static final Logger logger = LoggerFactory.getLogger(AASHiveRun.class);

    private static final String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) {

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Properties env = new Properties();
        try {
            env.load(new FileInputStream("conf/common.properties"));
            logger.info("env : {}", env);
        } catch (IOException e) {
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
