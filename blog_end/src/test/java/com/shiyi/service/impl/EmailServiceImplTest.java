package com.shiyi.service.impl;

import com.shiyi.common.RedisConstants;
import com.shiyi.entity.FriendLink;
import com.shiyi.entity.SystemConfig;
import com.shiyi.service.RedisService;
import com.shiyi.service.SystemConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.shiyi.enums.FriendLinkEnum.DOWN;
import static com.shiyi.enums.FriendLinkEnum.UP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EmailServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("邮件服务实现类测试")
class EmailServiceImplTest {

    @Mock
    private RedisService redisService;

    @Mock
    private SystemConfigService systemConfigService;

    @Mock
    private JavaMailSenderImpl javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private SystemConfig systemConfig;

    @BeforeEach
    void setUp() throws Exception {
        // 准备系统配置
        systemConfig = new SystemConfig();
        systemConfig.setEmailHost("smtp.qq.com");
        systemConfig.setEmailUsername("test@qq.com");
        systemConfig.setEmailPassword("test_password");
        systemConfig.setEmailPort(587);

        // 使用反射设置 javaMailSender
        ReflectionTestUtils.setField(emailService, "javaMailSender", javaMailSender);

        // Mock SystemConfigService
        when(systemConfigService.getCustomizeOne()).thenReturn(systemConfig);

        // Mock JavaMailSenderImpl 方法（使用 lenient 避免不必要的 stubbing 警告）
        lenient().when(javaMailSender.getUsername()).thenReturn("test@qq.com");
        lenient().when(javaMailSender.getHost()).thenReturn("smtp.qq.com");
        lenient().when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // 初始化服务（调用 @PostConstruct 方法）
        Method initMethod = EmailServiceImpl.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(emailService);
    }

    // ==================== sendCode 方法测试 ====================

    @Test
    @DisplayName("测试发送邮箱验证码-成功")
    void testSendCode_Success() throws MessagingException {
        // 准备测试数据
        String email = "test@example.com";

        // Mock
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        doNothing().when(redisService).setCacheObject(anyString(), anyString());
        when(redisService.expire(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        // 执行测试
        emailService.sendCode(email);

        // 验证调用
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        verify(redisService, times(1)).setCacheObject(
                eq(RedisConstants.EMAIL_CODE + email), anyString());
        verify(redisService, times(1)).expire(
                eq(RedisConstants.EMAIL_CODE + email), 
                eq((long) RedisConstants.CAPTCHA_EXPIRATION), 
                eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("测试发送邮箱验证码-邮箱地址为空")
    void testSendCode_EmailEmpty() {
        // 执行测试并验证异常
        assertThrows(MessagingException.class, () -> emailService.sendCode(""),
                "应抛出邮箱地址为空异常");
    }

    @Test
    @DisplayName("测试发送邮箱验证码-邮箱格式不正确")
    void testSendCode_InvalidEmailFormat() {
        // 执行测试并验证异常
        assertThrows(MessagingException.class, () -> emailService.sendCode("invalid-email"),
                "应抛出邮箱格式不正确异常");
    }

    @Test
    @DisplayName("测试发送邮箱验证码-邮件服务未初始化")
    void testSendCode_ServiceNotInitialized() throws Exception {
        // 创建一个新的服务实例，不初始化
        EmailServiceImpl newService = new EmailServiceImpl(redisService, systemConfigService);
        ReflectionTestUtils.setField(newService, "javaMailSender", new JavaMailSenderImpl());

        // 执行测试并验证异常
        assertThrows(MessagingException.class, () -> newService.sendCode("test@example.com"),
                "应抛出邮件服务未配置异常");
    }

    // ==================== friendPassSendEmail 方法测试 ====================

    @Test
    @DisplayName("测试友链通过发送邮件-成功")
    void testFriendPassSendEmail_Success() throws Exception {
        // 准备测试数据
        String email = "friend@example.com";

        // Mock
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // 执行测试
        emailService.friendPassSendEmail(email);

        // 验证调用
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("测试友链通过发送邮件-异常处理")
    void testFriendPassSendEmail_Exception() throws Exception {
        // 准备测试数据
        String email = "friend@example.com";

        // Mock 抛出异常（使用 RuntimeException，因为方法内部捕获所有异常）
        doThrow(new RuntimeException("发送失败")).when(javaMailSender).send(any(MimeMessage.class));

        // 执行测试（不应抛出异常，内部已捕获）
        assertDoesNotThrow(() -> emailService.friendPassSendEmail(email));
    }

    // ==================== friendFailedSendEmail 方法测试 ====================

    @Test
    @DisplayName("测试友链未通过发送邮件-成功")
    void testFriendFailedSendEmail_Success() throws Exception {
        // 准备测试数据
        String email = "friend@example.com";
        String reason = "网站内容不符合要求";

        // Mock
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // 执行测试
        emailService.friendFailedSendEmail(email, reason);

        // 验证调用
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("测试友链未通过发送邮件-异常处理")
    void testFriendFailedSendEmail_Exception() throws Exception {
        // 准备测试数据
        String email = "friend@example.com";
        String reason = "网站内容不符合要求";

        // Mock 抛出异常（使用 RuntimeException，因为方法内部捕获所有异常）
        doThrow(new RuntimeException("发送失败")).when(javaMailSender).send(any(MimeMessage.class));

        // 执行测试（不应抛出异常，内部已捕获）
        assertDoesNotThrow(() -> emailService.friendFailedSendEmail(email, reason));
    }

    // ==================== sendFriendEmail 方法测试 ====================

    @Test
    @DisplayName("测试发送友链审核邮件-通过")
    void testSendFriendEmail_Pass() throws Exception {
        // 准备测试数据
        FriendLink friendLink = new FriendLink();
        friendLink.setEmail("friend@example.com");
        friendLink.setStatus(UP.getCode());

        // Mock
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // 执行测试
        emailService.sendFriendEmail(friendLink);

        // 验证调用
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("测试发送友链审核邮件-未通过")
    void testSendFriendEmail_Failed() throws Exception {
        // 准备测试数据
        FriendLink friendLink = new FriendLink();
        friendLink.setEmail("friend@example.com");
        friendLink.setStatus(DOWN.getCode());
        friendLink.setReason("网站内容不符合要求");

        // Mock
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // 执行测试
        emailService.sendFriendEmail(friendLink);

        // 验证调用
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("测试发送友链审核邮件-状态不存在")
    void testSendFriendEmail_StatusNotFound() {
        // 准备测试数据
        FriendLink friendLink = new FriendLink();
        friendLink.setEmail("friend@example.com");
        friendLink.setStatus(999); // 不存在的状态

        // 执行测试（不应抛出异常）
        assertDoesNotThrow(() -> emailService.sendFriendEmail(friendLink));

        // 验证未调用发送方法
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    // ==================== emailNoticeMe 方法测试 ====================

    @Test
    @DisplayName("测试通知我-成功")
    void testEmailNoticeMe_Success() {
        // 准备测试数据
        String subject = "测试主题";
        String content = "测试内容";

        // Mock
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // 执行测试
        emailService.emailNoticeMe(subject, content);

        // 验证调用
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ==================== init 方法测试 ====================

    @Test
    @DisplayName("测试初始化-成功")
    void testInit_Success() throws Exception {
        // 准备测试数据
        SystemConfig config = new SystemConfig();
        config.setEmailHost("smtp.qq.com");
        config.setEmailUsername("test@qq.com");
        config.setEmailPassword("test_password");
        config.setEmailPort(587);

        // 创建新的服务实例
        EmailServiceImpl newService = new EmailServiceImpl(redisService, systemConfigService);
        when(systemConfigService.getCustomizeOne()).thenReturn(config);

        // 执行初始化
        Method initMethod = EmailServiceImpl.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(newService);

        // 验证配置已设置
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) ReflectionTestUtils.getField(newService, "javaMailSender");
        assertNotNull(mailSender, "邮件发送器不应为null");
        assertEquals("smtp.qq.com", mailSender.getHost(), "主机应匹配");
        assertEquals("test@qq.com", mailSender.getUsername(), "用户名应匹配");
    }

    @Test
    @DisplayName("测试初始化-系统配置为空")
    void testInit_ConfigNull() throws Exception {
        // 创建新的服务实例
        EmailServiceImpl newService = new EmailServiceImpl(redisService, systemConfigService);
        when(systemConfigService.getCustomizeOne()).thenReturn(null);

        // 执行初始化（不应抛出异常）
        Method initMethod = EmailServiceImpl.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        assertDoesNotThrow(() -> initMethod.invoke(newService));
    }

    @Test
    @DisplayName("测试初始化-端口465（SSL）")
    void testInit_Port465() throws Exception {
        // 准备测试数据
        SystemConfig config = new SystemConfig();
        config.setEmailHost("smtp.qq.com");
        config.setEmailUsername("test@qq.com");
        config.setEmailPassword("test_password");
        config.setEmailPort(465);

        // 创建新的服务实例
        EmailServiceImpl newService = new EmailServiceImpl(redisService, systemConfigService);
        when(systemConfigService.getCustomizeOne()).thenReturn(config);

        // 执行初始化
        Method initMethod = EmailServiceImpl.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(newService);

        // 验证配置已设置
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) ReflectionTestUtils.getField(newService, "javaMailSender");
        assertNotNull(mailSender, "邮件发送器不应为null");
        assertEquals(465, mailSender.getPort(), "端口应匹配");
    }

    @Test
    @DisplayName("测试初始化-邮箱格式不正确")
    void testInit_InvalidEmailFormat() throws Exception {
        // 准备测试数据
        SystemConfig config = new SystemConfig();
        config.setEmailHost("smtp.qq.com");
        config.setEmailUsername("invalid-email"); // 无效邮箱格式
        config.setEmailPassword("test_password");
        config.setEmailPort(587);

        // 创建新的服务实例
        EmailServiceImpl newService = new EmailServiceImpl(redisService, systemConfigService);
        when(systemConfigService.getCustomizeOne()).thenReturn(config);

        // 执行初始化（不应抛出异常，但会记录错误日志）
        Method initMethod = EmailServiceImpl.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        assertDoesNotThrow(() -> initMethod.invoke(newService));
    }

    @Test
    @DisplayName("测试初始化-邮箱主机为空")
    void testInit_EmailHostEmpty() throws Exception {
        // 准备测试数据
        SystemConfig config = new SystemConfig();
        config.setEmailHost(""); // 空主机
        config.setEmailUsername("test@qq.com");
        config.setEmailPassword("test_password");
        config.setEmailPort(587);

        // 创建新的服务实例
        EmailServiceImpl newService = new EmailServiceImpl(redisService, systemConfigService);
        when(systemConfigService.getCustomizeOne()).thenReturn(config);

        // 执行初始化（不应抛出异常，但会记录错误日志）
        Method initMethod = EmailServiceImpl.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        assertDoesNotThrow(() -> initMethod.invoke(newService));
    }
}

