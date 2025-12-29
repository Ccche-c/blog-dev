package com.shiyi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DeepSeek API请求体
 *
 * @author shiyi
 * @date 2024/01/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekChatRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<Message> messages;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 最大token数
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 消息对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}

