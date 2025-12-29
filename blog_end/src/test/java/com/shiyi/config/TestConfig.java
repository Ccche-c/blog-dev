package com.shiyi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

/**
 * 测试配置类
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {
    
    // 可以在这里配置测试专用的Bean
    // 例如：Mock的Service、Repository等
}

