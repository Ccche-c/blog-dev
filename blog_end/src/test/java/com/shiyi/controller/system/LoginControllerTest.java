package com.shiyi.controller.system;

import cn.dev33.satoken.stp.StpUtil;
import com.shiyi.common.ResponseResult;
import com.shiyi.dto.LoginDTO;
import com.shiyi.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * LoginController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("系统登录接口测试")
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试获取验证码-成功")
    void testGetCode_Success() throws IOException {
        // 准备测试数据
        Map<String, String> codeData = new HashMap<>();
        codeData.put("code", "1234");
        codeData.put("uuid", "test-uuid");

        // 模拟Service返回
        when(loginService.getCode(any(HttpServletResponse.class)))
                .thenReturn(codeData);

        // 执行测试
        ResponseResult result = loginController.getCode(response);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(loginService, times(1)).getCode(any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("测试登录-成功")
    void testLogin_Success() {
        // 准备测试数据
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("123456");
        loginDTO.setCode("1234");
        loginDTO.setUuid("test-uuid");
        
        ResponseResult expectedResult = ResponseResult.success("登录成功");

        // 模拟Service返回
        when(loginService.login(any(LoginDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = loginController.login(loginDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(loginService, times(1)).login(any(LoginDTO.class));
    }

    @Test
    @DisplayName("测试退出登录-成功")
    void testLogout_Success() {
        // 使用MockedStatic模拟StpUtil静态方法
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // StpUtil.logout()是void方法，使用thenAnswer()模拟
            stpUtilMock.when(StpUtil::logout).thenAnswer(invocation -> {
                // void方法，什么都不做
                return null;
            });

            // 执行测试
            ResponseResult result = loginController.logout();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            // 注意：ResponseResult.success("退出成功")会将消息设置为"SUCCESS"，"退出成功"作为data
            assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
            assertEquals("退出成功", result.getData(), "数据应匹配");
            
            // 验证StpUtil.logout()被调用
            stpUtilMock.verify(StpUtil::logout);
        }
    }
}

