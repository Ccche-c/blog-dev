package com.shiyi.service.impl;

import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Message;
import com.shiyi.mapper.MessageMapper;
import com.shiyi.utils.IpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shiyi.common.ResultCode.PARAMS_ILLEGAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("留言服务实现类测试")
class MessageServiceImplTest {

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MessageServiceImpl messageService;

    @BeforeEach
    void setUp() {
        // 使用反射注入 baseMapper
        ReflectionTestUtils.setField(messageService, "baseMapper", messageMapper);
    }

    // ==================== passBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量通过留言-成功")
    void testPassBatch_Success() {
        // 准备测试数据
        List<Integer> ids = Arrays.asList(1, 2, 3);

        // Mock
        doNothing().when(messageMapper).passBatch(ids);

        // 执行测试
        ResponseResult result = messageService.passBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(messageMapper, times(1)).passBatch(ids);
    }

    @Test
    @DisplayName("测试批量通过留言-空列表")
    void testPassBatch_EmptyList() {
        // 准备测试数据
        List<Integer> ids = new ArrayList<>();

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            messageService.passBatch(ids);
        }, "应该抛出 IllegalArgumentException");

        assertEquals(PARAMS_ILLEGAL.getDesc(), exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(messageMapper, never()).passBatch(anyList());
    }

    @Test
    @DisplayName("测试批量通过留言-null列表")
    void testPassBatch_NullList() {
        // 准备测试数据
        List<Integer> ids = null;

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            messageService.passBatch(ids);
        }, "应该抛出 IllegalArgumentException");

        assertEquals(PARAMS_ILLEGAL.getDesc(), exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(messageMapper, never()).passBatch(anyList());
    }

    // ==================== deleteMessageById 方法测试 ====================

    @Test
    @DisplayName("测试删除留言-成功")
    void testDeleteMessageById_Success() {
        // 准备测试数据
        int id = 1;

        // Mock
        when(messageMapper.deleteById(id)).thenReturn(1);

        // 执行测试
        ResponseResult result = messageService.deleteMessageById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(messageMapper, times(1)).deleteById(id);
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除留言-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Integer> ids = Arrays.asList(1, 2, 3);

        // Mock
        when(messageMapper.deleteBatchIds(ids)).thenReturn(3);

        // 执行测试
        ResponseResult result = messageService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(messageMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试批量删除留言-失败（删除行数为0）")
    void testDeleteBatch_Failure() {
        // 准备测试数据
        List<Integer> ids = Arrays.asList(1, 2, 3);

        // Mock
        when(messageMapper.deleteBatchIds(ids)).thenReturn(0);

        // 执行测试
        ResponseResult result = messageService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("批量删除留言失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(messageMapper, times(1)).deleteBatchIds(ids);
    }

    // ==================== publicAddMessage 方法测试 ====================

    @Test
    @DisplayName("测试添加留言-成功")
    void testPublicAddMessage_Success() {
        // 准备测试数据
        Message message = new Message();
        message.setId(1);
        message.setNickname("测试用户");
        message.setContent("测试留言内容");
        message.setAvatar("avatar.jpg");

        String ipAddress = "192.168.1.1";
        String ipSource = "中国|北京|北京";

        // Mock
        try (MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class)) {
            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn(ipAddress);
            ipUtilMock.when(() -> IpUtil.getIp2region(ipAddress)).thenReturn(ipSource);
            when(messageMapper.insert(message)).thenReturn(1);

            // 执行测试
            ResponseResult result = messageService.publicAddMessage(message);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
            assertEquals("留言成功", result.getData(), "数据应匹配");
            assertEquals(ipAddress, message.getIpAddress(), "IP地址应设置");
            assertEquals(ipSource, message.getIpSource(), "IP来源应设置");

            // 验证调用
            ipUtilMock.verify(() -> IpUtil.getIp(request));
            ipUtilMock.verify(() -> IpUtil.getIp2region(ipAddress));
            verify(messageMapper, times(1)).insert(message);
        }
    }

    @Test
    @DisplayName("测试添加留言-成功（IP来源为null）")
    void testPublicAddMessage_Success_IpSourceNull() {
        // 准备测试数据
        Message message = new Message();
        message.setId(1);
        message.setNickname("测试用户");
        message.setContent("测试留言内容");

        String ipAddress = "192.168.1.1";
        String ipSource = null;

        // Mock
        try (MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class)) {
            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn(ipAddress);
            ipUtilMock.when(() -> IpUtil.getIp2region(ipAddress)).thenReturn(ipSource);
            when(messageMapper.insert(message)).thenReturn(1);

            // 执行测试
            ResponseResult result = messageService.publicAddMessage(message);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals(ipAddress, message.getIpAddress(), "IP地址应设置");
            assertNull(message.getIpSource(), "IP来源应为null");

            // 验证调用
            ipUtilMock.verify(() -> IpUtil.getIp(request));
            ipUtilMock.verify(() -> IpUtil.getIp2region(ipAddress));
            verify(messageMapper, times(1)).insert(message);
        }
    }
}

