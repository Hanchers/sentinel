package com.hancher.sentinel.core.bean;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * jackson bean 配置
 * @date 2025-06-25 08:54:13
 * @author hancher
 * @since 1.0
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .findAndAddModules() // 自动发现并注册模块
                .build();
    }
}
