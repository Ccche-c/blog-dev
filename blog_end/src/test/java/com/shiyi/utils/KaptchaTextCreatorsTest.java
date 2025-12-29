package com.shiyi.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KaptchaTextCreators 单元测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("验证码文本生成器测试")
class KaptchaTextCreatorsTest {

    private KaptchaTextCreators kaptchaTextCreators;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        kaptchaTextCreators = new KaptchaTextCreators();
    }

    // ==================== getText 方法测试 ====================

    @Test
    @DisplayName("测试生成验证码文本-基本格式")
    void testGetText_BasicFormat() {
        String result = kaptchaTextCreators.getText();
        
        assertNotNull(result, "返回结果不应为null");
        assertFalse(result.isEmpty(), "返回结果不应为空");
        
        // 验证格式：数字运算符数字=?@结果
        // 例如：5*3=?@15 或 8+2=?@10
        assertTrue(result.contains("=?@"), "应包含分隔符 =?@");
        
        String[] parts = result.split("=\\?@");
        assertEquals(2, parts.length, "应包含两部分：表达式和结果");
        
        String expression = parts[0];
        String resultValue = parts[1];
        
        // 验证表达式包含运算符
        assertTrue(expression.contains("*") || expression.contains("+") 
                || expression.contains("-") || expression.contains("/"), 
                "表达式应包含运算符");
        
        // 验证结果是数字
        assertTrue(resultValue.matches("\\d+"), "结果应为数字");
    }

    @RepeatedTest(50)
    @DisplayName("测试生成验证码文本-多次运行验证格式一致性")
    void testGetText_FormatConsistency() {
        String result = kaptchaTextCreators.getText();
        
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("=?@"), "应包含分隔符 =?@");
        
        // 验证格式：数字运算符数字=?@结果
        Pattern pattern = Pattern.compile("\\d+[+\\-*/]\\d+=\\?@-?\\d+");
        assertTrue(pattern.matcher(result).matches(), 
                "格式应为：数字运算符数字=?@结果，实际值：" + result);
    }

    @RepeatedTest(100)
    @DisplayName("测试生成验证码文本-验证结果正确性")
    void testGetText_ResultCorrectness() {
        String result = kaptchaTextCreators.getText();
        
        String[] parts = result.split("=\\?@");
        assertEquals(2, parts.length, "应包含两部分");
        
        String expression = parts[0];
        int expectedResult = Integer.parseInt(parts[1]);
        
        // 解析表达式并计算结果
        int calculatedResult = calculateExpression(expression);
        
        assertEquals(expectedResult, calculatedResult, 
                "计算结果应匹配，表达式：" + expression);
    }

    @RepeatedTest(50)
    @DisplayName("测试生成验证码文本-验证乘法情况")
    void testGetText_Multiplication() {
        String result = kaptchaTextCreators.getText();
        
        // 如果包含乘法运算符，验证格式
        if (result.contains("*")) {
            assertTrue(result.matches("\\d+\\*\\d+=\\?@\\d+"), 
                    "乘法表达式格式应为：数字*数字=?@结果");
            
            String[] parts = result.split("=\\?@");
            String expression = parts[0];
            int expectedResult = Integer.parseInt(parts[1]);
            
            String[] operands = expression.split("\\*");
            int x = Integer.parseInt(operands[0]);
            int y = Integer.parseInt(operands[1]);
            int calculatedResult = x * y;
            
            assertEquals(expectedResult, calculatedResult, 
                    "乘法结果应正确");
        }
    }

    @RepeatedTest(50)
    @DisplayName("测试生成验证码文本-验证加法情况")
    void testGetText_Addition() {
        String result = kaptchaTextCreators.getText();
        
        // 如果包含加法运算符，验证格式
        if (result.contains("+")) {
            assertTrue(result.matches("\\d+\\+\\d+=\\?@\\d+"), 
                    "加法表达式格式应为：数字+数字=?@结果");
            
            String[] parts = result.split("=\\?@");
            String expression = parts[0];
            int expectedResult = Integer.parseInt(parts[1]);
            
            String[] operands = expression.split("\\+");
            int x = Integer.parseInt(operands[0]);
            int y = Integer.parseInt(operands[1]);
            int calculatedResult = x + y;
            
            assertEquals(expectedResult, calculatedResult, 
                    "加法结果应正确");
        }
    }

    @RepeatedTest(50)
    @DisplayName("测试生成验证码文本-验证减法情况")
    void testGetText_Subtraction() {
        String result = kaptchaTextCreators.getText();
        
        // 如果包含减法运算符，验证格式
        if (result.contains("-")) {
            assertTrue(result.matches("\\d+-\\d+=\\?@\\d+"), 
                    "减法表达式格式应为：数字-数字=?@结果");
            
            String[] parts = result.split("=\\?@");
            String expression = parts[0];
            int expectedResult = Integer.parseInt(parts[1]);
            
            String[] operands = expression.split("-");
            int x = Integer.parseInt(operands[0]);
            int y = Integer.parseInt(operands[1]);
            int calculatedResult = x - y;
            
            assertEquals(expectedResult, calculatedResult, 
                    "减法结果应正确");
        }
    }

    @RepeatedTest(50)
    @DisplayName("测试生成验证码文本-验证除法情况")
    void testGetText_Division() {
        String result = kaptchaTextCreators.getText();
        
        // 如果包含除法运算符，验证格式
        if (result.contains("/")) {
            assertTrue(result.matches("\\d+/\\d+=\\?@\\d+"), 
                    "除法表达式格式应为：数字/数字=?@结果");
            
            String[] parts = result.split("=\\?@");
            String expression = parts[0];
            int expectedResult = Integer.parseInt(parts[1]);
            
            String[] operands = expression.split("/");
            int y = Integer.parseInt(operands[0]);
            int x = Integer.parseInt(operands[1]);
            
            // 验证除法条件：x != 0 且 y % x == 0
            assertNotEquals(0, x, "除数不应为0");
            assertEquals(0, y % x, "应能整除");
            
            int calculatedResult = y / x;
            assertEquals(expectedResult, calculatedResult, 
                    "除法结果应正确");
        }
    }

    @RepeatedTest(20)
    @DisplayName("测试生成验证码文本-验证不同运算符出现")
    void testGetText_DifferentOperators() {
        String result = kaptchaTextCreators.getText();
        
        // 验证至少包含一种运算符
        boolean hasOperator = result.contains("*") || result.contains("+") 
                || result.contains("-") || result.contains("/");
        assertTrue(hasOperator, "应包含至少一种运算符");
        
        // 验证只包含一种运算符
        int operatorCount = 0;
        if (result.contains("*")) operatorCount++;
        if (result.contains("+")) operatorCount++;
        if (result.contains("-")) operatorCount++;
        if (result.contains("/")) operatorCount++;
        assertEquals(1, operatorCount, "应只包含一种运算符");
    }

    @Test
    @DisplayName("测试生成验证码文本-验证CNUMBERS数组使用")
    void testGetText_CNUMBERSUsage() {
        // 运行多次测试，验证使用的数字在CNUMBERS范围内
        for (int i = 0; i < 100; i++) {
            String result = kaptchaTextCreators.getText();
            String[] parts = result.split("=\\?@");
            String expression = parts[0];
            
            // 提取数字（移除运算符）
            String numbersOnly = expression.replaceAll("[+\\-*/]", " ");
            String[] numberStrings = numbersOnly.trim().split("\\s+");
            
            for (String numStr : numberStrings) {
                int num = Integer.parseInt(numStr);
                assertTrue(num >= 0 && num <= 10, 
                        "数字应在0-10范围内，实际值：" + num);
            }
        }
    }

    /**
     * 计算表达式的值
     * 
     * @param expression 表达式字符串，如 "5*3" 或 "8+2"
     * @return 计算结果
     */
    private int calculateExpression(String expression) {
        if (expression.contains("*")) {
            String[] parts = expression.split("\\*");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return x * y;
        } else if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return x + y;
        } else if (expression.contains("-")) {
            String[] parts = expression.split("-");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return x - y;
        } else if (expression.contains("/")) {
            String[] parts = expression.split("/");
            int y = Integer.parseInt(parts[0]);
            int x = Integer.parseInt(parts[1]);
            return y / x;
        }
        throw new IllegalArgumentException("不支持的运算符: " + expression);
    }
}

