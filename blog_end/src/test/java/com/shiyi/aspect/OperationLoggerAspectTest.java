package com.shiyi.aspect;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.shiyi.annotation.OperationLogger;
import com.shiyi.entity.AdminLog;
import com.shiyi.entity.ExceptionLog;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.AdminLogMapper;
import com.shiyi.mapper.ExceptionLogMapper;
import com.shiyi.utils.DateUtil;
import com.shiyi.utils.IpUtil;
import com.shiyi.vo.SystemUserVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

import static com.shiyi.common.Constants.CURRENT_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OperationLoggerAspect 单元测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("操作日志切面测试")
class OperationLoggerAspectTest {

    @Mock
    private AdminLogMapper adminLogMapper;

    @Mock
    private ExceptionLogMapper exceptionLogMapper;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Signature signature;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RequestAttributes requestAttributes;

    @Mock
    private OperationLogger operationLogger;

    @Mock
    private SaSession saSession;

    @InjectMocks
    private OperationLoggerAspect operationLoggerAspect;

    private SystemUserVO testUser;
    private TestTarget testTarget;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new SystemUserVO();
        testUser.setUsername("testUser");
        testUser.setNickname("测试用户");

        testTarget = new TestTarget();
    }

    // ==================== doAround 方法测试 ====================

    @Test
    @DisplayName("测试doAround-非管理员角色")
    void testDoAround_NotAdmin() throws Throwable {
        // 准备测试数据
        lenient().when(operationLogger.value()).thenReturn("测试操作");

        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(() -> StpUtil.hasRole("admin")).thenReturn(false);

            // 执行测试并验证异常
            assertThrows(BusinessException.class, () -> {
                operationLoggerAspect.doAround(proceedingJoinPoint, operationLogger);
            }, "非管理员应抛出BusinessException");

            // 验证方法调用
            stpUtilMock.verify(() -> StpUtil.hasRole("admin"));
            verify(proceedingJoinPoint, never()).proceed();
        }
    }

    @Test
    @DisplayName("测试doAround-成功执行并保存日志")
    void testDoAround_Success_WithSave() throws Throwable {
        // 准备测试数据
        Object result = "success";
        String methodName = "testMethod";
        String[] parameterNames = new String[]{"param1", "param2"};
        Object[] args = new Object[]{"value1", 123};
        String operationName = "测试操作";

        lenient().when(operationLogger.value()).thenReturn(operationName);
        lenient().when(operationLogger.save()).thenReturn(true);
        lenient().when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(proceedingJoinPoint.getArgs()).thenReturn(args);
        lenient().when(proceedingJoinPoint.getTarget()).thenReturn(testTarget);
        lenient().when(methodSignature.getName()).thenReturn(methodName);
        lenient().when(methodSignature.getParameterNames()).thenReturn(parameterNames);
        when(proceedingJoinPoint.proceed()).thenReturn(result);

        // 模拟RequestContextHolder
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<RequestContextHolder> requestContextHolderMock = mockStatic(RequestContextHolder.class);
             MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class);
             MockedStatic<DateUtil> dateUtilMock = mockStatic(DateUtil.class)) {

            stpUtilMock.when(() -> StpUtil.hasRole("admin")).thenReturn(true);
            stpUtilMock.when(StpUtil::getSession).thenReturn(saSession);
            when(saSession.get(CURRENT_USER)).thenReturn(testUser);

            requestContextHolderMock.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST)).thenReturn(request);

            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/api/test");

            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn("192.168.1.1");
            ipUtilMock.when(() -> IpUtil.getIp2region("192.168.1.1")).thenReturn("本地");

            Date startDate = new Date();
            Date endDate = new Date(startDate.getTime() + 100);
            dateUtilMock.when(DateUtil::getNowDate).thenReturn(startDate).thenReturn(endDate);

            // 模拟AspectUtils - AspectUtils是枚举，需要确保JoinPoint的模拟正确
            // 让AspectUtils的方法正常执行，通过正确模拟JoinPoint来确保它能正常工作
            Method testMethod = TestTarget.class.getMethod("testMethod", String.class, Integer.class);
            lenient().when(proceedingJoinPoint.getTarget()).thenReturn(testTarget);
            lenient().when(methodSignature.getMethod()).thenReturn(testMethod);
            lenient().when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class});

            // 执行测试
            Object actualResult = operationLoggerAspect.doAround(proceedingJoinPoint, operationLogger);

            // 验证结果
            assertEquals(result, actualResult, "返回值应匹配");

            // 验证方法调用
            stpUtilMock.verify(() -> StpUtil.hasRole("admin"));
            verify(proceedingJoinPoint).proceed();
            verify(adminLogMapper).insert(any(AdminLog.class));
        }
    }

    @Test
    @DisplayName("测试doAround-不保存日志")
    void testDoAround_Success_WithoutSave() throws Throwable {
        // 准备测试数据
        Object result = "success";
        String operationName = "测试操作";

        lenient().when(operationLogger.value()).thenReturn(operationName);
        lenient().when(operationLogger.save()).thenReturn(false);
        lenient().when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(result);

        // 模拟RequestContextHolder
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<RequestContextHolder> requestContextHolderMock = mockStatic(RequestContextHolder.class);
             MockedStatic<DateUtil> dateUtilMock = mockStatic(DateUtil.class)) {

            stpUtilMock.when(() -> StpUtil.hasRole("admin")).thenReturn(true);

            // 由于save=false，handle方法会提前返回，这些可能不会被调用
            // 但为了测试完整性，我们仍然设置它们
            requestContextHolderMock.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            lenient().when(requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST)).thenReturn(request);

            dateUtilMock.when(DateUtil::getNowDate).thenReturn(new Date());

            // 模拟AspectUtils - AspectUtils是枚举，需要确保JoinPoint的模拟正确
            // 让AspectUtils的方法正常执行，通过正确模拟JoinPoint来确保它能正常工作
            Method testMethod = TestTarget.class.getMethod("testMethod", String.class, Integer.class);
            lenient().when(proceedingJoinPoint.getTarget()).thenReturn(testTarget);
            lenient().when(methodSignature.getMethod()).thenReturn(testMethod);
            lenient().when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class});

            // 执行测试
            Object actualResult = operationLoggerAspect.doAround(proceedingJoinPoint, operationLogger);

            // 验证结果
            assertEquals(result, actualResult, "返回值应匹配");

            // 验证不保存日志
            verify(adminLogMapper, never()).insert(any(AdminLog.class));
        }
    }

    @Test
    @DisplayName("测试doAround-日志记录异常")
    void testDoAround_LogException() throws Throwable {
        // 准备测试数据
        Object result = "success";
        String operationName = "测试操作";

        lenient().when(operationLogger.value()).thenReturn(operationName);
        lenient().when(operationLogger.save()).thenReturn(true);
        lenient().when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(result);

        // 模拟RequestContextHolder
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<RequestContextHolder> requestContextHolderMock = mockStatic(RequestContextHolder.class);
             MockedStatic<DateUtil> dateUtilMock = mockStatic(DateUtil.class)) {

            stpUtilMock.when(() -> StpUtil.hasRole("admin")).thenReturn(true);

            // 模拟RequestContextHolder返回requestAttributes，但resolveReference返回null
            // 这样会抛出NullPointerException（Exception的子类），而不是AssertionError（Error的子类）
            requestContextHolderMock.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST)).thenReturn(null);

            dateUtilMock.when(DateUtil::getNowDate).thenReturn(new Date());

            // 模拟AspectUtils - AspectUtils是枚举，需要确保JoinPoint的模拟正确
            // 让AspectUtils的方法正常执行，通过正确模拟JoinPoint来确保它能正常工作
            Method testMethod = TestTarget.class.getMethod("testMethod", String.class, Integer.class);
            lenient().when(proceedingJoinPoint.getTarget()).thenReturn(testTarget);
            lenient().when(methodSignature.getMethod()).thenReturn(testMethod);
            lenient().when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class});

            // 执行测试 - 应该捕获异常并继续执行
            Object actualResult = operationLoggerAspect.doAround(proceedingJoinPoint, operationLogger);

            // 验证结果 - 即使日志记录失败，业务逻辑仍应正常返回
            assertEquals(result, actualResult, "返回值应匹配");
        }
    }

    // ==================== doAfterThrowing 方法测试 ====================

    @Test
    @DisplayName("测试doAfterThrowing-异常日志记录")
    void testDoAfterThrowing_Success() throws Exception {
        // 准备测试数据
        String methodName = "testMethod";
        String[] parameterNames = new String[]{"param1"};
        Object[] args = new Object[]{"value1"};
        String operationName = "测试操作";
        Throwable exception = new RuntimeException("测试异常");

        when(operationLogger.value()).thenReturn(operationName);
        // doAfterThrowing接收JoinPoint，但代码中会转换为ProceedingJoinPoint
        // 所以我们需要让joinPoint本身就是一个ProceedingJoinPoint
        // 直接使用proceedingJoinPoint来模拟joinPoint的行为
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        // getTarget()在getParamsJson中不会被调用，所以使用lenient()
        lenient().when(proceedingJoinPoint.getTarget()).thenReturn(testTarget);
        when(methodSignature.getName()).thenReturn(methodName);
        when(methodSignature.getParameterNames()).thenReturn(parameterNames);

        // 模拟RequestContextHolder和工具类
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<RequestContextHolder> requestContextHolderMock = mockStatic(RequestContextHolder.class);
             MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class);
             MockedStatic<DateUtil> dateUtilMock = mockStatic(DateUtil.class)) {

            stpUtilMock.when(StpUtil::getSession).thenReturn(saSession);
            when(saSession.get(CURRENT_USER)).thenReturn(testUser);

            requestContextHolderMock.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST)).thenReturn(request);

            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn("192.168.1.1");
            ipUtilMock.when(() -> IpUtil.getIp2region("192.168.1.1")).thenReturn("本地");

            dateUtilMock.when(DateUtil::getNowDate).thenReturn(new Date());

            // 注意：AspectUtils是枚举，我们不能直接替换INSTANCE
            // AspectUtils.INSTANCE.parseParams会正常执行，使用实际的实现
            // 这里我们只需要确保joinPoint的模拟正确即可

            // 执行测试 - 使用proceedingJoinPoint，因为代码中会将JoinPoint转换为ProceedingJoinPoint
            operationLoggerAspect.doAfterThrowing(proceedingJoinPoint, operationLogger, exception);

            // 验证方法调用
            verify(exceptionLogMapper).insert(any(ExceptionLog.class));
        }
    }

    // ==================== 辅助测试类 ====================

    /**
     * 测试目标类
     */
    static class TestTarget {
        @OperationLogger("测试方法")
        public String testMethod(String param1, Integer param2) {
            return "result";
        }
    }
}

