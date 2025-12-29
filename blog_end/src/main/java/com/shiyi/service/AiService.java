package com.shiyi.service;

import com.shiyi.common.ResponseResult;
import com.shiyi.dto.AiChatRequest;

/**
 * AI对话服务接口
 *
 * @author shiyi
 * @date 2024/01/01
 */
public interface AiService {

    /**
     * AI对话
     *
     * @param request 对话请求
     * @return 对话响应
     */
    ResponseResult chat(AiChatRequest request);
}

