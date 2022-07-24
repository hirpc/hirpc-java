package dev.hirpc.plugin.redis;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: JT
 * @date: 2020/8/7
 * @title:
 */
@Setter
@Getter
public class RedisProperty {

    private String name;

    private String mode;

    private String master;

    private List<String> nodes;

    private List<String> sentinels;

    private Integer maxRedirects;

    private Integer database;

    private String password;

    private Integer poolMaxActive = 1000;

    private Integer poolMaxIdle = 10;

    private Integer poolMinIdle = 5;

    private Integer poolMaxWait = -1;

}
