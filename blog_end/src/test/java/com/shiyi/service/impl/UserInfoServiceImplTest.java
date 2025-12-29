package com.shiyi.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiyi.common.*;
import com.shiyi.dto.EmailLoginDTO;
import com.shiyi.dto.EmailRegisterDTO;
import com.shiyi.dto.QQLoginDTO;
import com.shiyi.dto.UserInfoDTO;
import com.shiyi.entity.User;
import com.shiyi.entity.UserInfo;
import com.shiyi.entity.WebConfig;
import com.shiyi.enums.LoginTypeEnum;
import com.shiyi.enums.UserStatusEnum;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.UserInfoMapper;
import com.shiyi.mapper.UserMapper;
import com.shiyi.service.EmailService;
import com.shiyi.service.RedisService;
import com.shiyi.service.WebConfigService;
import com.shiyi.strategy.context.SocialLoginStrategyContext;
import com.shiyi.vo.UserInfoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserInfoServiceImpl 单元测试
 * 使用JaCoCo进行代码覆盖率测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户信息服务实现类测试")
@SuppressWarnings("unchecked")
class UserInfoServiceImplTest {

    @Mock
    private SocialLoginStrategyContext socialLoginStrategyContext;

    @Mock
    private EmailService emailService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisService redisService;

    @Mock
    private WebConfigService webConfigService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserInfoMapper userInfoMapper;

    private UserInfoServiceImpl userInfoService;

    @BeforeEach
    void setUp() throws Exception {
        // 重置所有Mock
        reset(socialLoginStrategyContext, emailService, userMapper, redisService, 
              webConfigService, request, userInfoMapper);

        // 创建Service实例
        userInfoService = new UserInfoServiceImpl(
                socialLoginStrategyContext,
                emailService,
                userMapper,
                redisService,
                webConfigService,
                request
        );

        // 使用反射设置baseMapper
        Field baseMapperField = userInfoService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(userInfoService, userInfoMapper);
    }

    // ==================== emailRegister 方法测试 ====================

    @Test
    @DisplayName("测试邮箱注册-成功场景")
    void testEmailRegister_Success() {
        // 准备测试数据
        EmailRegisterDTO dto = new EmailRegisterDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setNickname("测试用户");
        dto.setCode("123456");

        WebConfig webConfig = new WebConfig();
        webConfig.setTouristAvatar("default-avatar.jpg");

        // 模拟依赖返回
        when(redisService.getCacheObject(RedisConstants.EMAIL_CODE + dto.getEmail()))
                .thenReturn("123456");
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(null); // 用户不存在
        when(webConfigService.getOne(any(QueryWrapper.class)))
                .thenReturn(webConfig);
        when(userInfoMapper.insert(any(UserInfo.class)))
                .thenAnswer(invocation -> {
                    UserInfo info = invocation.getArgument(0);
                    info.setId(1);
                    return 1;
                });
        when(userMapper.insert(any(User.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = userInfoService.emailRegister(dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
        assertEquals("注册成功", result.getData(), "数据应匹配");
        verify(redisService, times(1)).getCacheObject(RedisConstants.EMAIL_CODE + dto.getEmail());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(userInfoMapper, times(1)).insert(any(UserInfo.class));
        verify(userMapper, times(1)).insert(any(User.class));
        verify(redisService, times(1)).deleteObject(RedisConstants.EMAIL_CODE + dto.getEmail());
    }

    @Test
    @DisplayName("测试邮箱注册-验证码错误")
    void testEmailRegister_InvalidCode() {
        // 准备测试数据
        EmailRegisterDTO dto = new EmailRegisterDTO();
        dto.setEmail("test@example.com");
        dto.setCode("wrong-code");

        // 模拟验证码不匹配
        when(redisService.getCacheObject(RedisConstants.EMAIL_CODE + dto.getEmail()))
                .thenReturn("123456");

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userInfoService.emailRegister(dto);
        }, "验证码错误时应抛出BusinessException");

        verify(redisService, times(1)).getCacheObject(RedisConstants.EMAIL_CODE + dto.getEmail());
        verify(userMapper, never()).selectOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("测试邮箱注册-邮箱已存在")
    void testEmailRegister_EmailExists() {
        // 准备测试数据
        EmailRegisterDTO dto = new EmailRegisterDTO();
        dto.setEmail("existing@example.com");
        dto.setCode("123456");

        User existingUser = User.builder()
                .id(1)
                .username(dto.getEmail())
                .build();

        // 模拟依赖返回
        when(redisService.getCacheObject(RedisConstants.EMAIL_CODE + dto.getEmail()))
                .thenReturn("123456");
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(existingUser); // 用户已存在

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userInfoService.emailRegister(dto);
        }, "邮箱已存在时应抛出BusinessException");

        verify(redisService, times(1)).getCacheObject(RedisConstants.EMAIL_CODE + dto.getEmail());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(userInfoMapper, never()).insert(any(UserInfo.class));
    }

    // ==================== updatePassword 方法测试 ====================

    @Test
    @DisplayName("测试修改密码-成功场景")
    void testUpdatePassword_Success() {
        // 准备测试数据
        EmailRegisterDTO dto = new EmailRegisterDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("newPassword123");
        dto.setCode("123456");

        User user = User.builder()
                .id(1)
                .username(dto.getEmail())
                .password("oldPassword")
                .build();

        // 模拟依赖返回
        when(redisService.getCacheObject(RedisConstants.EMAIL_CODE + RedisConstants.EMAIL_CODE + dto.getEmail()))
                .thenReturn("123456");
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(user);
        when(userMapper.updateById(any(User.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = userInfoService.updatePassword(dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
        assertEquals("修改成功", result.getData(), "数据应匹配");
        verify(userMapper, times(1)).updateById(any(User.class));
        verify(redisService, times(1)).deleteObject(RedisConstants.EMAIL_CODE + dto.getEmail());
    }

    @Test
    @DisplayName("测试修改密码-用户不存在")
    void testUpdatePassword_UserNotFound() {
        // 准备测试数据
        EmailRegisterDTO dto = new EmailRegisterDTO();
        dto.setEmail("notfound@example.com");
        dto.setCode("123456");

        // 模拟依赖返回
        when(redisService.getCacheObject(RedisConstants.EMAIL_CODE + RedisConstants.EMAIL_CODE + dto.getEmail()))
                .thenReturn("123456");
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(null); // 用户不存在

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userInfoService.updatePassword(dto);
        }, "用户不存在时应抛出BusinessException");

        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(userMapper, never()).updateById(any(User.class));
    }

    // ==================== emailLogin 方法测试 ====================

    @Test
    @DisplayName("测试邮箱登录-成功场景")
    void testEmailLogin_Success() {
        // 准备测试数据
        EmailLoginDTO dto = new EmailLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        User user = User.builder()
                .id(1)
                .username(dto.getEmail())
                .status(UserStatusEnum.normal.code)
                .userInfoId(1)
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();

        UserInfo userInfo = UserInfo.builder()
                .id(1)
                .nickname("测试用户")
                .avatar("avatar.jpg")
                .intro("个人简介")
                .webSite("https://example.com")
                .build();

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 先设置StpUtil的Mock
            // StpUtil.login()是void方法，不需要设置返回值，只需要确保它被调用时不抛出异常
            stpUtilMock.when(() -> StpUtil.login(1L, "PC")).thenAnswer(invocation -> {
                // void方法，什么都不做
                return null;
            });
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("token123");

            // 模拟依赖返回
            when(userMapper.selectNameAndPassword(eq(dto.getEmail()), anyString()))
                    .thenReturn(user);
            when(userInfoMapper.selectById(1))
                    .thenReturn(userInfo);

            // 执行测试
            ResponseResult result = userInfoService.emailLogin(dto);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "返回数据不应为null");
            UserInfoVO userInfoVO = (UserInfoVO) result.getData();
            assertEquals("test@example.com", userInfoVO.getEmail(), "邮箱应匹配");
            assertEquals("测试用户", userInfoVO.getNickname(), "昵称应匹配");
            assertEquals("token123", userInfoVO.getToken(), "Token应匹配");
        }

        verify(userMapper, times(1)).selectNameAndPassword(eq(dto.getEmail()), anyString());
        verify(userInfoMapper, times(1)).selectById(1);
    }

    @Test
    @DisplayName("测试邮箱登录-密码错误")
    void testEmailLogin_WrongPassword() {
        // 准备测试数据
        EmailLoginDTO dto = new EmailLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("wrongPassword");

        // 模拟用户不存在（密码错误）
        when(userMapper.selectNameAndPassword(eq(dto.getEmail()), anyString()))
                .thenReturn(null);

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userInfoService.emailLogin(dto);
        }, "密码错误时应抛出BusinessException");

        verify(userMapper, times(1)).selectNameAndPassword(eq(dto.getEmail()), anyString());
    }

    @Test
    @DisplayName("测试邮箱登录-账号被禁用")
    void testEmailLogin_AccountDisabled() {
        // 准备测试数据
        EmailLoginDTO dto = new EmailLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        User user = User.builder()
                .id(1)
                .status(UserStatusEnum.disable.code)
                .build();

        // 模拟依赖返回
        when(userMapper.selectNameAndPassword(eq(dto.getEmail()), anyString()))
                .thenReturn(user);

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userInfoService.emailLogin(dto);
        }, "账号被禁用时应抛出BusinessException");

        verify(userMapper, times(1)).selectNameAndPassword(eq(dto.getEmail()), anyString());
    }

    // ==================== qqLogin 方法测试 ====================

//    @Test
//    @DisplayName("测试QQ登录-成功场景")
//    void testQqLogin_Success() {
//        // 准备测试数据
//        QQLoginDTO qqLoginDTO = QQLoginDTO.builder()
//                .openId("qq-open-id")
//                .accessToken("qq-access-token")
//                .build();
//
//        UserInfoVO userInfoVO = UserInfoVO.builder()
//                .id(1)
//                .nickname("QQ用户")
//                .avatar("qq-avatar.jpg")
//                .loginType(LoginTypeEnum.QQ.getType())
//                .build();
//
//        // 模拟依赖返回
//        when(socialLoginStrategyContext.executeLoginStrategy(anyString(), eq(LoginTypeEnum.QQ)))
//                .thenReturn(userInfoVO);
//
//        // 执行测试
//        ResponseResult result = userInfoService.qqLogin(qqLoginDTO);
//
//        // 验证结果
//        assertNotNull(result, "返回结果不应为null");
//        assertEquals(200, result.getCode(), "响应码应为200");
//        assertNotNull(result.getData(), "返回数据不应为null");
//        verify(socialLoginStrategyContext, times(1)).executeLoginStrategy(anyString(), eq(LoginTypeEnum.QQ));
//    }

    // ==================== weiboLogin 方法测试 ====================

//    @Test
//    @DisplayName("测试微博登录-成功场景")
//    void testWeiboLogin_Success() {
//        // 准备测试数据
//        String code = "weibo-code-123";
//
//        UserInfoVO userInfoVO = UserInfoVO.builder()
//                .id(1)
//                .nickname("微博用户")
//                .loginType(LoginTypeEnum.WEIBO.getType())
//                .build();
//
//        // 模拟依赖返回
//        when(socialLoginStrategyContext.executeLoginStrategy(eq(code), eq(LoginTypeEnum.WEIBO)))
//                .thenReturn(userInfoVO);
//
//        // 执行测试
//        ResponseResult result = userInfoService.weiboLogin(code);
//
//        // 验证结果
//        assertNotNull(result, "返回结果不应为null");
//        assertEquals(200, result.getCode(), "响应码应为200");
//        verify(socialLoginStrategyContext, times(1)).executeLoginStrategy(eq(code), eq(LoginTypeEnum.WEIBO));
//    }

    // ==================== giteeLogin 方法测试 ====================

//    @Test
//    @DisplayName("测试码云登录-成功场景")
//    void testGiteeLogin_Success() {
//        // 准备测试数据
//        String code = "gitee-code-123";
//
//        UserInfoVO userInfoVO = UserInfoVO.builder()
//                .id(1)
//                .nickname("码云用户")
//                .loginType(LoginTypeEnum.GITEE.getType())
//                .build();
//
//        // 模拟依赖返回
//        when(socialLoginStrategyContext.executeLoginStrategy(eq(code), eq(LoginTypeEnum.GITEE)))
//                .thenReturn(userInfoVO);
//
//        // 执行测试
//        ResponseResult result = userInfoService.giteeLogin(code);
//
//        // 验证结果
//        assertNotNull(result, "返回结果不应为null");
//        assertEquals(200, result.getCode(), "响应码应为200");
//        verify(socialLoginStrategyContext, times(1)).executeLoginStrategy(eq(code), eq(LoginTypeEnum.GITEE));
//    }

    // ==================== sendEmailCode 方法测试 ====================

    @Test
    @DisplayName("测试发送邮箱验证码-成功场景")
    void testSendEmailCode_Success() throws MessagingException {
        // 准备测试数据
        String email = "test@example.com";

        // 模拟EmailService不抛出异常
        doNothing().when(emailService).sendCode(email);

        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(email);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
        assertEquals("验证码已发送，请前往邮箱查看!!", result.getData(), "数据应匹配");
        verify(emailService, times(1)).sendCode(email);
    }

    @Test
    @DisplayName("测试发送邮箱验证码-邮箱格式不正确")
    void testSendEmailCode_InvalidEmailFormat() throws MessagingException {
        // 准备测试数据
        String email = "invalid-email";

        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(email);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertTrue(result.getMessage().contains("邮箱格式不正确"), "错误消息应包含邮箱格式不正确");
        verify(emailService, never()).sendCode(anyString());
    }

    @Test
    @DisplayName("测试发送邮箱验证码-邮箱为null")
    void testSendEmailCode_NullEmail() throws MessagingException {
        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(null);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertTrue(result.getMessage().contains("邮箱格式不正确"), "错误消息应包含邮箱格式不正确");
        verify(emailService, never()).sendCode(anyString());
    }

    @Test
    @DisplayName("测试发送邮箱验证码-MessagingException-认证失败")
    void testSendEmailCode_MessagingException_AuthenticationFailed() throws MessagingException {
        // 准备测试数据
        String email = "test@example.com";

        // 模拟EmailService抛出MessagingException
        doThrow(new MessagingException("Authentication failed")).when(emailService).sendCode(email);

        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(email);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertTrue(result.getMessage().contains("邮箱授权码错误"), "错误消息应包含邮箱授权码错误");
        verify(emailService, times(1)).sendCode(email);
    }

    @Test
    @DisplayName("测试发送邮箱验证码-MessagingException-连接被拒绝")
    void testSendEmailCode_MessagingException_ConnectionRefused() throws MessagingException {
        // 准备测试数据
        String email = "test@example.com";

        // 模拟EmailService抛出MessagingException
        doThrow(new MessagingException("Connection refused")).when(emailService).sendCode(email);

        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(email);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertTrue(result.getMessage().contains("无法连接到邮件服务器"), "错误消息应包含无法连接到邮件服务器");
        verify(emailService, times(1)).sendCode(email);
    }

    @Test
    @DisplayName("测试发送邮箱验证码-MessagingException-超时")
    void testSendEmailCode_MessagingException_Timeout() throws MessagingException {
        // 准备测试数据
        String email = "test@example.com";

        // 模拟EmailService抛出MessagingException
        doThrow(new MessagingException("Connection timeout")).when(emailService).sendCode(email);

        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(email);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertTrue(result.getMessage().contains("邮件服务器连接超时"), "错误消息应包含邮件服务器连接超时");
        verify(emailService, times(1)).sendCode(email);
    }

    @Test
    @DisplayName("测试发送邮箱验证码-MessagingException-SSL错误")
    void testSendEmailCode_MessagingException_SSL() throws MessagingException {
        // 准备测试数据
        String email = "test@example.com";

        // 模拟EmailService抛出MessagingException
        doThrow(new MessagingException("SSL certificate verification failed")).when(emailService).sendCode(email);

        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(email);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertTrue(result.getMessage().contains("SSL连接失败"), "错误消息应包含SSL连接失败");
        verify(emailService, times(1)).sendCode(email);
    }

    @Test
    @DisplayName("测试发送邮箱验证码-通用Exception")
    void testSendEmailCode_GenericException() throws MessagingException {
        // 准备测试数据
        String email = "test@example.com";

        // 模拟EmailService抛出通用Exception
        RuntimeException exception = new RuntimeException("未知异常");
        doThrow(exception).when(emailService).sendCode(email);

        // 执行测试
        ResponseResult result = userInfoService.sendEmailCode(email);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertTrue(result.getMessage().contains("系统异常"), "错误消息应包含系统异常");
        verify(emailService, times(1)).sendCode(email);
    }

    // ==================== publicBindEmail 方法测试 ====================

    @Test
    @DisplayName("测试绑定邮箱-成功场景")
    void testPublicBindEmail_Success() {
        // 准备测试数据
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(1);
        dto.setEmail("newemail@example.com");
        dto.setCode("123456");
        dto.setNickname("测试用户");

        // 模拟依赖返回
        // checkCode方法会在传入的key前再加上RedisConstants.EMAIL_CODE
        // publicBindEmail传入的key是RedisConstants.EMAIL_CODE + email
        // 所以实际查询的key是RedisConstants.EMAIL_CODE + (RedisConstants.EMAIL_CODE + email)
        String actualKey = RedisConstants.EMAIL_CODE + (RedisConstants.EMAIL_CODE + dto.getEmail());
        when(redisService.getCacheObject(actualKey))
                .thenReturn("123456");
        when(userInfoMapper.updateById(any(UserInfo.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = userInfoService.publicBindEmail(dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
        assertEquals("绑定邮箱成功", result.getData(), "数据应匹配");
        verify(redisService, atLeastOnce()).getCacheObject(anyString());
        verify(userInfoMapper, times(1)).updateById(any(UserInfo.class));
        verify(redisService, times(1)).deleteObject(RedisConstants.EMAIL_CODE + dto.getEmail());
    }

    @Test
    @DisplayName("测试绑定邮箱-验证码错误")
    void testPublicBindEmail_InvalidCode() {
        // 准备测试数据
        UserInfoDTO dto = new UserInfoDTO();
        dto.setEmail("test@example.com");
        dto.setCode("wrong-code");

        // 模拟验证码不匹配
        // checkCode方法会在传入的key前加上RedisConstants.EMAIL_CODE
        // publicBindEmail传入的key是RedisConstants.EMAIL_CODE + email
        // 所以实际查询的key是RedisConstants.EMAIL_CODE + (RedisConstants.EMAIL_CODE + email)
        String actualKey = RedisConstants.EMAIL_CODE + (RedisConstants.EMAIL_CODE + dto.getEmail());
        lenient().when(redisService.getCacheObject(actualKey))
                .thenReturn("123456");

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userInfoService.publicBindEmail(dto);
        }, "验证码错误时应抛出BusinessException");

        verify(redisService, atLeastOnce()).getCacheObject(anyString());
        verify(userInfoMapper, never()).updateById(any(UserInfo.class));
    }

    // ==================== publicSelectUserInfo 方法测试 ====================

    @Test
    @DisplayName("测试获取用户信息-成功场景")
    void testPublicSelectUserInfo_Success() {
        // 准备测试数据
        UserInfo userInfo = UserInfo.builder()
                .id(1)
                .nickname("测试用户")
                .email("test@example.com")
                .avatar("avatar.jpg")
                .build();

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 先设置StpUtil的Mock
            stpUtilMock.when(StpUtil::getLoginIdAsInt).thenReturn(1);

            // 模拟依赖返回
            when(userInfoMapper.getByUserId(anyInt()))
                    .thenReturn(userInfo);

            // 执行测试
            ResponseResult result = userInfoService.publicSelectUserInfo();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "返回数据不应为null");
            UserInfo resultUserInfo = (UserInfo) result.getData();
            assertEquals("测试用户", resultUserInfo.getNickname(), "昵称应匹配");
        }

        verify(userInfoMapper, times(1)).getByUserId(anyInt());
    }

    // ==================== publicUpdateUser 方法测试 ====================

    @Test
    @DisplayName("测试修改用户信息-成功场景")
    void testPublicUpdateUser_Success() {
        // 准备测试数据
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(1);
        dto.setNickname("新昵称");
        dto.setIntro("新简介");
        dto.setWebSite("https://newsite.com");
        dto.setAvatar("new-avatar.jpg");

        // 模拟依赖返回
        when(userInfoMapper.updateById(any(UserInfo.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = userInfoService.publicUpdateUser(dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
        assertEquals("修改信息成功", result.getData(), "数据应匹配");
        verify(userInfoMapper, times(1)).updateById(any(UserInfo.class));
    }

    @Test
    @DisplayName("测试修改用户信息-更新失败")
    void testPublicUpdateUser_UpdateFailure() {
        // 准备测试数据
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(1);
        dto.setNickname("新昵称");

        // 模拟依赖返回（更新失败）
        when(userInfoMapper.updateById(any(UserInfo.class)))
                .thenReturn(0);

        // 执行测试
        ResponseResult result = userInfoService.publicUpdateUser(dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(userInfoMapper, times(1)).updateById(any(UserInfo.class));
    }

    // ==================== wxIsLogin 方法测试 ====================

//    @Test
//    @DisplayName("测试判断微信登录-成功场景")
//    void testWxIsLogin_Success() {
//        // 准备测试数据
//        String tempUserId = "temp-user-id-123";
//        UserInfoVO userInfoVO = UserInfoVO.builder()
//                .id(1)
//                .nickname("微信用户")
//                .avatar("wx-avatar.jpg")
//                .build();
//
//        // 使用MockedStatic模拟StpUtil
//        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
//            // 先设置StpUtil的Mock
//            // StpUtil.login()是void方法，不需要设置返回值，只需要确保它被调用时不抛出异常
//            stpUtilMock.when(() -> StpUtil.login(1L, "PC")).thenAnswer(invocation -> {
//                // void方法，什么都不做
//                return null;
//            });
//            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("token123");
//
//            // 模拟依赖返回
//            when(redisService.getCacheObject(RedisConstants.WX_LOGIN_USER + tempUserId))
//                    .thenReturn(userInfoVO);
//
//            // 执行测试
//            ResponseResult result = userInfoService.wxIsLogin(tempUserId);
//
//            // 验证结果
//            assertNotNull(result, "返回结果不应为null");
//            assertEquals(200, result.getCode(), "响应码应为200");
//            assertNotNull(result.getData(), "返回数据不应为null");
//            UserInfoVO resultVO = (UserInfoVO) result.getData();
//            assertEquals("token123", resultVO.getToken(), "Token应匹配");
//        }
//
//        verify(redisService, times(1)).getCacheObject(RedisConstants.WX_LOGIN_USER + tempUserId);
//    }

//    @Test
//    @DisplayName("测试判断微信登录-用户未被授权")
//    void testWxIsLogin_UserNotAuthorized() {
//        // 准备测试数据
//        String tempUserId = "temp-user-id-123";
//
//        // 模拟Redis中没有用户信息
//        when(redisService.getCacheObject(RedisConstants.WX_LOGIN_USER + tempUserId))
//                .thenReturn(null);
//
//        // 执行测试
//        ResponseResult result = userInfoService.wxIsLogin(tempUserId);
//
//        // 验证结果
//        assertNotNull(result, "返回结果不应为null");
//        assertNotEquals(200, result.getCode(), "响应码不应为200");
//        assertEquals("用户未被授权", result.getMessage(), "错误消息应匹配");
//        verify(redisService, times(1)).getCacheObject(RedisConstants.WX_LOGIN_USER + tempUserId);
//    }
}

