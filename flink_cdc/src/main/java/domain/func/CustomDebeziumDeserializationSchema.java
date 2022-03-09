package domain.func;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

public class CustomDebeziumDeserializationSchema implements DebeziumDeserializationSchema<String> {
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {

        String topic = sourceRecord.topic();
        String[] fields = topic.split("\\.");
        String dataBase = fields[1];
        String tableName = fields[2];

        JSONObject row = new JSONObject();
        row.put("dataBase",dataBase);
        row.put("tableName",tableName);

        Struct value = (Struct) sourceRecord.value();
        Struct after = value.getStruct("after");
        JSONObject afterJSONObj = new JSONObject();
        if (after != null) {
            List<Field> fieldList = after.schema().fields();
            for (Field field : fieldList) {
                afterJSONObj.put(field.name(),after.get(field));
            }
        }
        row.put("data",afterJSONObj);

        Struct before = value.getStruct("before");
        JSONObject beforeJSONObj = new JSONObject();
        if (before != null) {
            for (Field field : before.schema().fields()) {
                beforeJSONObj.put(field.name(),before.get(field));
            }
        }
        row.put("beforeData",beforeJSONObj);

        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        row.put("type",operation.name().toLowerCase());

        collector.collect(row.toJSONString());

    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
