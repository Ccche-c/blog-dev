package com.shiyi.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCommentListVO {
    private Integer id;
    private Long userId;
    private Long replyUserId;

    private String content;

    private String nickname;
    private String webSite;

    private String replyNickname;
    private String replyWebSite;

    private String avatar;

    private Date createTime;

    private String browser;

    private String browserVersion;

    private String system;

    private String systemVersion;
    private String ipAddress;

    List<ApiCommentListVO> children = new ArrayList<>();


}
