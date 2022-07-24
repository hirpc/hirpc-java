package dev.hirpc.kafka.plugin.config;

import dev.hirpc.common.exceptions.ExceptionCode;
import dev.hirpc.common.plugin.ConfigLoad;
import dev.hirpc.kafka.domain.KafkaSource;
import dev.hirpc.kafka.exception.KafkaConfigException;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: Oliver
 * @date: 2022/6/14
 * @title:
 */
public class KafkaDefaultConfigLoad implements ConfigLoad<KafkaSource> {

    private String profileActive;

    public KafkaDefaultConfigLoad(String profileActive) {
        this.profileActive = profileActive;
    }

    @Override
    public Map<String, KafkaSource> load() {
        String dbYamlPath = Objects.isNull(this.profileActive) ? "kafka.yaml" : String.format("kafka-%s.yaml", this.profileActive);
        Yaml yaml = new Yaml();
        Map<String, Object> redisDefaultConfigMap = yaml.load(
                this.getClass().getClassLoader().getResourceAsStream(dbYamlPath)
        );
        if (Objects.isNull(redisDefaultConfigMap)) {
            this.throwKafkaConfigException(dbYamlPath);
        }
        Map<String, Object> hirpcObj = Optional.ofNullable(redisDefaultConfigMap.get("hirpc"))
                .map(obj -> (Map<String, Object>) obj).orElse(null);
        if (Objects.isNull(hirpcObj)) {
            this.throwKafkaConfigException(dbYamlPath);
        }
        List<Map<String, Object>> sourceMapList = Optional.of(hirpcObj.get("kafka"))
                .map(obj -> (List<Map<String, Object>>) obj)
                .orElse(null);
        if (Objects.isNull(sourceMapList)) {
            this.throwKafkaConfigException(dbYamlPath);
        }
        return sourceMapList.stream()
                .map((Map<String, Object> sourceMap) -> {
                    KafkaSource kafkaSource = new KafkaSource();
                    kafkaSource.setName(String.valueOf(sourceMap.get("name")));
                    List<String> serverList = (List<String>)(sourceMap.get("bootstrap-servers"));
                    kafkaSource.setBootstrapServers(serverList);
                    return kafkaSource;
                }).collect(Collectors.toMap(KafkaSource::getName, source -> source));
    }

    public void throwKafkaConfigException(String dbYamlPath) {
        throw new KafkaConfigException(ExceptionCode.ERROR, "未正确配置配置文件[{}]", dbYamlPath);
    }

}
