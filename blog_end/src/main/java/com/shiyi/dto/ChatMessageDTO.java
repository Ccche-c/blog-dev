package com.shiyi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 聊天消息DTO
 *
 * @author shiyi
 * @date 2024/01/01
 */
@Data
@ApiModel("聊天消息")
public class ChatMessageDTO {

    /**
     * 角色：user/assistant/system
     */
    @NotBlank(message = "角色不能为空")
    @ApiModelProperty(value = "角色（user/assistant/system）", required = true)
    private String role;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @ApiModelProperty(value = "消息内容", required = true)
    private String content;
}

