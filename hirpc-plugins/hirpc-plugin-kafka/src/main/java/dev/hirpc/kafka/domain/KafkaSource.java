package dev.hirpc.kafka.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: Oliver
 * @date: 2022/6/14
 * @title:
 */
@Setter
@Getter
public class KafkaSource {

    private String name;

    private List<String> bootstrapServers;

}
