package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.service.impl.HomeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ApiHomeController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("门户首页管理接口测试")
class ApiHomeControllerTest {

    @Mock
    private HomeServiceImpl homeService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ApiHomeController apiHomeController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试增加访问量-成功")
    void testReport_Success() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("访问量增加成功");

        // 模拟Service返回
        when(homeService.report(any(HttpServletRequest.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiHomeController.report(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(homeService, times(1)).report(any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("测试获取网站信息-成功")
    void testGetWebSiteInfo_Success() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(homeService.getWebSiteInfo())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiHomeController.getWebSiteInfo();

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(homeService, times(1)).getWebSiteInfo();
    }

    @Test
    @DisplayName("测试获取首页共享数据-成功")
    void testSelectHomeData_Success() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(homeService.selectPubicData())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiHomeController.selectHomeData();

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(homeService, times(1)).selectPubicData();
    }
}

