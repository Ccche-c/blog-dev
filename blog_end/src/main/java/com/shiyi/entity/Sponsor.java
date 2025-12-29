package com.shiyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.shiyi.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author blue
 * @since 2021-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Sponsor对象", description="")
@TableName("b_sponsor")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sponsor implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "金额")
    private double price;

    @ApiModelProperty(value = "是否支付")
    private Integer isPay;

    @ApiModelProperty(value = "支付方式")
    private Integer payType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.FORMAT_STRING,timezone="GMT+8")
    private Date createTime;

}
