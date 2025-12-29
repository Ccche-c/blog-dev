package com.shiyi.controller.api;

import com.shiyi.annotation.BusinessLogger;
import com.shiyi.common.ResponseResult;
import com.shiyi.dto.AiChatRequest;
import com.shiyi.service.AiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * AI对话控制器（门户）
 *
 * @author shiyi
 * @date 2024/01/01
 */
@RestController
@RequestMapping("/v1/ai")
@Api(tags = "AI对话-门户")
@RequiredArgsConstructor
@Validated
public class ApiAiController {

    private final AiService aiService;

    @BusinessLogger(value = "AI对话-用户使用AI对话功能", type = "查询", desc = "AI对话推送精品内容")
    @PostMapping("/chat")
    @ApiOperation(value = "AI对话", httpMethod = "POST", response = ResponseResult.class, notes = "AI对话功能，用于推送精品内容")
    public ResponseResult chat(@Valid @RequestBody AiChatRequest request) {
        return aiService.chat(request);
    }
}

