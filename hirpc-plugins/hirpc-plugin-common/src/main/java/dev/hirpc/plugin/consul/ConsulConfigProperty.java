package dev.hirpc.plugin.consul;

import lombok.Getter;
import lombok.Setter;

/**
 * @author JT
 * @date 2022/7/16
 * @desc
 */
@Setter
@Getter
public class ConsulConfigProperty {

    private String host;

    private String port;

    private String aclToken;
}
