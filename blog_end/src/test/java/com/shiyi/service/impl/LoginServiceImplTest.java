package com.shiyi.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.google.code.kaptcha.Producer;
import com.shiyi.common.RedisConstants;
import com.shiyi.common.ResponseResult;
import com.shiyi.dto.LoginDTO;
import com.shiyi.entity.User;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.UserMapper;
import com.shiyi.service.RedisService;
import com.shiyi.utils.AesEncryptUtils;
import com.shiyi.vo.SystemUserVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;

import static com.shiyi.common.ResultCode.ERROR_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LoginServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("登录服务实现类测试")
class LoginServiceImplTest {

    @Mock
    private Producer captchaProducerMath;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisService redisService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LoginServiceImpl loginService;

    // ==================== login 方法测试 ====================

    @Test
    @DisplayName("测试登录-成功（不记住我）")
    void testLogin_Success_NoRememberMe() {
        // 准备测试数据
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setUuid("test-uuid");
        dto.setCode("1234");
        dto.setRememberMe(false);

        User user = User.builder()
                .id(1)
                .username("testuser")
                .password("encrypted_password")
                .build();

        SystemUserVO systemUserVO = new SystemUserVO();
        systemUserVO.setId(1);
        systemUserVO.setUsername("testuser");

        String token = "test-token-12345";
        String encryptedPassword = "encrypted_password";

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<AesEncryptUtils> aesEncryptUtilsMock = mockStatic(AesEncryptUtils.class)) {

            when(redisService.getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid()))
                    .thenReturn(dto.getCode());
            aesEncryptUtilsMock.when(() -> AesEncryptUtils.aesEncrypt(dto.getPassword()))
                    .thenReturn(encryptedPassword);
            when(userMapper.selectNameAndPassword(dto.getUsername(), encryptedPassword))
                    .thenReturn(user);
            
            SaSession session = mock(SaSession.class);
            stpUtilMock.when(() -> StpUtil.login(eq(1L), eq("system"))).thenAnswer(invocation -> null);
            stpUtilMock.when(StpUtil::getSession).thenReturn(session);
            // SaSession.set() 返回 SaSession（链式调用），不是 void
            when(session.set(anyString(), any())).thenReturn(session);
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn(token);
            
            when(userMapper.getById(user.getId())).thenReturn(systemUserVO);

            // 执行测试
            ResponseResult result = loginService.login(dto);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals(token, result.getData(), "Token应匹配");

            // 验证调用
            verify(redisService, times(1)).getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid());
            verify(userMapper, times(1)).selectNameAndPassword(dto.getUsername(), encryptedPassword);
            stpUtilMock.verify(() -> StpUtil.login(eq(1L), eq("system")));
            verify(session, times(1)).set(anyString(), any());
            stpUtilMock.verify(() -> StpUtil.getTokenValue());
        }
    }

    @Test
    @DisplayName("测试登录-成功（记住我）")
    void testLogin_Success_RememberMe() {
        // 准备测试数据
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setUuid("test-uuid");
        dto.setCode("1234");
        dto.setRememberMe(true);

        User user = User.builder()
                .id(1)
                .username("testuser")
                .password("encrypted_password")
                .build();

        SystemUserVO systemUserVO = new SystemUserVO();
        systemUserVO.setId(1);
        systemUserVO.setUsername("testuser");

        String token = "test-token-12345";
        String encryptedPassword = "encrypted_password";

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<AesEncryptUtils> aesEncryptUtilsMock = mockStatic(AesEncryptUtils.class)) {

            when(redisService.getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid()))
                    .thenReturn(dto.getCode());
            aesEncryptUtilsMock.when(() -> AesEncryptUtils.aesEncrypt(dto.getPassword()))
                    .thenReturn(encryptedPassword);
            when(userMapper.selectNameAndPassword(dto.getUsername(), encryptedPassword))
                    .thenReturn(user);
            
            SaSession session = mock(SaSession.class);
            stpUtilMock.when(() -> StpUtil.login(eq(1L), any(SaLoginModel.class))).thenAnswer(invocation -> null);
            stpUtilMock.when(StpUtil::getSession).thenReturn(session);
            // SaSession.set() 返回 SaSession（链式调用），不是 void
            when(session.set(anyString(), any())).thenReturn(session);
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn(token);
            
            when(userMapper.getById(user.getId())).thenReturn(systemUserVO);

            // 执行测试
            ResponseResult result = loginService.login(dto);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals(token, result.getData(), "Token应匹配");

            // 验证调用
            verify(redisService, times(1)).getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid());
            verify(userMapper, times(1)).selectNameAndPassword(dto.getUsername(), encryptedPassword);
            stpUtilMock.verify(() -> StpUtil.login(eq(1L), any(SaLoginModel.class)));
            verify(session, times(1)).set(anyString(), any());
            stpUtilMock.verify(() -> StpUtil.getTokenValue());
        }
    }

    @Test
    @DisplayName("测试登录-验证码为空")
    void testLogin_CaptchaNull() {
        // 准备测试数据
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setUuid("test-uuid");
        dto.setCode("1234");
        dto.setRememberMe(false);

        // Mock
        when(redisService.getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid()))
                .thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginService.login(dto);
        });

        assertEquals("验证码已失效或回答错误!", exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(redisService, times(1)).getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid());
        verify(userMapper, never()).selectNameAndPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("测试登录-验证码错误")
    void testLogin_CaptchaMismatch() {
        // 准备测试数据
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setUuid("test-uuid");
        dto.setCode("1234");
        dto.setRememberMe(false);

        // Mock
        when(redisService.getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid()))
                .thenReturn("5678"); // 不同的验证码

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginService.login(dto);
        });

        assertEquals("验证码已失效或回答错误!", exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(redisService, times(1)).getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid());
        verify(userMapper, never()).selectNameAndPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("测试登录-用户名或密码错误")
    void testLogin_InvalidCredentials() {
        // 准备测试数据
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpassword");
        dto.setUuid("test-uuid");
        dto.setCode("1234");
        dto.setRememberMe(false);

        String encryptedPassword = "encrypted_password";

        // Mock
        try (MockedStatic<AesEncryptUtils> aesEncryptUtilsMock = mockStatic(AesEncryptUtils.class)) {
            when(redisService.getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid()))
                    .thenReturn(dto.getCode());
            aesEncryptUtilsMock.when(() -> AesEncryptUtils.aesEncrypt(dto.getPassword()))
                    .thenReturn(encryptedPassword);
            when(userMapper.selectNameAndPassword(dto.getUsername(), encryptedPassword))
                    .thenReturn(null);

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                loginService.login(dto);
            });

            assertEquals(ERROR_PASSWORD.getDesc(), exception.getMessage(), "异常消息应匹配");

            // 验证调用
            verify(redisService, times(1)).getCacheObject(RedisConstants.CAPTCHA_CODE + dto.getUuid());
            verify(userMapper, times(1)).selectNameAndPassword(dto.getUsername(), encryptedPassword);
        }
    }
}

