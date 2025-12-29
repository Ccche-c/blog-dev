package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.dto.EmailLoginDTO;
import com.shiyi.dto.EmailRegisterDTO;
import com.shiyi.dto.QQLoginDTO;
import com.shiyi.dto.UserInfoDTO;
import com.shiyi.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ApiUserController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户登录注册接口测试")
class ApiUserControllerTest {

    @Mock
    private UserInfoService userInfoService;

    @InjectMocks
    private ApiUserController apiUserController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试邮箱注册-成功")
    void testEmailRegister_Success() {
        // 准备测试数据
        EmailRegisterDTO registerDTO = new EmailRegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("123456");
        registerDTO.setCode("123456");
        
        ResponseResult expectedResult = ResponseResult.success("注册成功");

        // 模拟Service返回
        when(userInfoService.emailRegister(any(EmailRegisterDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserController.emailRegister(registerDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).emailRegister(any(EmailRegisterDTO.class));
    }

    @Test
    @DisplayName("测试邮箱登录-成功")
    void testEmailLogin_Success() {
        // 准备测试数据
        EmailLoginDTO loginDTO = new EmailLoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("123456");
        
        ResponseResult expectedResult = ResponseResult.success("登录成功");

        // 模拟Service返回
        when(userInfoService.emailLogin(any(EmailLoginDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserController.emailLogin(loginDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).emailLogin(any(EmailLoginDTO.class));
    }

    @Test
    @DisplayName("测试修改密码-成功")
    void testUpdatePassword_Success() {
        // 准备测试数据
        EmailRegisterDTO registerDTO = new EmailRegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("newPassword123");
        registerDTO.setCode("123456");
        
        ResponseResult expectedResult = ResponseResult.success("修改密码成功");

        // 模拟Service返回
        when(userInfoService.updatePassword(any(EmailRegisterDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserController.updatePassword(registerDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).updatePassword(any(EmailRegisterDTO.class));
    }

//    @Test
//    @DisplayName("测试QQ登录-成功")
//    void testQQLogin_Success() {
//        // 准备测试数据
//        QQLoginDTO qqLoginDTO = new QQLoginDTO();
//        qqLoginDTO.setOpenId("test_open_id");
//
//        ResponseResult expectedResult = ResponseResult.success("登录成功");
//
//        // 模拟Service返回
//        when(userInfoService.qqLogin(any(QQLoginDTO.class)))
//                .thenReturn(expectedResult);
//
//        // 执行测试
//        ResponseResult result = apiUserController.login(qqLoginDTO);
//
//        // 验证结果
//        assertNotNull(result);
//        assertEquals(200, result.getCode());
//        verify(userInfoService, times(1)).qqLogin(any(QQLoginDTO.class));
//    }

//    @Test
//    @DisplayName("测试Gitee登录-成功")
//    void testGiteeLogin_Success() {
//        // 准备测试数据
//        String code = "test_code";
//        ResponseResult expectedResult = ResponseResult.success("登录成功");
//
//        // 模拟Service返回
//        when(userInfoService.giteeLogin(code))
//                .thenReturn(expectedResult);
//
//        // 执行测试
//        ResponseResult result = apiUserController.gitEELogin(code);
//
//        // 验证结果
//        assertNotNull(result);
//        assertEquals(200, result.getCode());
//        verify(userInfoService, times(1)).giteeLogin(code);
//    }

//    @Test
//    @DisplayName("测试微博登录-成功")
//    void testWeiboLogin_Success() {
//        // 准备测试数据
//        String code = "test_code";
//        ResponseResult expectedResult = ResponseResult.success("登录成功");
//
//        // 模拟Service返回
//        when(userInfoService.weiboLogin(code))
//                .thenReturn(expectedResult);
//
//        // 执行测试
//        ResponseResult result = apiUserController.weiboLogin(code);
//
//        // 验证结果
//        assertNotNull(result);
//        assertEquals(200, result.getCode());
//        verify(userInfoService, times(1)).weiboLogin(code);
//    }

//    @Test
//    @DisplayName("测试获取微信登录二维码-成功")
//    void testWxQr_Success() {
//        // 准备测试数据
//        ResponseResult expectedResult = ResponseResult.success("获取成功");
//
//        // 模拟Service返回
//        when(userInfoService.wxQr())
//                .thenReturn(expectedResult);
//
//        // 执行测试
//        ResponseResult result = apiUserController.wxQr();
//
//        // 验证结果
//        assertNotNull(result);
//        assertEquals(200, result.getCode());
//        verify(userInfoService, times(1)).wxQr();
//    }

//    @Test
//    @DisplayName("测试微信登录回调-成功")
//    void testWxCallBack_Success() {
//        // 准备测试数据
//        String body = "{\"code\":\"test_code\"}";
//        Map<String, Object> expectedResult = new HashMap<>();
//        expectedResult.put("success", true);
//
//        // 模拟Service返回
//        when(userInfoService.wechatLogin(body))
//                .thenReturn(expectedResult);
//
//        // 执行测试
//        Map<String, Object> result = apiUserController.wxCallBack(body);
//
//        // 验证结果
//        assertNotNull(result);
//        verify(userInfoService, times(1)).wechatLogin(body);
//    }

//    @Test
//    @DisplayName("测试判断微信登录状态-成功")
//    void testWxIsLogin_Success() {
//        // 准备测试数据
//        String tempUserId = "temp_user_123";
//        ResponseResult expectedResult = ResponseResult.success("登录成功");
//
//        // 模拟Service返回
//        when(userInfoService.wxIsLogin(tempUserId))
//                .thenReturn(expectedResult);
//
//        // 执行测试
//        ResponseResult result = apiUserController.wxIsLogin(tempUserId);
//
//        // 验证结果
//        assertNotNull(result);
//        assertEquals(200, result.getCode());
//        verify(userInfoService, times(1)).wxIsLogin(tempUserId);
//    }

    @Test
    @DisplayName("测试发送邮箱验证码-成功")
    void testSendEmailCode_Success() {
        // 准备测试数据
        String email = "test@example.com";
        ResponseResult expectedResult = ResponseResult.success("验证码已发送");

        // 模拟Service返回
        when(userInfoService.sendEmailCode(email))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserController.sendEmailCode(email);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).sendEmailCode(email);
    }

    @Test
    @DisplayName("测试绑定邮箱-成功")
    void testPublicBindEmail_Success() {
        // 准备测试数据
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail("test@example.com");
        
        ResponseResult expectedResult = ResponseResult.success("绑定成功");

        // 模拟Service返回
        when(userInfoService.publicBindEmail(any(UserInfoDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserController.publicBindEmail(userInfoDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).publicBindEmail(any(UserInfoDTO.class));
    }

    @Test
    @DisplayName("测试获取用户信息-成功")
    void testPublicSelectUserInfo_Success() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(userInfoService.publicSelectUserInfo())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserController.publicSelectUserInfo();

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).publicSelectUserInfo();
    }

    @Test
    @DisplayName("测试修改用户信息-成功")
    void testPublicUpdateUser_Success() {
        // 准备测试数据
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setNickname("新昵称");
        
        ResponseResult expectedResult = ResponseResult.success("修改成功");

        // 模拟Service返回
        when(userInfoService.publicUpdateUser(any(UserInfoDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserController.publicUpdateUser(userInfoDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).publicUpdateUser(any(UserInfoDTO.class));
    }
}

