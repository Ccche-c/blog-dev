package com.shiyi.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AspectUtils 单元测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AOP工具类测试")
class AspectUtilsTest {

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private MethodSignature methodSignature;

    private AspectUtils aspectUtils = AspectUtils.INSTANCE;

    // ==================== getKey 方法测试 ====================

    @Test
    @DisplayName("测试获取键-带前缀")
    void testGetKey_WithPrefix() {
        // 准备测试数据
        String prefix = "test_prefix_";
        TestTarget target = new TestTarget();
        
        when(joinPoint.getTarget()).thenReturn(target);
        
        // 执行测试
        String result = aspectUtils.getKey(joinPoint, prefix);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.startsWith(prefix), "应以前缀开头");
        assertTrue(result.contains("TestTarget"), "应包含类名");
        assertTrue(result.contains("_"), "类名中的点应被替换为下划线");
        
        verify(joinPoint, times(1)).getTarget();
    }

    @Test
    @DisplayName("测试获取键-无前缀")
    void testGetKey_NoPrefix() {
        // 准备测试数据
        String prefix = "";
        TestTarget target = new TestTarget();
        
        when(joinPoint.getTarget()).thenReturn(target);
        
        // 执行测试
        String result = aspectUtils.getKey(joinPoint, prefix);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("TestTarget"), "应包含类名");
        assertTrue(result.contains("_"), "类名中的点应被替换为下划线");
        
        verify(joinPoint, times(1)).getTarget();
    }

    @Test
    @DisplayName("测试获取键-null前缀")
    void testGetKey_NullPrefix() {
        // 准备测试数据
        String prefix = null;
        TestTarget target = new TestTarget();
        
        when(joinPoint.getTarget()).thenReturn(target);
        
        // 执行测试
        String result = aspectUtils.getKey(joinPoint, prefix);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("TestTarget"), "应包含类名");
        
        verify(joinPoint, times(1)).getTarget();
    }

    // ==================== getClassName 方法测试 ====================

    @Test
    @DisplayName("测试获取类名-标准类名")
    void testGetClassName_StandardClassName() {
        // 准备测试数据
        TestTarget target = new TestTarget();
        
        when(joinPoint.getTarget()).thenReturn(target);
        
        // 执行测试
        String result = aspectUtils.getClassName(joinPoint);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("TestTarget"), "应包含类名");
        assertTrue(result.contains("_"), "类名中的点应被替换为下划线");
        assertFalse(result.contains("."), "不应包含点号");
        
        verify(joinPoint, times(1)).getTarget();
    }

    @Test
    @DisplayName("测试获取类名-内部类")
    void testGetClassName_InnerClass() {
        // 准备测试数据
        InnerTestTarget target = new InnerTestTarget();
        
        when(joinPoint.getTarget()).thenReturn(target);
        
        // 执行测试
        String result = aspectUtils.getClassName(joinPoint);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("InnerTestTarget"), "应包含类名");
        assertTrue(result.contains("_"), "类名中的点应被替换为下划线");
        assertFalse(result.contains("."), "不应包含点号");
        
        verify(joinPoint, times(1)).getTarget();
    }

    // ==================== getMethod 方法测试 ====================

    @Test
    @DisplayName("测试获取方法-成功")
    void testGetMethod_Success() throws NoSuchMethodException {
        // 准备测试数据
        TestTarget target = new TestTarget();
        String methodName = "testMethod";
        Class<?>[] parameterTypes = new Class[]{String.class, Integer.class};
        
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn(methodName);
        when(methodSignature.getParameterTypes()).thenReturn(parameterTypes);
        when(joinPoint.getTarget()).thenReturn(target);
        
        // 执行测试
        Method result = aspectUtils.getMethod(joinPoint);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(methodName, result.getName(), "方法名应匹配");
        assertEquals(parameterTypes.length, result.getParameterTypes().length, "参数类型数量应匹配");
        
        verify(joinPoint, times(1)).getSignature();
        verify(joinPoint, times(1)).getTarget();
    }

    @Test
    @DisplayName("测试获取方法-方法不存在")
    void testGetMethod_NoSuchMethod() throws NoSuchMethodException {
        // 准备测试数据
        TestTarget target = new TestTarget();
        String methodName = "nonExistentMethod";
        Class<?>[] parameterTypes = new Class[]{String.class};
        
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn(methodName);
        when(methodSignature.getParameterTypes()).thenReturn(parameterTypes);
        when(joinPoint.getTarget()).thenReturn(target);
        
        // 执行测试并验证异常
        assertThrows(NoSuchMethodException.class, () -> aspectUtils.getMethod(joinPoint), 
                "应抛出NoSuchMethodException");
        
        verify(joinPoint, times(1)).getSignature();
        verify(joinPoint, times(1)).getTarget();
    }

    // ==================== parseParams 方法测试 ====================

    @Test
    @DisplayName("测试解析参数-包含占位符")
    void testParseParams_WithPlaceholders() {
        // 准备测试数据
        Object[] params = new Object[]{"param1", 123, "param3"};
        String bussinessName = "test_{1}_{2}_{3}";
        
        // 执行测试
        String result = aspectUtils.parseParams(params, bussinessName);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("\"param1\""), "应包含第一个参数的JSON表示");
        assertTrue(result.contains("123"), "应包含第二个参数的JSON表示");
        assertTrue(result.contains("\"param3\""), "应包含第三个参数的JSON表示");
        assertFalse(result.contains("{1}"), "占位符应被替换");
        assertFalse(result.contains("{2}"), "占位符应被替换");
        assertFalse(result.contains("{3}"), "占位符应被替换");
    }

    @Test
    @DisplayName("测试解析参数-多个相同索引占位符")
    void testParseParams_MultipleSameIndex() {
        // 准备测试数据
        Object[] params = new Object[]{"param1", 123};
        String bussinessName = "test_{1}_{1}_{2}";
        
        // 执行测试
        String result = aspectUtils.parseParams(params, bussinessName);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        // 统计 "param1" 的出现次数（应该出现2次，因为 {1} 出现2次）
        long count = result.split("\"param1\"").length - 1;
        assertTrue(count >= 2, "第一个参数应出现至少2次");
    }

    @Test
    @DisplayName("测试解析参数-无占位符")
    void testParseParams_NoPlaceholders() {
        // 准备测试数据
        Object[] params = new Object[]{"param1", 123};
        String bussinessName = "test_no_placeholders";
        
        // 执行测试
        String result = aspectUtils.parseParams(params, bussinessName);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(bussinessName, result, "无占位符时应返回原字符串");
    }

    @Test
    @DisplayName("测试解析参数-只有左括号")
    void testParseParams_OnlyLeftBrace() {
        // 准备测试数据
        Object[] params = new Object[]{"param1"};
        String bussinessName = "test_{1";
        
        // 执行测试
        String result = aspectUtils.parseParams(params, bussinessName);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(bussinessName, result, "只有左括号时应返回原字符串");
    }

    @Test
    @DisplayName("测试解析参数-只有右括号")
    void testParseParams_OnlyRightBrace() {
        // 准备测试数据
        Object[] params = new Object[]{"param1"};
        String bussinessName = "test_1}";
        
        // 执行测试
        String result = aspectUtils.parseParams(params, bussinessName);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(bussinessName, result, "只有右括号时应返回原字符串");
    }

    @Test
    @DisplayName("测试解析参数-复杂对象")
    void testParseParams_ComplexObject() {
        // 准备测试数据
        TestObject testObj = new TestObject("name", 100);
        Object[] params = new Object[]{testObj};
        String bussinessName = "test_{1}";
        
        // 执行测试
        String result = aspectUtils.parseParams(params, bussinessName);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("name"), "应包含对象的字段");
        assertTrue(result.contains("100"), "应包含对象的字段");
        assertFalse(result.contains("{1}"), "占位符应被替换");
    }

    // ==================== match 方法测试 ====================

    @Test
    @DisplayName("测试正则匹配-成功")
    void testMatch_Success() {
        // 准备测试数据
        String str = "test_{1}_{2}_{3}";
        String regex = "(?<=\\{)(\\d+)";
        
        // 执行测试
        List<String> result = AspectUtils.match(str, regex);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(3, result.size(), "应匹配3个数字");
        assertTrue(result.contains("1"), "应包含1");
        assertTrue(result.contains("2"), "应包含2");
        assertTrue(result.contains("3"), "应包含3");
    }

    @Test
    @DisplayName("测试正则匹配-无匹配")
    void testMatch_NoMatch() {
        // 准备测试数据
        String str = "test_no_numbers";
        String regex = "(?<=\\{)(\\d+)";
        
        // 执行测试
        List<String> result = AspectUtils.match(str, regex);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.isEmpty(), "应返回空列表");
    }

    @Test
    @DisplayName("测试正则匹配-null字符串")
    void testMatch_NullString() {
        // 准备测试数据
        String str = null;
        String regex = "(?<=\\{)(\\d+)";
        
        // 执行测试
        List<String> result = AspectUtils.match(str, regex);
        
        // 验证结果
        assertNull(result, "null字符串应返回null");
    }

    @Test
    @DisplayName("测试正则匹配-多个匹配")
    void testMatch_MultipleMatches() {
        // 准备测试数据
        String str = "test_{1}_abc_{2}_xyz_{3}_end";
        String regex = "(?<=\\{)(\\d+)";
        
        // 执行测试
        List<String> result = AspectUtils.match(str, regex);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(3, result.size(), "应匹配3个数字");
    }

    @Test
    @DisplayName("测试正则匹配-复杂正则表达式")
    void testMatch_ComplexRegex() {
        // 准备测试数据
        String str = "email: test@example.com, phone: 123-456-7890";
        String regex = "\\b\\w+@\\w+\\.\\w+\\b"; // 匹配邮箱
        
        // 执行测试
        List<String> result = AspectUtils.match(str, regex);
        
        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "应匹配1个邮箱");
        assertEquals("test@example.com", result.get(0), "应匹配正确的邮箱");
    }

    // ==================== 辅助类 ====================

    /**
     * 测试目标类
     */
    static class TestTarget {
        public void testMethod(String param1, Integer param2) {
            // 测试方法
        }
    }

    /**
     * 内部测试目标类
     */
    static class InnerTestTarget {
        // 内部类
    }

    /**
     * 测试对象类
     */
    static class TestObject {
        private String name;
        private int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}

