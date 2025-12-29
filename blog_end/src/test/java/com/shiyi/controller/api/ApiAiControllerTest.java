package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.dto.AiChatRequest;
import com.shiyi.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ApiAiController 单元测试
 * 使用JaCoCo进行代码覆盖率测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AI对话接口测试")
class ApiAiControllerTest {

    @Mock
    private AiService aiService;

    @InjectMocks
    private ApiAiController apiAiController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
        reset(aiService);
    }

    @Test
    @DisplayName("测试AI对话-成功场景")
    void testChat_Success() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("如何系统学习spring boot（5000字）");
        
        ResponseResult expectedResult = ResponseResult.success("AI对话成功", "推荐内容...");

        // 模拟Service返回
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiAiController.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        assertEquals("AI对话成功", result.getMessage(), "消息应匹配");
        verify(aiService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("测试AI对话-失败场景")
    void testChat_Failure() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("测试消息");
        
        ResponseResult expectedResult = ResponseResult.error("AI服务调用失败");

        // 模拟Service返回
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiAiController.chat(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("AI服务调用失败", result.getMessage(), "错误消息应匹配");
        verify(aiService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("测试AI对话-带系统提示词")
    void testChat_WithSystemPrompt() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("推荐一本书");
        request.setSystemPrompt("你是一个专业的图书推荐助手");
        
        ResponseResult expectedResult = ResponseResult.success("AI对话成功", "推荐《深入理解Java虚拟机》");

        // 模拟Service返回
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiAiController.chat(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(aiService, times(1)).chat(argThat(req -> 
            req.getSystemPrompt().equals("你是一个专业的图书推荐助手")
        ));
    }

    @Test
    @DisplayName("测试AI对话-带温度参数")
    void testChat_WithTemperature() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("写一首诗");
        request.setTemperature(0.7);
        
        ResponseResult expectedResult = ResponseResult.success("AI对话成功", "诗歌内容...");

        // 模拟Service返回
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiAiController.chat(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(aiService, times(1)).chat(argThat(req -> 
            req.getTemperature() != null && req.getTemperature() == 0.7
        ));
    }

    @Test
    @DisplayName("测试AI对话-空消息内容")
    void testChat_EmptyMessage() {
        // 准备测试数据 - 空消息
        AiChatRequest request = new AiChatRequest();
        request.setMessage("");

        // 由于使用了@Valid注解，空消息会触发验证异常
        // 但这里我们测试的是Controller层，验证会在更早的阶段触发
        // 如果验证通过，Service应该被调用
        ResponseResult expectedResult = ResponseResult.error("消息内容不能为空");

        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiAiController.chat(request);

        // 验证结果
        assertNotNull(result);
        verify(aiService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("测试AI对话-长消息内容")
    void testChat_LongMessage() {
        // 准备测试数据 - 长消息
        AiChatRequest request = new AiChatRequest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("这是一个很长的消息内容");
        }
        String longMessage = sb.toString();
        request.setMessage(longMessage);
        
        ResponseResult expectedResult = ResponseResult.success("AI对话成功", "处理结果");

        // 模拟Service返回
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiAiController.chat(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(aiService, times(1)).chat(argThat(req -> 
            req.getMessage().length() > 100
        ));
    }

    @Test
    @DisplayName("测试AI对话-Service抛出异常")
    void testChat_ServiceException() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("测试异常");

        // 模拟Service抛出异常
        when(aiService.chat(any(AiChatRequest.class)))
                .thenThrow(new RuntimeException("服务异常"));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            apiAiController.chat(request);
        });

        verify(aiService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("测试AI对话-返回null数据")
    void testChat_NullData() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("测试消息");
        
        ResponseResult expectedResult = ResponseResult.success("成功", null);

        // 模拟Service返回null数据
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiAiController.chat(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
        verify(aiService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("测试AI对话-验证Service调用参数")
    void testChat_VerifyServiceCall() {
        // 准备测试数据
        AiChatRequest request = new AiChatRequest();
        request.setMessage("测试消息");
        request.setSystemPrompt("自定义提示词");
        request.setTemperature(0.8);
        
        ResponseResult expectedResult = ResponseResult.success("成功", "数据");

        // 模拟Service返回
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        apiAiController.chat(request);

        // 验证Service被调用，且参数正确
        verify(aiService, times(1)).chat(argThat(req -> 
            req.getMessage().equals("测试消息") &&
            req.getSystemPrompt().equals("自定义提示词") &&
            req.getTemperature() == 0.8
        ));
    }

    @Test
    @DisplayName("测试AI对话-多次调用")
    void testChat_MultipleCalls() {
        // 准备测试数据
        AiChatRequest request1 = new AiChatRequest();
        request1.setMessage("第一次调用");
        
        AiChatRequest request2 = new AiChatRequest();
        request2.setMessage("第二次调用");
        
        ResponseResult result1 = ResponseResult.success("成功1", "数据1");
        ResponseResult result2 = ResponseResult.success("成功2", "数据2");

        // 模拟Service返回
        when(aiService.chat(any(AiChatRequest.class)))
                .thenReturn(result1)
                .thenReturn(result2);

        // 执行测试
        ResponseResult response1 = apiAiController.chat(request1);
        ResponseResult response2 = apiAiController.chat(request2);

        // 验证结果
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(200, response1.getCode());
        assertEquals(200, response2.getCode());
        verify(aiService, times(2)).chat(any(AiChatRequest.class));
    }
}

