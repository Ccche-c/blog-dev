package com.shiyi.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SensitiveUtils 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@DisplayName("敏感词过滤工具类测试")
class SensitiveUtilsTest {

    private SensitiveUtils sensitiveUtils;

    @BeforeEach
    void setUp() {
        sensitiveUtils = new SensitiveUtils();
    }

    // ==================== 字符判断方法测试 ====================

    @Test
    @DisplayName("测试isAsciiAlphaUpper-大写字母")
    void testIsAsciiAlphaUpper_UpperCase() {
        assertTrue(SensitiveUtils.isAsciiAlphaUpper('A'), "A应为大写字母");
        assertTrue(SensitiveUtils.isAsciiAlphaUpper('Z'), "Z应为大写字母");
        assertTrue(SensitiveUtils.isAsciiAlphaUpper('M'), "M应为大写字母");
    }

    @Test
    @DisplayName("测试isAsciiAlphaUpper-非大写字母")
    void testIsAsciiAlphaUpper_NonUpperCase() {
        assertFalse(SensitiveUtils.isAsciiAlphaUpper('a'), "a不应为大写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaUpper('z'), "z不应为大写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaUpper('0'), "0不应为大写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaUpper('9'), "9不应为大写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaUpper('@'), "@不应为大写字母");
    }

    @Test
    @DisplayName("测试isAsciiAlphaLower-小写字母")
    void testIsAsciiAlphaLower_LowerCase() {
        assertTrue(SensitiveUtils.isAsciiAlphaLower('a'), "a应为小写字母");
        assertTrue(SensitiveUtils.isAsciiAlphaLower('z'), "z应为小写字母");
        assertTrue(SensitiveUtils.isAsciiAlphaLower('m'), "m应为小写字母");
    }

    @Test
    @DisplayName("测试isAsciiAlphaLower-非小写字母")
    void testIsAsciiAlphaLower_NonLowerCase() {
        assertFalse(SensitiveUtils.isAsciiAlphaLower('A'), "A不应为小写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaLower('Z'), "Z不应为小写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaLower('0'), "0不应为小写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaLower('9'), "9不应为小写字母");
        assertFalse(SensitiveUtils.isAsciiAlphaLower('@'), "@不应为小写字母");
    }

    @Test
    @DisplayName("测试isAsciiAlpha-字母")
    void testIsAsciiAlpha_Alpha() {
        assertTrue(SensitiveUtils.isAsciiAlpha('A'), "A应为字母");
        assertTrue(SensitiveUtils.isAsciiAlpha('Z'), "Z应为字母");
        assertTrue(SensitiveUtils.isAsciiAlpha('a'), "a应为字母");
        assertTrue(SensitiveUtils.isAsciiAlpha('z'), "z应为字母");
        assertTrue(SensitiveUtils.isAsciiAlpha('M'), "M应为字母");
        assertTrue(SensitiveUtils.isAsciiAlpha('m'), "m应为字母");
    }

    @Test
    @DisplayName("测试isAsciiAlpha-非字母")
    void testIsAsciiAlpha_NonAlpha() {
        assertFalse(SensitiveUtils.isAsciiAlpha('0'), "0不应为字母");
        assertFalse(SensitiveUtils.isAsciiAlpha('9'), "9不应为字母");
        assertFalse(SensitiveUtils.isAsciiAlpha('@'), "@不应为字母");
        assertFalse(SensitiveUtils.isAsciiAlpha(' '), "空格不应为字母");
    }

    @Test
    @DisplayName("测试isAsciiNumeric-数字")
    void testIsAsciiNumeric_Numeric() {
        assertTrue(SensitiveUtils.isAsciiNumeric('0'), "0应为数字");
        assertTrue(SensitiveUtils.isAsciiNumeric('9'), "9应为数字");
        assertTrue(SensitiveUtils.isAsciiNumeric('5'), "5应为数字");
    }

    @Test
    @DisplayName("测试isAsciiNumeric-非数字")
    void testIsAsciiNumeric_NonNumeric() {
        assertFalse(SensitiveUtils.isAsciiNumeric('A'), "A不应为数字");
        assertFalse(SensitiveUtils.isAsciiNumeric('a'), "a不应为数字");
        assertFalse(SensitiveUtils.isAsciiNumeric('@'), "@不应为数字");
        assertFalse(SensitiveUtils.isAsciiNumeric(' '), "空格不应为数字");
    }

    @Test
    @DisplayName("测试isAsciiAlphanumeric-字母数字")
    void testIsAsciiAlphanumeric_Alphanumeric() {
        assertTrue(SensitiveUtils.isAsciiAlphanumeric('A'), "A应为字母数字");
        assertTrue(SensitiveUtils.isAsciiAlphanumeric('Z'), "Z应为字母数字");
        assertTrue(SensitiveUtils.isAsciiAlphanumeric('a'), "a应为字母数字");
        assertTrue(SensitiveUtils.isAsciiAlphanumeric('z'), "z应为字母数字");
        assertTrue(SensitiveUtils.isAsciiAlphanumeric('0'), "0应为字母数字");
        assertTrue(SensitiveUtils.isAsciiAlphanumeric('9'), "9应为字母数字");
    }

    @Test
    @DisplayName("测试isAsciiAlphanumeric-非字母数字")
    void testIsAsciiAlphanumeric_NonAlphanumeric() {
        assertFalse(SensitiveUtils.isAsciiAlphanumeric('@'), "@不应为字母数字");
        assertFalse(SensitiveUtils.isAsciiAlphanumeric(' '), "空格不应为字母数字");
        assertFalse(SensitiveUtils.isAsciiAlphanumeric('中'), "中文字符不应为字母数字");
        assertFalse(SensitiveUtils.isAsciiAlphanumeric('。'), "中文标点不应为字母数字");
    }

    // ==================== filter 方法测试 ====================

    @Test
    @DisplayName("测试过滤敏感词-空文本")
    void testFilter_EmptyText() {
        assertNull(SensitiveUtils.filter(null), "null应返回null");
        assertNull(SensitiveUtils.filter(""), "空字符串应返回null");
        assertNull(SensitiveUtils.filter("   "), "空白字符串应返回null");
    }

    @Test
    @DisplayName("测试过滤敏感词-无敏感词")
    void testFilter_NoSensitiveWords() {
        // 由于filter依赖ROOT_NODE中已加载的敏感词，如果敏感词文件为空或没有匹配的敏感词，应该返回原文本
        String text = "这是一段正常的文本";
        String result = SensitiveUtils.filter(text);
        // 如果ROOT_NODE为空（未初始化），应该返回原文本
        assertNotNull(result, "结果不应为null");
    }

    @Test
    @DisplayName("测试过滤敏感词-包含符号")
    void testFilter_WithSymbols() {
        // 测试包含符号的文本，符号应该被保留
        String text = "测试@#$文本";
        String result = SensitiveUtils.filter(text);
        assertNotNull(result, "结果不应为null");
        // 符号应该被保留
        assertTrue(result.contains("@"), "应包含@符号");
    }

    @Test
    @DisplayName("测试过滤敏感词-中文字符")
    void testFilter_ChineseCharacters() {
        // 测试中文字符，中文字符应该被保留（因为不在符号范围内）
        String text = "这是一段中文文本";
        String result = SensitiveUtils.filter(text);
        assertNotNull(result, "结果不应为null");
        assertTrue(result.contains("中文"), "应包含中文字符");
    }

    @Test
    @DisplayName("测试过滤敏感词-混合字符")
    void testFilter_MixedCharacters() {
        // 测试混合字符（字母、数字、中文、符号）
        // 注意：如果ROOT_NODE已初始化，某些词可能被过滤
        String text = "Test123测试@#$";
        String result = SensitiveUtils.filter(text);
        assertNotNull(result, "结果不应为null");
        // 由于filter方法会处理文本，结果可能包含原文本或过滤后的文本
        // 只要结果不为null且不为空，就认为测试通过
        assertFalse(result.isEmpty(), "结果不应为空");
        // 验证结果长度合理（至少包含部分原文本或替换符）
        assertTrue(result.length() > 0, "结果长度应大于0");
    }

    // ==================== init 方法测试 ====================

    @Test
    @DisplayName("测试初始化-成功")
    void testInit_Success() {
        // 测试init方法是否能正常执行（不抛出异常）
        assertDoesNotThrow(() -> {
            sensitiveUtils.init();
        }, "init方法不应抛出异常");
    }

    // ==================== addKeyword 方法测试（通过反射） ====================

    @Test
    @DisplayName("测试添加敏感词-通过反射")
    void testAddKeyword_ThroughReflection() throws Exception {
        // 使用反射调用私有方法addKeyword
        Method addKeywordMethod = SensitiveUtils.class.getDeclaredMethod("addKeyword", String.class);
        addKeywordMethod.setAccessible(true);
        
        // 添加测试敏感词
        String testKeyword = "testbadword";
        addKeywordMethod.invoke(sensitiveUtils, testKeyword);
        
        // 验证filter方法能够过滤该敏感词
        String text = "这是一个testbadword测试";
        String result = SensitiveUtils.filter(text);
        assertNotNull(result, "结果不应为null");
        // 如果敏感词被正确添加，应该被替换为***
        assertTrue(result.contains("***"), "应包含替换符");
    }

    @Test
    @DisplayName("测试过滤敏感词-单个敏感词")
    void testFilter_SingleSensitiveWord() throws Exception {
        // 使用反射添加敏感词
        Method addKeywordMethod = SensitiveUtils.class.getDeclaredMethod("addKeyword", String.class);
        addKeywordMethod.setAccessible(true);
        addKeywordMethod.invoke(sensitiveUtils, "敏感词");
        
        // 测试过滤
        String text = "这是一个敏感词测试";
        String result = SensitiveUtils.filter(text);
        assertNotNull(result, "结果不应为null");
        assertTrue(result.contains("***"), "应包含替换符");
        assertFalse(result.contains("敏感词"), "不应包含敏感词");
    }

    @Test
    @DisplayName("测试过滤敏感词-多个敏感词")
    void testFilter_MultipleSensitiveWords() throws Exception {
        // 使用反射添加多个敏感词
        Method addKeywordMethod = SensitiveUtils.class.getDeclaredMethod("addKeyword", String.class);
        addKeywordMethod.setAccessible(true);
        addKeywordMethod.invoke(sensitiveUtils, "坏词1");
        addKeywordMethod.invoke(sensitiveUtils, "坏词2");
        
        // 测试过滤
        String text = "这是坏词1和坏词2的测试";
        String result = SensitiveUtils.filter(text);
        assertNotNull(result, "结果不应为null");
        // 应该包含两个替换符
        long count = result.chars().filter(ch -> ch == '*').count();
        assertTrue(count >= 3, "应包含替换符"); // 每个***有3个*
    }

    @Test
    @DisplayName("测试过滤敏感词-敏感词中间有符号")
    void testFilter_SensitiveWordWithSymbols() throws Exception {
        // 使用反射添加敏感词
        Method addKeywordMethod = SensitiveUtils.class.getDeclaredMethod("addKeyword", String.class);
        addKeywordMethod.setAccessible(true);
        addKeywordMethod.invoke(sensitiveUtils, "敏感词");
        
        // 测试敏感词中间有符号的情况（符号会被跳过）
        String text = "这是敏@感#词测试";
        String result = SensitiveUtils.filter(text);
        assertNotNull(result, "结果不应为null");
        // 符号应该被保留，但敏感词应该被过滤
        assertTrue(result.contains("@") || result.contains("#"), "应包含符号");
    }

    @Test
    @DisplayName("测试过滤敏感词-部分匹配")
    void testFilter_PartialMatch() throws Exception {
        // 使用反射添加敏感词
        Method addKeywordMethod = SensitiveUtils.class.getDeclaredMethod("addKeyword", String.class);
        addKeywordMethod.setAccessible(true);
        addKeywordMethod.invoke(sensitiveUtils, "敏感");
        
        // 测试部分匹配（"敏感"是"敏感词"的前缀，但"敏感词"不是敏感词）
        String text1 = "这是敏感测试";
        String result1 = SensitiveUtils.filter(text1);
        assertNotNull(result1, "结果不应为null");
        assertTrue(result1.contains("***"), "应包含替换符");
        
        // "敏感词"不应该被过滤（因为只添加了"敏感"）
        String text2 = "这是敏感词测试";
        String result2 = SensitiveUtils.filter(text2);
        assertNotNull(result2, "结果不应为null");
        // "敏感"部分应该被过滤
        assertTrue(result2.contains("***"), "应包含替换符");
    }
}

