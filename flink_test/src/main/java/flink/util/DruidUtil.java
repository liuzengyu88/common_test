package flink.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import flink.env.EnvHolder;

import javax.sql.DataSource;

public class DruidUtil {

    public static DataSource getDataSource(String resourceFileName) throws Exception {
        EnvHolder envHolder = new EnvHolder(resourceFileName);
        return DruidDataSourceFactory.createDataSource(envHolder.getEnvProperties());
    }

}
