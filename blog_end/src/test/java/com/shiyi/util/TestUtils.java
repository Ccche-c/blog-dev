package com.shiyi.util;

import com.shiyi.common.ResponseResult;
import com.shiyi.common.ResultCode;

/**
 * 测试工具类
 * 
 * @author shiyi
 * @date 2024/01/01
 */
public class TestUtils {
    
    /**
     * 创建成功的ResponseResult
     */
    public static ResponseResult createSuccessResult(Object data) {
        return ResponseResult.success(data);
    }
    
    /**
     * 创建成功的ResponseResult（带消息）
     */
    public static ResponseResult createSuccessResult(String message, Object data) {
        return ResponseResult.success(message, data);
    }
    
    /**
     * 创建失败的ResponseResult
     */
    public static ResponseResult createErrorResult(String message) {
        return ResponseResult.error(message);
    }
    
    /**
     * 验证ResponseResult是否成功
     */
    public static boolean isSuccess(ResponseResult result) {
        return result != null && result.getCode() != null 
            && result.getCode().equals(ResultCode.SUCCESS.getCode());
    }
    
    /**
     * 验证ResponseResult是否失败
     */
    public static boolean isError(ResponseResult result) {
        return result != null && result.getCode() != null 
            && !result.getCode().equals(ResultCode.SUCCESS.getCode());
    }
}

