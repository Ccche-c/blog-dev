package com.shiyi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shiyi.entity.Tags;
import com.shiyi.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiArticleListVO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 作者昵称
     */
    private String username;

    /**
     * 文章标题
     */
    private String title;
    /**
     *文章封面图
     */
    private String avatar;
    /**
     * 文章描述
     */
    private String summary;
    /**
     * 是否置顶
     */
    private Integer isStick;
    /**
     * 是否原创
     */
    private Integer isOriginal;
    /**
     * 是否发布 0：下架 1：发布
     */
    private Integer isPublish;
    /**
     * 阅读量
     */
    private Integer quantity;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞量
     */
    private Object likeCount;

    /**
     * 发表时间
     */
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD,timezone="GMT+8")
    private Date createTime;

    /**
     * 标签集合
     */
    private List<Tags> tagList;
}
