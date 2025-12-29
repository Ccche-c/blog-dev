package com.shiyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API配置属性
 *
 * @author shiyi
 * @date 2024/01/01
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfigProperties {

    /**
     * DeepSeek API密钥
     */
    private String apiKey;

    /**
     * DeepSeek API基础URL
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * 聊天接口路径
     */
    private String chatPath = "/v1/chat/completions";

    /**
     * 模型名称
     */
    private String model = "deepseek-chat";

    /**
     * 系统提示词（用于推送精品内容）
     */
    private String systemPrompt = "你是一个专业的博客内容推荐助手，擅长根据用户需求推荐精品文章内容。请用简洁、友好的语言与用户交流，并推荐相关的优质内容。";

    /**
     * 温度参数（0-2，控制回复的随机性）
     */
    private Double temperature = 0.7;

    /**
     * 最大token数
     */
    private Integer maxTokens = 2048;

    /**
     * 连接超时时间（秒）
     */
    private Integer connectTimeout = 10;

    /**
     * 读取超时时间（秒）
     */
    private Integer readTimeout = 60;

    /**
     * 获取完整的聊天接口URL
     */
    public String getChatUrl() {
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String normalizedPath = chatPath.startsWith("/") ? chatPath : "/" + chatPath;
        return normalizedBase + normalizedPath;
    }
}

