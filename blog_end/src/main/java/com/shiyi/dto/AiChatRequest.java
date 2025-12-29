package com.shiyi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * AI对话请求DTO
 *
 * @author shiyi
 * @date 2024/01/01
 */
@Data
@ApiModel("AI对话请求参数")
public class AiChatRequest {

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @ApiModelProperty(value = "消息内容", required = true)
    private String message;

    /**
     * 自定义系统提示词（可选，不传则使用默认）
     */
    @ApiModelProperty(value = "自定义系统提示词")
    private String systemPrompt;

    /**
     * 对话历史（可选，用于上下文对话）
     */
    @ApiModelProperty(value = "对话历史")
    private List<ChatMessageDTO> history;

    /**
     * 温度参数（可选，0-2之间）
     */
    @ApiModelProperty(value = "温度参数（0-2，控制回复随机性）")
    private Double temperature;
}

