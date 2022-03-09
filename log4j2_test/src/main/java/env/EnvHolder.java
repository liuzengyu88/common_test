package env;

import java.io.IOException;
import java.util.Properties;

public class EnvHolder {

    private Properties envProperties;

    public Properties getEnv() {
        return envProperties;
    }

    public EnvHolder(String resourceFileName) throws IOException {
        this.envProperties = new Properties();
        envProperties.load(this.getClass().getClassLoader().getResourceAsStream(resourceFileName));
    }

}
