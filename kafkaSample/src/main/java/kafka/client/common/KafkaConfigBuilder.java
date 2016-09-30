package kafka.client.common;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Properties;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public class KafkaConfigBuilder<K, V> {
    private String servers;
    private String asks;
    private Integer batchSize;
    private Integer partitionSize;
    private String groupId;
    private String clientId;

    private org.apache.kafka.common.serialization.Serializer keySerializer;
    private org.apache.kafka.common.serialization.Serializer valueSerializer;
    private Deserializer keyDeserializer;
    private Deserializer valueDeserializer;
    private Properties properties;
    private String topic;
    private KafkaService.Mode mode;

    public KafkaConfigBuilder setServers(String servers) {
        this.servers = servers;
        return this;
    }

    public KafkaConfigBuilder setAsks(String asks) {
        this.asks = asks;
        return this;
    }

    public KafkaConfigBuilder setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public KafkaConfigBuilder setPartitionSize(Integer partitionSize) {
        this.partitionSize = partitionSize;
        return this;
    }

    public KafkaConfigBuilder setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public KafkaConfigBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public KafkaConfigBuilder setKeySerializer(Serializer keySerializer) {
        this.keySerializer = keySerializer;
        return this;
    }

    public KafkaConfigBuilder setValueSerializer(Serializer valueSerializer) {
        this.valueSerializer = valueSerializer;
        return this;
    }

    public KafkaConfigBuilder setKeyDeserializer(Deserializer keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
        return this;
    }

    public KafkaConfigBuilder setValueDeserializer(Deserializer valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
        return this;
    }

    public KafkaConfigBuilder setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public KafkaConfigBuilder setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public KafkaConfigBuilder setMode(KafkaService.Mode mode) {
        this.mode = mode;
        return this;
    }
//    public KafkaClient<K, V> build(){
//        return new KafkaClientImpl<K,V>(this);
//    }

    public String getServers() {
        return servers;
    }

    public String getAsks() {
        return asks;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public Integer getPartitionSize() {
        return partitionSize;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getClientId() {
        return clientId;
    }

    public Serializer getKeySerializer() {
        return keySerializer;
    }

    public Serializer getValueSerializer() {
        return valueSerializer;
    }

    public Deserializer getKeyDeserializer() {
        return keyDeserializer;
    }

    public Deserializer getValueDeserializer() {
        return valueDeserializer;
    }

    public Properties getProperties() {
        return properties;
    }


    public String getTopic() {
        return topic;
    }

    public KafkaService.Mode getMode() {
        return mode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KafkaConfigBuilder{");
        sb.append("servers='").append(servers).append('\'');
        sb.append(", asks='").append(asks).append('\'');
        sb.append(", batchSize=").append(batchSize);
        sb.append(", partitionSize=").append(partitionSize);
        sb.append(", groupId='").append(groupId).append('\'');
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append(", keySerializer='").append(keySerializer).append('\'');
        sb.append(", valueSerializer='").append(valueSerializer).append('\'');
        sb.append(", keyDeserializer='").append(keyDeserializer).append('\'');
        sb.append(", valueDeserializer='").append(valueDeserializer).append('\'');
        sb.append(", properties=").append(properties);
        sb.append('}');
        return sb.toString();
    }
}
