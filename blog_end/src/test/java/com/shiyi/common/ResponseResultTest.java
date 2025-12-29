package com.shiyi.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.shiyi.common.ResultCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResponseResult 单元测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("统一返回结果类测试")
class ResponseResultTest {

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("测试构造函数")
    void testConstructor() {
        Integer code = 200;
        String message = "测试消息";
        Object data = "测试数据";
        
        ResponseResult result = new ResponseResult(code, message, data);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(code, result.getCode(), "响应码应匹配");
        assertEquals(message, result.getMessage(), "消息应匹配");
        assertEquals(data, result.getData(), "数据应匹配");
        assertNotNull(result.getExtra(), "extra不应为null");
        assertTrue(result.getExtra().isEmpty(), "extra应为空Map");
    }

    // ==================== success 方法测试 ====================

    @Test
    @DisplayName("测试success()-无参数")
    void testSuccess_NoParams() {
        ResponseResult result = ResponseResult.success();
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(SUCCESS.getCode(), result.getCode(), "响应码应为SUCCESS");
        assertEquals(SUCCESS.getDesc(), result.getMessage(), "消息应为SUCCESS描述");
        assertNull(result.getData(), "数据应为null");
    }

    @Test
    @DisplayName("测试success(Object data)")
    void testSuccess_WithData() {
        Object data = "测试数据";
        
        ResponseResult result = ResponseResult.success(data);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(SUCCESS.getCode(), result.getCode(), "响应码应为SUCCESS");
        assertEquals(SUCCESS.getDesc(), result.getMessage(), "消息应为SUCCESS描述");
        assertEquals(data, result.getData(), "数据应匹配");
    }

    @Test
    @DisplayName("测试success(String message, Object data)")
    void testSuccess_WithMessageAndData() {
        String message = "操作成功";
        Object data = "测试数据";
        
        ResponseResult result = ResponseResult.success(message, data);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(SUCCESS.getCode(), result.getCode(), "响应码应为SUCCESS");
        assertEquals(message, result.getMessage(), "消息应匹配");
        assertEquals(data, result.getData(), "数据应匹配");
    }

    @Test
    @DisplayName("测试success(Integer code, String message, Object data)")
    void testSuccess_WithCodeMessageAndData() {
        Integer code = 201;
        String message = "创建成功";
        Object data = "测试数据";
        
        ResponseResult result = ResponseResult.success(code, message, data);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(code, result.getCode(), "响应码应匹配");
        assertEquals(message, result.getMessage(), "消息应匹配");
        assertEquals(data, result.getData(), "数据应匹配");
    }

    @Test
    @DisplayName("测试success(Integer code, String message)")
    void testSuccess_WithCodeAndMessage() {
        Integer code = 201;
        String message = "创建成功";
        
        ResponseResult result = ResponseResult.success(code, message);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(code, result.getCode(), "响应码应匹配");
        assertEquals(message, result.getMessage(), "消息应匹配");
        assertNull(result.getData(), "数据应为null");
    }

    @Test
    @DisplayName("测试success(Object data)-复杂对象")
    void testSuccess_WithComplexObject() {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("key1", "value1");
        data.put("key2", 123);
        
        ResponseResult result = ResponseResult.success(data);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(SUCCESS.getCode(), result.getCode(), "响应码应为SUCCESS");
        assertEquals(data, result.getData(), "数据应匹配");
    }

    @Test
    @DisplayName("测试success(Object data)-null数据")
    void testSuccess_WithNullData() {
        ResponseResult result = ResponseResult.success((Object) null);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(SUCCESS.getCode(), result.getCode(), "响应码应为SUCCESS");
        assertNull(result.getData(), "数据应为null");
    }

    // ==================== error 方法测试 ====================

    @Test
    @DisplayName("测试error()-无参数")
    void testError_NoParams() {
        ResponseResult result = ResponseResult.error();
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(FAILURE.getCode(), result.getCode(), "响应码应为FAILURE");
        assertEquals(ERROR.getDesc(), result.getMessage(), "消息应为ERROR描述");
        assertNull(result.getData(), "数据应为null");
    }

    @Test
    @DisplayName("测试error(String message)")
    void testError_WithMessage() {
        String message = "操作失败";
        
        ResponseResult result = ResponseResult.error(message);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(FAILURE.getCode(), result.getCode(), "响应码应为FAILURE");
        assertEquals(message, result.getMessage(), "消息应匹配");
        assertNull(result.getData(), "数据应为null");
    }

    @Test
    @DisplayName("测试error(Integer code, String message)")
    void testError_WithCodeAndMessage() {
        Integer code = 500;
        String message = "服务器错误";
        
        ResponseResult result = ResponseResult.error(code, message);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(code, result.getCode(), "响应码应匹配");
        assertEquals(message, result.getMessage(), "消息应匹配");
        assertNull(result.getData(), "数据应为null");
    }

    @Test
    @DisplayName("测试error(String message)-空字符串")
    void testError_WithEmptyMessage() {
        String message = "";
        
        ResponseResult result = ResponseResult.error(message);
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(FAILURE.getCode(), result.getCode(), "响应码应为FAILURE");
        assertEquals(message, result.getMessage(), "消息应匹配");
    }

    // ==================== putExtra 方法测试 ====================

    @Test
    @DisplayName("测试putExtra-添加单个键值对")
    void testPutExtra_SingleKeyValue() {
        ResponseResult result = ResponseResult.success();
        String key = "testKey";
        Object value = "testValue";
        
        ResponseResult returned = result.putExtra(key, value);
        
        assertNotNull(returned, "返回结果不应为null");
        assertEquals(result, returned, "应返回自身");
        assertTrue(result.getExtra().containsKey(key), "extra应包含键");
        assertEquals(value, result.getExtra().get(key), "extra值应匹配");
    }

    @Test
    @DisplayName("测试putExtra-添加多个键值对")
    void testPutExtra_MultipleKeyValues() {
        ResponseResult result = ResponseResult.success();
        
        result.putExtra("key1", "value1");
        result.putExtra("key2", 123);
        result.putExtra("key3", true);
        
        assertEquals(3, result.getExtra().size(), "extra应包含3个键值对");
        assertEquals("value1", result.getExtra().get("key1"), "key1值应匹配");
        assertEquals(123, result.getExtra().get("key2"), "key2值应匹配");
        assertEquals(true, result.getExtra().get("key3"), "key3值应匹配");
    }

    @Test
    @DisplayName("测试putExtra-覆盖已存在的键")
    void testPutExtra_OverrideExistingKey() {
        ResponseResult result = ResponseResult.success();
        String key = "testKey";
        
        result.putExtra(key, "value1");
        result.putExtra(key, "value2");
        
        assertEquals(1, result.getExtra().size(), "extra应只包含1个键值对");
        assertEquals("value2", result.getExtra().get(key), "值应被覆盖");
    }

    @Test
    @DisplayName("测试putExtra-链式调用")
    void testPutExtra_Chaining() {
        ResponseResult result = ResponseResult.success()
                .putExtra("key1", "value1")
                .putExtra("key2", "value2");
        
        assertEquals(2, result.getExtra().size(), "extra应包含2个键值对");
        assertEquals("value1", result.getExtra().get("key1"), "key1值应匹配");
        assertEquals("value2", result.getExtra().get("key2"), "key2值应匹配");
    }

    @Test
    @DisplayName("测试putExtra-null值")
    void testPutExtra_NullValue() {
        ResponseResult result = ResponseResult.success();
        String key = "testKey";
        
        result.putExtra(key, null);
        
        assertTrue(result.getExtra().containsKey(key), "extra应包含键");
        assertNull(result.getExtra().get(key), "值应为null");
    }

    // ==================== 综合测试 ====================

    @Test
    @DisplayName("测试success和putExtra组合使用")
    void testSuccess_WithPutExtra() {
        Object data = "测试数据";
        ResponseResult result = ResponseResult.success(data)
                .putExtra("extraKey", "extraValue");
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(SUCCESS.getCode(), result.getCode(), "响应码应为SUCCESS");
        assertEquals(data, result.getData(), "数据应匹配");
        assertEquals("extraValue", result.getExtra().get("extraKey"), "extra值应匹配");
    }

    @Test
    @DisplayName("测试error和putExtra组合使用")
    void testError_WithPutExtra() {
        String message = "操作失败";
        ResponseResult result = ResponseResult.error(message)
                .putExtra("errorCode", "E001");
        
        assertNotNull(result, "返回结果不应为null");
        assertEquals(FAILURE.getCode(), result.getCode(), "响应码应为FAILURE");
        assertEquals(message, result.getMessage(), "消息应匹配");
        assertEquals("E001", result.getExtra().get("errorCode"), "extra值应匹配");
    }

    @Test
    @DisplayName("测试不同ResultCode的使用")
    void testDifferentResultCodes() {
        // 测试SUCCESS
        ResponseResult successResult = ResponseResult.success();
        assertEquals(SUCCESS.getCode(), successResult.getCode(), "SUCCESS响应码应匹配");
        
        // 测试FAILURE
        ResponseResult errorResult = ResponseResult.error();
        assertEquals(FAILURE.getCode(), errorResult.getCode(), "FAILURE响应码应匹配");
        
        // 测试自定义错误码
        ResponseResult customResult = ResponseResult.error(404, "未找到");
        assertEquals(404, customResult.getCode(), "自定义响应码应匹配");
    }

    @Test
    @DisplayName("测试extra的初始化")
    void testExtraInitialization() {
        ResponseResult result = new ResponseResult(200, "测试", null);
        
        assertNotNull(result.getExtra(), "extra不应为null");
        assertTrue(result.getExtra() instanceof java.util.HashMap, "extra应为HashMap类型");
        assertTrue(result.getExtra().isEmpty(), "extra应为空");
    }
}

