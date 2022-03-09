package flink.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 用来初始化配置文件
 */
public class EnvHolder {

    private static final Logger logger = LoggerFactory.getLogger(EnvHolder.class);

    private final Properties envProperties;

    public EnvHolder(String resourceFileName) {
        this.envProperties = new Properties();
        try {
            this.envProperties.load(this.getClass().getClassLoader().getResourceAsStream(resourceFileName));
            logger.info("初始化配置文件({})：{}", resourceFileName, this.envProperties);
        } catch (IOException e) {
            // do nothing
        }
    }

    public Properties getEnvProperties() {
        return envProperties;
    }
}
