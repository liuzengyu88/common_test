package flink.jobs;

import flink.util.DruidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WordCount {

    private static final Logger logger = LoggerFactory.getLogger(WordCount.class);

    public static void main(String[] args) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            DataSource dataSource = DruidUtil.getDataSource("druid.properties");
            connection = dataSource.getConnection();
            String sql = "insert into student values(?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, "zhangsan");
            preparedStatement.execute();
            logger.info("执行的SQL是：{}", sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
