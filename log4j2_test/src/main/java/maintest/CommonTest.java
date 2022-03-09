package maintest;

import env.EnvHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class CommonTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonTest.class);

    public static void main(String[] args) {

        try {
            EnvHolder envHolder = new EnvHolder("env.properties");
            Properties env = envHolder.getEnv();
            System.out.println("env = " + env);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
