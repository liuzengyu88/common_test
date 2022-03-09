package domain;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import domain.func.CustomDebeziumDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkCDCImpl {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> sourceDS = env.addSource(MySQLSource.<String>builder()
                .hostname("hadoop102")
                .port(3306)
                .username("root")
                .password("root")
                .databaseList("flink_test_1")
//                .tableList("flink_test_1.students")
                .startupOptions(StartupOptions.initial())
                .deserializer(new CustomDebeziumDeserializationSchema())
                .build());

        sourceDS.print("source >>>>>>>");

        env.execute();
    }

}
