package com.shiyi.config;

import com.shiyi.config.properties.DeepSeekConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        // 支持中文编码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }

    /**
     * AI服务专用的RestTemplate，使用更长的超时时间
     */
    @Bean("aiRestTemplate")
    @ConditionalOnMissingBean(name = "aiRestTemplate")
    public RestTemplate aiRestTemplate(@Autowired(required = false) DeepSeekConfigProperties deepSeekProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // 如果配置了DeepSeek属性，使用配置的超时时间，否则使用默认值
        if (deepSeekProperties != null) {
            factory.setConnectTimeout(deepSeekProperties.getConnectTimeout() * 1000); // 秒转毫秒
            factory.setReadTimeout(deepSeekProperties.getReadTimeout() * 1000); // 秒转毫秒
        } else {
            // 默认值：连接10秒，读取60秒
            factory.setConnectTimeout(10000);
            factory.setReadTimeout(60000);
        }
        
        RestTemplate restTemplate = new RestTemplate(factory);
        // 支持中文编码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }
}

