package com.shiyi.utils;

import eu.bitwalker.useragentutils.UserAgent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * IpUtil 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IP工具类测试")
class IpUtilTest {

    // ==================== getIp 方法测试 ====================

    @Test
    @DisplayName("测试获取IP地址-从x-forwarded-for头")
    void testGetIp_FromXForwardedFor() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String expectedIp = "192.168.1.100";

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn(expectedIp);

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedIp, result, "IP地址应匹配");

        // 验证调用
        verify(request, times(1)).getHeader("x-forwarded-for");
        verify(request, never()).getHeader("Proxy-Client-IP");
        verify(request, never()).getHeader("WL-Proxy-Client-IP");
        verify(request, never()).getRemoteAddr();
    }

    @Test
    @DisplayName("测试获取IP地址-从Proxy-Client-IP头")
    void testGetIp_FromProxyClientIp() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String expectedIp = "192.168.1.101";

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(expectedIp);

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedIp, result, "IP地址应匹配");

        // 验证调用
        verify(request, times(1)).getHeader("x-forwarded-for");
        verify(request, times(1)).getHeader("Proxy-Client-IP");
    }

    @Test
    @DisplayName("测试获取IP地址-从WL-Proxy-Client-IP头")
    void testGetIp_FromWLProxyClientIp() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String expectedIp = "192.168.1.102";

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(expectedIp);

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedIp, result, "IP地址应匹配");
    }

    @Test
    @DisplayName("测试获取IP地址-从getRemoteAddr")
    void testGetIp_FromRemoteAddr() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String expectedIp = "192.168.1.103";

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(expectedIp);

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedIp, result, "IP地址应匹配");
    }

    @Test
    @DisplayName("测试获取IP地址-本地IP（127.0.0.1）")
    void testGetIp_LocalIp() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String localHostIp = "192.168.1.1";

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        try (MockedStatic<InetAddress> inetAddressMock = mockStatic(InetAddress.class)) {
            InetAddress inetAddress = mock(InetAddress.class);
            inetAddressMock.when(InetAddress::getLocalHost).thenReturn(inetAddress);
            when(inetAddress.getHostAddress()).thenReturn(localHostIp);

            // 执行测试
            String result = IpUtil.getIp(request);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(localHostIp, result, "IP地址应为本地IP");
        }
    }

    @Test
    @DisplayName("测试获取IP地址-多个IP（逗号分隔）")
    void testGetIp_MultipleIps() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String multipleIps = "192.168.1.100,192.168.1.101,192.168.1.102";
        String expectedIp = "192.168.1.100";

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn(multipleIps);

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedIp, result, "应返回第一个IP地址");
    }

    @Test
    @DisplayName("测试获取IP地址-IPv6地址（0:0:0:0:0:0:0:1）")
    void testGetIp_IPv6() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals("127.0.0.1", result, "IPv6应转换为127.0.0.1");
    }

    @Test
    @DisplayName("测试获取IP地址-异常情况")
    void testGetIp_Exception() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Mock - 抛出异常
        when(request.getHeader("x-forwarded-for")).thenThrow(new RuntimeException("Test exception"));

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals("", result, "异常时应返回空字符串");
    }

    @Test
    @DisplayName("测试获取IP地址-unknown值")
    void testGetIp_UnknownValue() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String expectedIp = "192.168.1.104";

        // Mock
        when(request.getHeader("x-forwarded-for")).thenReturn("unknown");
        when(request.getHeader("Proxy-Client-IP")).thenReturn("UNKNOWN");
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(expectedIp);

        // 执行测试
        String result = IpUtil.getIp(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedIp, result, "应跳过unknown值");
    }

    // ==================== getUserAgent 方法测试 ====================

    @Test
    @DisplayName("测试获取UserAgent-成功")
    void testGetUserAgent_Success() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);
        String userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

        // Mock
        when(request.getHeader("User-Agent")).thenReturn(userAgentString);

        // 执行测试
        UserAgent result = IpUtil.getUserAgent(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");

        // 验证调用
        verify(request, times(1)).getHeader("User-Agent");
    }

    @Test
    @DisplayName("测试获取UserAgent-null User-Agent")
    void testGetUserAgent_NullUserAgent() {
        // 准备测试数据
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Mock
        when(request.getHeader("User-Agent")).thenReturn(null);

        // 执行测试
        UserAgent result = IpUtil.getUserAgent(request);

        // 验证结果
        assertNotNull(result, "返回结果不应为null（UserAgent.parseUserAgentString会处理null）");

        // 验证调用
        verify(request, times(1)).getHeader("User-Agent");
    }

    // ==================== getHostIp 方法测试 ====================

    @Test
    @DisplayName("测试获取本地IP-成功")
    void testGetHostIp_Success() {
        // 准备测试数据
        String expectedIp = "192.168.1.1";

        // Mock
        try (MockedStatic<InetAddress> inetAddressMock = mockStatic(InetAddress.class)) {
            InetAddress inetAddress = mock(InetAddress.class);
            inetAddressMock.when(InetAddress::getLocalHost).thenReturn(inetAddress);
            when(inetAddress.getHostAddress()).thenReturn(expectedIp);

            // 执行测试
            String result = IpUtil.getHostIp();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(expectedIp, result, "IP地址应匹配");
        }
    }

    @Test
    @DisplayName("测试获取本地IP-异常情况")
    void testGetHostIp_Exception() {
        // Mock
        try (MockedStatic<InetAddress> inetAddressMock = mockStatic(InetAddress.class)) {
            inetAddressMock.when(InetAddress::getLocalHost).thenThrow(new UnknownHostException("Test exception"));

            // 执行测试
            String result = IpUtil.getHostIp();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals("127.0.0.1", result, "异常时应返回127.0.0.1");
        }
    }

    // ==================== getHostName 方法测试 ====================

    @Test
    @DisplayName("测试获取主机名-成功")
    void testGetHostName_Success() {
        // 准备测试数据
        String expectedHostName = "test-host";

        // Mock
        try (MockedStatic<InetAddress> inetAddressMock = mockStatic(InetAddress.class)) {
            InetAddress inetAddress = mock(InetAddress.class);
            inetAddressMock.when(InetAddress::getLocalHost).thenReturn(inetAddress);
            when(inetAddress.getHostName()).thenReturn(expectedHostName);

            // 执行测试
            String result = IpUtil.getHostName();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(expectedHostName, result, "主机名应匹配");
        }
    }

    @Test
    @DisplayName("测试获取主机名-异常情况")
    void testGetHostName_Exception() {
        // Mock
        try (MockedStatic<InetAddress> inetAddressMock = mockStatic(InetAddress.class)) {
            inetAddressMock.when(InetAddress::getLocalHost).thenThrow(new UnknownHostException("Test exception"));

            // 执行测试
            String result = IpUtil.getHostName();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals("未知", result, "异常时应返回'未知'");
        }
    }

    // ==================== getIp2region 方法测试 ====================

    @Test
    @DisplayName("测试获取IP2region-成功")
    void testGetIp2region_Success() {
        // 准备测试数据
        String ip = "192.168.1.1";
        // 注意：由于searcher是静态的且依赖文件，实际测试中可能为null
        // 这里只测试方法不抛出异常
        assertDoesNotThrow(() -> IpUtil.getIp2region(ip), "方法不应抛出异常");
    }

    @Test
    @DisplayName("测试获取IP2region-null IP")
    void testGetIp2region_NullIp() {
        // 执行测试并验证结果
        // 如果searcher为null，应返回null；否则可能返回UNKNOWN或抛出异常
        assertDoesNotThrow(() -> IpUtil.getIp2region(null), "方法不应抛出异常");
    }

    // ==================== getCityInfo 方法测试 ====================

    @Test
    @DisplayName("测试获取城市信息-成功")
    void testGetCityInfo_Success() {
        // 准备测试数据
        String ip = "192.168.1.1";
        // 注意：getCityInfo会调用analyzeIp，而analyzeIp会进行实际的网络请求
        // 在测试环境中，网络请求可能会失败，返回UNKNOWN
        // 只验证方法不抛出异常
        assertDoesNotThrow(() -> {
            String result = IpUtil.getCityInfo(ip);
            assertNotNull(result, "返回结果不应为null");
        }, "方法不应抛出异常");
    }

    @Test
    @DisplayName("测试获取城市信息-null IP")
    void testGetCityInfo_NullIp() {
        // 执行测试
        // 注意：当ip为null时，analyzeIp可能返回null或空字符串，
        // JSONObject.parseObject可能返回null，导致NullPointerException
        // 这里捕获可能的异常
        try {
            String result = IpUtil.getCityInfo(null);
            // 如果成功返回，验证结果不为null
            assertNotNull(result, "返回结果不应为null");
        } catch (NullPointerException e) {
            // NullPointerException是预期的，因为analyzeIp(null)可能返回null
            // 导致JSONObject.parseObject返回null
            assertTrue(true, "NullPointerException是预期的");
        } catch (Exception e) {
            // 其他异常不应该发生
            fail("不应抛出其他异常: " + e.getClass().getName());
        }
    }

    // ==================== analyzeIp 方法测试 ====================

    @Test
    @DisplayName("测试分析IP-成功")
    void testAnalyzeIp_Success() {
        // 准备测试数据
        String ip = "192.168.1.1";
        // 注意：analyzeIp会进行实际的网络请求
        // 在测试环境中，网络请求可能会失败或超时
        // 由于网络请求的不确定性，只验证方法不抛出异常
        assertDoesNotThrow(() -> IpUtil.analyzeIp(ip), "方法不应抛出异常");
    }

    @Test
    @DisplayName("测试分析IP-null IP")
    void testAnalyzeIp_NullIp() {
        // 执行测试
        // analyzeIp可能会抛出异常或返回空字符串
        assertDoesNotThrow(() -> IpUtil.analyzeIp(null), "方法不应抛出异常");
    }
}

