package com.shiyi.service.impl;

import com.shiyi.common.ResponseResult;
import com.shiyi.config.properties.DeepSeekConfigProperties;
import com.shiyi.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AiServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AI服务实现类测试")
@SuppressWarnings("unchecked")
class AiServiceImplTest {

    @Mock
    private DeepSeekConfigProperties properties;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AiServiceImpl aiService;

    @BeforeEach
    void setUp() {
        // 设置默认配置（使用 lenient 标记，因为某些测试可能不会使用所有配置）
        lenient().when(properties.getApiKey()).thenReturn("test-api-key");
        lenient().when(properties.getChatUrl()).thenReturn("https://api.deepseek.com/v1/chat/completions");
        lenient().when(properties.getModel()).thenReturn("deepseek-chat");
        lenient().when(properties.getSystemPrompt()).thenReturn("默认系统提示词");
        lenient().when(properties.getTemperature()).thenReturn(0.7);
        lenient().when(properties.getMaxTokens()).thenReturn(2048);
    }

    // ==================== chat 方法测试 ====================

    @Test
    @DisplayName("测试AI对话-API密钥未配置")
    void testChat_ApiKeyNotConfigured() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        // Mock
        when(properties.getApiKey()).thenReturn(null);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("请先配置DeepSeek API密钥", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(properties, times(1)).getApiKey();
        verify(restTemplate, never()).postForObject(anyString(), any(HttpEntity.class), any(Class.class));
    }

    @Test
    @DisplayName("测试AI对话-成功（无历史对话，无自定义系统提示词）")
    void testChat_Success_NoHistory_NoCustomSystemPrompt() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        DeepSeekChatResponse response = new DeepSeekChatResponse();
        DeepSeekChatResponse.Choice choice = new DeepSeekChatResponse.Choice();
        DeepSeekChatResponse.Message message = new DeepSeekChatResponse.Message();
        message.setContent("你好！我是AI助手。");
        choice.setMessage(message);
        response.setChoices(Arrays.asList(choice));

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenReturn(response);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("AI对话成功", result.getMessage(), "消息应匹配");
        assertEquals("你好！我是AI助手。", result.getData(), "回复内容应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-成功（有历史对话，有自定义系统提示词和温度参数）")
    void testChat_Success_WithHistory_WithCustomSystemPrompt() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("继续");
        request.setSystemPrompt("自定义系统提示词");
        request.setTemperature(0.9);

        ChatMessageDTO historyMsg1 = new ChatMessageDTO();
        historyMsg1.setRole("user");
        historyMsg1.setContent("你好");
        ChatMessageDTO historyMsg2 = new ChatMessageDTO();
        historyMsg2.setRole("assistant");
        historyMsg2.setContent("你好！有什么可以帮助你的吗？");
        request.setHistory(Arrays.asList(historyMsg1, historyMsg2));

        DeepSeekChatResponse response = new DeepSeekChatResponse();
        DeepSeekChatResponse.Choice choice = new DeepSeekChatResponse.Choice();
        DeepSeekChatResponse.Message message = new DeepSeekChatResponse.Message();
        message.setContent("好的，我继续为你服务。");
        choice.setMessage(message);
        response.setChoices(Arrays.asList(choice));

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenReturn(response);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("好的，我继续为你服务。", result.getData(), "回复内容应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-成功（有使用统计信息）")
    void testChat_Success_WithUsage() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        DeepSeekChatResponse response = new DeepSeekChatResponse();
        DeepSeekChatResponse.Choice choice = new DeepSeekChatResponse.Choice();
        DeepSeekChatResponse.Message message = new DeepSeekChatResponse.Message();
        message.setContent("你好！");
        choice.setMessage(message);
        response.setChoices(Arrays.asList(choice));

        DeepSeekChatResponse.Usage usage = new DeepSeekChatResponse.Usage();
        usage.setPromptTokens(10);
        usage.setCompletionTokens(5);
        usage.setTotalTokens(15);
        response.setUsage(usage);

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenReturn(response);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getExtra(), "Extra不应为null");
        assertEquals(usage, result.getExtra().get("usage"), "使用统计信息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-响应为null")
    void testChat_ResponseIsNull() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenReturn(null);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("AI服务暂未返回内容，请稍后重试", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-响应choices为空")
    void testChat_ResponseChoicesEmpty() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        DeepSeekChatResponse response = new DeepSeekChatResponse();
        response.setChoices(new ArrayList<>());

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenReturn(response);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("AI服务暂未返回内容，请稍后重试", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-RestClientResponseException异常（有错误体）")
    void testChat_RestClientResponseException_WithErrorBody() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        RestClientResponseException exception = mock(RestClientResponseException.class);
        when(exception.getResponseBodyAsString()).thenReturn("{\"error\": \"Invalid API key\"}");

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenThrow(exception);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertTrue(result.getMessage().contains("AI服务调用失败"), "错误消息应包含相关信息");
        assertTrue(result.getMessage().contains("Invalid API key"), "错误消息应包含错误体内容");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-RestClientResponseException异常（无错误体）")
    void testChat_RestClientResponseException_NoErrorBody() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        RestClientResponseException exception = mock(RestClientResponseException.class);
        when(exception.getResponseBodyAsString()).thenReturn(null);

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenThrow(exception);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("AI服务调用失败，请检查API密钥和网络连接", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-RestClientException异常（超时）")
    void testChat_RestClientException_Timeout() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        RestClientException exception = new RestClientException("Connection timeout");

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenThrow(exception);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("请求超时，请稍后重试", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-RestClientException异常（连接被拒绝）")
    void testChat_RestClientException_ConnectionRefused() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        RestClientException exception = new RestClientException("Connection refused");

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenThrow(exception);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("无法连接到AI服务，请检查网络", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-RestClientException异常（SSL证书错误）")
    void testChat_RestClientException_SSLError() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        RestClientException exception = new RestClientException("SSL handshake failed");

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenThrow(exception);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("SSL证书验证失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-RestClientException异常（其他网络错误）")
    void testChat_RestClientException_OtherError() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        RestClientException exception = new RestClientException("Network error occurred");

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenThrow(exception);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("网络请求异常: Network error occurred", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-通用Exception异常")
    void testChat_GenericException() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        RuntimeException exception = new RuntimeException("Unexpected error");

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenThrow(exception);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("处理异常: Unexpected error", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }

    @Test
    @DisplayName("测试AI对话-历史对话过滤空消息")
    void testChat_HistoryFilterEmptyMessages() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("你好");

        ChatMessageDTO historyMsg1 = new ChatMessageDTO();
        historyMsg1.setRole("user");
        historyMsg1.setContent(""); // 空内容，应被过滤
        ChatMessageDTO historyMsg2 = new ChatMessageDTO();
        historyMsg2.setRole(""); // 空角色，应被过滤
        historyMsg2.setContent("内容");
        ChatMessageDTO historyMsg3 = new ChatMessageDTO();
        historyMsg3.setRole("assistant");
        historyMsg3.setContent("有效消息");
        request.setHistory(Arrays.asList(historyMsg1, historyMsg2, historyMsg3));

        DeepSeekChatResponse response = new DeepSeekChatResponse();
        DeepSeekChatResponse.Choice choice = new DeepSeekChatResponse.Choice();
        DeepSeekChatResponse.Message message = new DeepSeekChatResponse.Message();
        message.setContent("回复");
        choice.setMessage(message);
        response.setChoices(Arrays.asList(choice));

        // Mock
        when(restTemplate.postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        )).thenReturn(response);

        // 执行测试
        ResponseResult result = aiService.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(restTemplate, times(1)).postForObject(
                eq(properties.getChatUrl()),
                any(HttpEntity.class),
                eq(DeepSeekChatResponse.class)
        );
    }
}

