package com.shiyi.controller.api;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.shiyi.annotation.BusinessLogger;
import com.shiyi.common.ResponseResult;
import com.shiyi.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author blue
 * @Description: 用户文章管理控制器
 * @Date 2021-07-25 19:04
 */
@RestController
@RequestMapping("/v1/user/article")
@Api(tags = "用户文章管理-门户")
@RequiredArgsConstructor
public class ApiUserArticleController {

    private final ArticleService articleService;

    @BusinessLogger(value = "用户-获取当前用户文章列表",type = "查询",desc = "获取当前用户文章列表")
    @GetMapping(value = "/list")
    @SaCheckLogin
    @ApiOperation(value = "当前用户文章列表", httpMethod = "GET", response = ResponseResult.class, notes = "获取当前用户的文章列表（无论是否发布）")
    public ResponseResult selectUserArticleList(
            @ApiParam(name = "categoryId", value = "分类ID", required = false) Integer categoryId,
            @ApiParam(name = "tagId", value = "标签ID", required = false) Integer tagId) {
        return articleService.selectUserArticleList(categoryId,tagId);
    }
}

