package com.shiyi.service.impl;

import com.shiyi.common.ResponseResult;
import com.shiyi.config.properties.DeepSeekConfigProperties;
import com.shiyi.dto.*;
import com.shiyi.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * AI对话服务实现类
 *
 * @author shiyi
 * @date 2024/01/01
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {

    private final DeepSeekConfigProperties properties;
    private final RestTemplate restTemplate;

    @Autowired
    public AiServiceImpl(DeepSeekConfigProperties properties, @Qualifier("aiRestTemplate") RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseResult chat(AiChatRequest request) {
        // 检查API密钥是否配置
        if (!StringUtils.hasText(properties.getApiKey())) {
            return ResponseResult.error("请先配置DeepSeek API密钥");
        }

        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            // 构建请求体
            DeepSeekChatRequest chatRequest = buildChatRequest(request);
            
            // 记录请求日志（不记录敏感信息）
            log.info("调用DeepSeek API，URL: {}, Model: {}", properties.getChatUrl(), properties.getModel());

            // 发送请求
            HttpEntity<DeepSeekChatRequest> httpEntity = new HttpEntity<>(chatRequest, headers);
            DeepSeekChatResponse response = restTemplate.postForObject(
                    properties.getChatUrl(),
                    httpEntity,
                    DeepSeekChatResponse.class
            );

            // 处理响应
            if (response == null || CollectionUtils.isEmpty(response.getChoices())) {
                return ResponseResult.error("AI服务暂未返回内容，请稍后重试");
            }

            // 提取回复内容
            String content = response.getChoices().get(0).getMessage().getContent();
            ResponseResult result = ResponseResult.success("AI对话成功", content);

            // 添加使用统计信息
            if (response.getUsage() != null) {
                result.putExtra("usage", response.getUsage());
            }

            return result;

        } catch (RestClientResponseException e) {
            log.error("DeepSeek API调用失败: {}", e.getResponseBodyAsString(), e);
            try {
                // 尝试解析错误信息
                String errorBody = e.getResponseBodyAsString();
                if (StringUtils.hasText(errorBody)) {
                    return ResponseResult.error("AI服务调用失败: " + errorBody);
                }
            } catch (Exception ex) {
                log.error("解析错误信息失败", ex);
            }
            return ResponseResult.error("AI服务调用失败，请检查API密钥和网络连接");

        } catch (RestClientException e) {
            log.error("DeepSeek API网络请求异常，URL: {}, 异常信息: {}", properties.getChatUrl(), e.getMessage(), e);
            // 输出更详细的错误信息
            String errorMsg = "网络请求异常";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("timeout") || e.getMessage().contains("超时")) {
                    errorMsg = "请求超时，请稍后重试";
                } else if (e.getMessage().contains("Connection refused") || e.getMessage().contains("连接被拒绝")) {
                    errorMsg = "无法连接到AI服务，请检查网络";
                } else if (e.getMessage().contains("SSL") || e.getMessage().contains("证书")) {
                    errorMsg = "SSL证书验证失败";
                } else {
                    errorMsg = "网络请求异常: " + e.getMessage();
                }
            }
            return ResponseResult.error(errorMsg);

        } catch (Exception e) {
            log.error("AI对话处理异常", e);
            return ResponseResult.error("处理异常: " + e.getMessage());
        }
    }

    /**
     * 构建DeepSeek API请求体
     */
    private DeepSeekChatRequest buildChatRequest(AiChatRequest request) {
        List<DeepSeekChatRequest.Message> messages = new ArrayList<>();

        // 添加系统提示词
        String systemPrompt = StringUtils.hasText(request.getSystemPrompt())
                ? request.getSystemPrompt()
                : properties.getSystemPrompt();
        messages.add(new DeepSeekChatRequest.Message("system", systemPrompt));

        // 添加历史对话
        if (!CollectionUtils.isEmpty(request.getHistory())) {
            for (ChatMessageDTO historyMsg : request.getHistory()) {
                if (StringUtils.hasText(historyMsg.getRole()) && StringUtils.hasText(historyMsg.getContent())) {
                    messages.add(new DeepSeekChatRequest.Message(historyMsg.getRole(), historyMsg.getContent()));
                }
            }
        }

        // 添加当前用户消息
        messages.add(new DeepSeekChatRequest.Message("user", request.getMessage()));

        // 构建请求
        DeepSeekChatRequest chatRequest = new DeepSeekChatRequest();
        chatRequest.setModel(properties.getModel());
        chatRequest.setMessages(messages);
        chatRequest.setTemperature(request.getTemperature() != null ? request.getTemperature() : properties.getTemperature());
        chatRequest.setMaxTokens(properties.getMaxTokens());

        return chatRequest;
    }
}

