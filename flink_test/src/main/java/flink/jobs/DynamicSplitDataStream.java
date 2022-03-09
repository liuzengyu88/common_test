package flink.jobs;

public class DynamicSplitDataStream {

    public static void main(String[] args) {

        String path = DynamicSplitDataStream.class.getClassLoader().getResource("").getPath();
        System.out.println("path = " + path);
        System.out.println(path + "druid.properties");


    }

}
