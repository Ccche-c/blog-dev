package com.shiyi.controller.api;

import com.shiyi.annotation.BusinessLogger;
import com.shiyi.common.ResponseResult;
import com.shiyi.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author blue
 * @Description: 博客前台博客文章控制器
 * @Date 2021-07-25 19:04
 */
@RestController
@RequestMapping("/v1/article")
@Api(tags = "文章管理-门户")
@RequiredArgsConstructor
public class ApiArticleController {

    private final ArticleService articleService;

    @BusinessLogger(value = "首页-用户访问首页",type = "查询",desc = "查询所有文章")
    @GetMapping(value = "/list")
    @ApiOperation(value = "文章列表", httpMethod = "GET", response = ResponseResult.class, notes = "文章列表")
    public ResponseResult selectPublicArticleList(Integer categoryId,Integer tagId) {
        return  articleService.selectPublicArticleList(categoryId,tagId);
    }

    @BusinessLogger(value = "门户-用户查看文章详情",type = "查询",desc = "查看文章详情")
    @GetMapping(value = "/info")
    @ApiOperation(value = "文章详情", httpMethod = "GET", response = ResponseResult.class, notes = "文章详情")
    public ResponseResult selectPublicArticleInfo(Integer id) {
        return articleService.selectPublicArticleInfo(id);
    }

    @BusinessLogger(value = "门户-用户搜索文章",type = "查询",desc = "用户搜索文章")
    @GetMapping(value = "/search")
    @ApiOperation(value = "用户搜索文章", httpMethod = "GET", response = ResponseResult.class, notes = "用户搜索文章")
    public ResponseResult publicSearchArticle(String keyword) {
        return articleService.publicSearchArticle(keyword);
    }

    @BusinessLogger(value = "首页-归档",type = "查询",desc = "归档")
    @GetMapping(value = "/archive")
    @ApiOperation(value = "归档", httpMethod = "GET", response = ResponseResult.class, notes = "归档")
    public ResponseResult archive() {
        return  articleService.archive();
    }

    @BusinessLogger(value = "门户-文章点赞",type = "查询",desc = "文章点赞")
    @GetMapping(value = "/like")
    @ApiOperation(value = "文章点赞", httpMethod = "GET", response = ResponseResult.class, notes = "文章点赞")
    public ResponseResult articleLike(Integer articleId) {
        return articleService.articleLike(articleId);
    }

    @BusinessLogger(value = "文章详情-用户验证秘钥",type = "查询",desc = "验证秘钥")
    @GetMapping(value = "/checkSecret")
    @ApiOperation(value = "验证秘钥", httpMethod = "GET", response = ResponseResult.class, notes = "验证秘钥")
    public ResponseResult checkSecret(String code) {
        return articleService.checkSecret(code);
    }
}
