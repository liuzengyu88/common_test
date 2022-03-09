package common.flink.run;

import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueStateProcess {

    private static final Logger logger = LoggerFactory.getLogger(ValueStateProcess.class);

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> socketDS = env.socketTextStream("hadoop102", 9999);

        KeyedStream<String, String> keyedDS = socketDS.keyBy(data -> data.split(" ")[0]);

        ValueStateDescriptor<String> stateDescriptor = new ValueStateDescriptor<>("valueState", String.class);
        StateTtlConfig stateTtlConfig = StateTtlConfig
                .newBuilder(Time.seconds(10))
                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();
        stateDescriptor.enableTimeToLive(stateTtlConfig);

        SingleOutputStreamOperator<String> processedDS = keyedDS.process(new KeyedProcessFunction<String, String, String>() {

            private ValueState<String> valueState;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.valueState = getRuntimeContext().getState(stateDescriptor);
            }

            @Override
            public void processElement(String value, KeyedProcessFunction<String, String, String>.Context context, Collector<String> collector) throws Exception {

                String[] strings = value.split(" ");

                logger.info("计算前，本条数据的key为：{}，当前的valueState为：{}", strings[0], valueState.value());
                valueState.update(strings[1]);
                logger.info("计算后，本条数据的key为：{}，当前的valueState为：{}", strings[0], valueState.value());
                collector.collect(value);
            }
        });

        processedDS.print(">>>>");

        env.execute();
    }
}
