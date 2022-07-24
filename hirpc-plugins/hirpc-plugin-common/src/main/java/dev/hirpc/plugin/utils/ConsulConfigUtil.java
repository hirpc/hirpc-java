package dev.hirpc.plugin.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dev.hirpc.plugin.consul.ConsulConfigProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author JT
 * @date 2022/7/16
 * @desc
 */
@Slf4j
public class ConsulConfigUtil {

    private ConsulConfigUtil() {}

    private static final int SUCCESS = 200;

    public static String getConfigWithAcl(ConsulConfigProperty consulConfigProperty, String configPath) {
        String url = StrUtil.format(
                "{}:{}/v1/kv/{}",
                consulConfigProperty.getHost(),
                consulConfigProperty.getPort(),
                configPath
        );

        HttpResponse response = null;
        try {
            response = HttpUtil.createGet(url)
                    .header("X-Consul-Token", consulConfigProperty.getAclToken())
                    .timeout(3000)
                    .execute();
        } catch (Exception e) {
            log.error("[Consul配置] - 获取配置出现异常, 配置地址({}), 请求超时或出现异常: {}", url, e.getMessage());
            return null;
        }
        if (SUCCESS != response.getStatus()) {
            log.debug("[Consul配置] - 获取配置出现异常, 配置地址({}), 返回状态码({})", url, response.getStatus());
            return null;
        }
        String body = response.body();
        if (!JSON.isValid(body)) {
            log.debug("[Consul配置] - 获取配置出现异常, 返回数据非JSON格式: {}", body);
            return null;
        }
        JSONArray bodyJsonArr = JSON.parseArray(body);
        JSONObject bodyJsonObj = bodyJsonArr.getJSONObject(0);
        String valueBase64 = bodyJsonObj.getString("Value");
        return Base64.decodeStr(valueBase64);
    }

}
