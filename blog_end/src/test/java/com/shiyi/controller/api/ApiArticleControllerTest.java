package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ApiArticleController 单元测试
 * 使用JaCoCo进行代码覆盖率测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("门户文章管理接口测试")
class ApiArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ApiArticleController apiArticleController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
        reset(articleService);
    }

    // ==================== selectPublicArticleList 方法测试 ====================

    @Test
    @DisplayName("测试获取文章列表-成功场景（带分类和标签）")
    void testSelectPublicArticleList_Success_WithCategoryAndTag() {
        // 准备测试数据
        Integer categoryId = 1;
        Integer tagId = 2;
        ResponseResult expectedResult = ResponseResult.success("获取成功", "文章列表数据");

        // 模拟Service返回
        when(articleService.selectPublicArticleList(categoryId, tagId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleList(categoryId, tagId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(articleService, times(1)).selectPublicArticleList(categoryId, tagId);
    }

    @Test
    @DisplayName("测试获取文章列表-无参数")
    void testSelectPublicArticleList_NoParams() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.selectPublicArticleList(null, null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleList(null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).selectPublicArticleList(null, null);
    }

    @Test
    @DisplayName("测试获取文章列表-仅分类ID")
    void testSelectPublicArticleList_WithCategoryOnly() {
        // 准备测试数据
        Integer categoryId = 1;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.selectPublicArticleList(categoryId, null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleList(categoryId, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).selectPublicArticleList(categoryId, null);
    }

    @Test
    @DisplayName("测试获取文章列表-仅标签ID")
    void testSelectPublicArticleList_WithTagOnly() {
        // 准备测试数据
        Integer tagId = 2;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.selectPublicArticleList(null, tagId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleList(null, tagId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).selectPublicArticleList(null, tagId);
    }

    @Test
    @DisplayName("测试获取文章列表-失败场景")
    void testSelectPublicArticleList_Failure() {
        // 准备测试数据
        Integer categoryId = 1;
        Integer tagId = 2;
        ResponseResult expectedResult = ResponseResult.error("获取失败");

        // 模拟Service返回
        when(articleService.selectPublicArticleList(categoryId, tagId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleList(categoryId, tagId);

        // 验证结果
        assertNotNull(result);
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(articleService, times(1)).selectPublicArticleList(categoryId, tagId);
    }

    // ==================== selectPublicArticleInfo 方法测试 ====================

    @Test
    @DisplayName("测试获取文章详情-成功场景")
    void testSelectPublicArticleInfo_Success() {
        // 准备测试数据
        Integer id = 1;
        ResponseResult expectedResult = ResponseResult.success("获取成功", "文章详情数据");

        // 模拟Service返回
        when(articleService.selectPublicArticleInfo(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleInfo(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(articleService, times(1)).selectPublicArticleInfo(id);
    }

    @Test
    @DisplayName("测试获取文章详情-文章不存在")
    void testSelectPublicArticleInfo_NotFound() {
        // 准备测试数据
        Integer id = 999;
        ResponseResult expectedResult = ResponseResult.error("文章不存在");

        // 模拟Service返回
        when(articleService.selectPublicArticleInfo(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleInfo(id);

        // 验证结果
        assertNotNull(result);
        assertNotEquals(200, result.getCode());
        verify(articleService, times(1)).selectPublicArticleInfo(id);
    }

    @Test
    @DisplayName("测试获取文章详情-空ID")
    void testSelectPublicArticleInfo_NullId() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.error("文章ID不能为空");

        // 模拟Service返回
        when(articleService.selectPublicArticleInfo(null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.selectPublicArticleInfo(null);

        // 验证结果
        assertNotNull(result);
        verify(articleService, times(1)).selectPublicArticleInfo(null);
    }

    // ==================== publicSearchArticle 方法测试 ====================

    @Test
    @DisplayName("测试搜索文章-成功场景")
    void testPublicSearchArticle_Success() {
        // 准备测试数据
        String keyword = "Spring Boot";
        ResponseResult expectedResult = ResponseResult.success("搜索成功", "搜索结果数据");

        // 模拟Service返回
        when(articleService.publicSearchArticle(keyword))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.publicSearchArticle(keyword);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(articleService, times(1)).publicSearchArticle(keyword);
    }

    @Test
    @DisplayName("测试搜索文章-空关键词")
    void testPublicSearchArticle_EmptyKeyword() {
        // 准备测试数据
        String keyword = "";
        ResponseResult expectedResult = ResponseResult.success("搜索成功", "空结果");

        // 模拟Service返回
        when(articleService.publicSearchArticle(keyword))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.publicSearchArticle(keyword);

        // 验证结果
        assertNotNull(result);
        verify(articleService, times(1)).publicSearchArticle(keyword);
    }

    @Test
    @DisplayName("测试搜索文章-Null关键词")
    void testPublicSearchArticle_NullKeyword() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("搜索成功");

        // 模拟Service返回
        when(articleService.publicSearchArticle(null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.publicSearchArticle(null);

        // 验证结果
        assertNotNull(result);
        verify(articleService, times(1)).publicSearchArticle(null);
    }

    @Test
    @DisplayName("测试搜索文章-长关键词")
    void testPublicSearchArticle_LongKeyword() {
        // 准备测试数据
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("测试关键词");
        }
        String longKeyword = sb.toString();
        ResponseResult expectedResult = ResponseResult.success("搜索成功");

        // 模拟Service返回
        when(articleService.publicSearchArticle(longKeyword))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.publicSearchArticle(longKeyword);

        // 验证结果
        assertNotNull(result);
        verify(articleService, times(1)).publicSearchArticle(argThat(k -> k.length() > 100));
    }

    @Test
    @DisplayName("测试搜索文章-无结果")
    void testPublicSearchArticle_NoResults() {
        // 准备测试数据
        String keyword = "不存在的关键词";
        ResponseResult expectedResult = ResponseResult.success("搜索成功", null);

        // 模拟Service返回
        when(articleService.publicSearchArticle(keyword))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.publicSearchArticle(keyword);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).publicSearchArticle(keyword);
    }

    // ==================== archive 方法测试 ====================

    @Test
    @DisplayName("测试归档-成功场景")
    void testArchive_Success() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功", "归档数据");

        // 模拟Service返回
        when(articleService.archive())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.archive();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(articleService, times(1)).archive();
    }

    @Test
    @DisplayName("测试归档-空归档")
    void testArchive_Empty() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功", null);

        // 模拟Service返回
        when(articleService.archive())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.archive();

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
        verify(articleService, times(1)).archive();
    }

    @Test
    @DisplayName("测试归档-多次调用")
    void testArchive_MultipleCalls() {
        // 准备测试数据
        ResponseResult result1 = ResponseResult.success("第一次", "数据1");
        ResponseResult result2 = ResponseResult.success("第二次", "数据2");

        // 模拟Service返回
        when(articleService.archive())
                .thenReturn(result1)
                .thenReturn(result2);

        // 执行测试
        ResponseResult response1 = apiArticleController.archive();
        ResponseResult response2 = apiArticleController.archive();

        // 验证结果
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(200, response1.getCode());
        assertEquals(200, response2.getCode());
        verify(articleService, times(2)).archive();
    }

    // ==================== articleLike 方法测试 ====================

    @Test
    @DisplayName("测试文章点赞-成功场景（点赞）")
    void testArticleLike_Success() {
        // 准备测试数据
        Integer articleId = 1;
        ResponseResult expectedResult = ResponseResult.success("点赞成功");

        // 模拟Service返回
        when(articleService.articleLike(articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.articleLike(articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleService, times(1)).articleLike(articleId);
    }

    @Test
    @DisplayName("测试文章点赞-取消点赞")
    void testArticleLike_CancelLike() {
        // 准备测试数据
        Integer articleId = 1;
        ResponseResult expectedResult = ResponseResult.success("取消点赞成功");

        // 模拟Service返回（第二次调用表示取消点赞）
        when(articleService.articleLike(articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.articleLike(articleId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).articleLike(articleId);
    }

    @Test
    @DisplayName("测试文章点赞-空文章ID")
    void testArticleLike_NullArticleId() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.error("文章ID不能为空");

        // 模拟Service返回
        when(articleService.articleLike(null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.articleLike(null);

        // 验证结果
        assertNotNull(result);
        assertNotEquals(200, result.getCode());
        verify(articleService, times(1)).articleLike(null);
    }

    @Test
    @DisplayName("测试文章点赞-多次点赞同一文章")
    void testArticleLike_MultipleLikes() {
        // 准备测试数据
        Integer articleId = 1;
        ResponseResult result1 = ResponseResult.success("点赞成功");
        ResponseResult result2 = ResponseResult.success("取消点赞成功");

        // 模拟Service返回
        when(articleService.articleLike(articleId))
                .thenReturn(result1)
                .thenReturn(result2);

        // 执行测试
        ResponseResult response1 = apiArticleController.articleLike(articleId);
        ResponseResult response2 = apiArticleController.articleLike(articleId);

        // 验证结果
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(200, response1.getCode());
        assertEquals(200, response2.getCode());
        verify(articleService, times(2)).articleLike(articleId);
    }

    // ==================== checkSecret 方法测试 ====================

    @Test
    @DisplayName("测试验证秘钥-成功场景")
    void testCheckSecret_Success() {
        // 准备测试数据
        String code = "test123";
        ResponseResult expectedResult = ResponseResult.success("验证成功");

        // 模拟Service返回
        when(articleService.checkSecret(code))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.checkSecret(code);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleService, times(1)).checkSecret(code);
    }

    @Test
    @DisplayName("测试验证秘钥-验证失败")
    void testCheckSecret_Failure() {
        // 准备测试数据
        String code = "wrong_code";
        ResponseResult expectedResult = ResponseResult.error("验证码错误");

        // 模拟Service返回
        when(articleService.checkSecret(code))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.checkSecret(code);

        // 验证结果
        assertNotNull(result);
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("验证码错误", result.getMessage());
        verify(articleService, times(1)).checkSecret(code);
    }

    @Test
    @DisplayName("测试验证秘钥-空验证码")
    void testCheckSecret_EmptyCode() {
        // 准备测试数据
        String code = "";
        ResponseResult expectedResult = ResponseResult.error("验证码不能为空");

        // 模拟Service返回
        when(articleService.checkSecret(code))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.checkSecret(code);

        // 验证结果
        assertNotNull(result);
        assertNotEquals(200, result.getCode());
        verify(articleService, times(1)).checkSecret(code);
    }

    @Test
    @DisplayName("测试验证秘钥-Null验证码")
    void testCheckSecret_NullCode() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.error("验证码不能为空");

        // 模拟Service返回
        when(articleService.checkSecret(null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.checkSecret(null);

        // 验证结果
        assertNotNull(result);
        assertNotEquals(200, result.getCode());
        verify(articleService, times(1)).checkSecret(null);
    }

    @Test
    @DisplayName("测试验证秘钥-长验证码")
    void testCheckSecret_LongCode() {
        // 准备测试数据
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append("A");
        }
        String longCode = sb.toString();
        ResponseResult expectedResult = ResponseResult.success("验证成功");

        // 模拟Service返回
        when(articleService.checkSecret(longCode))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiArticleController.checkSecret(longCode);

        // 验证结果
        assertNotNull(result);
        verify(articleService, times(1)).checkSecret(argThat(c -> c.length() > 40));
    }

    // ==================== 综合测试 ====================

    @Test
    @DisplayName("测试所有方法-验证Service调用")
    void testAllMethods_VerifyServiceCalls() {
        // 准备测试数据
        ResponseResult successResult = ResponseResult.success("成功");

        // 模拟所有Service方法
        when(articleService.selectPublicArticleList(any(), any())).thenReturn(successResult);
        when(articleService.selectPublicArticleInfo(any())).thenReturn(successResult);
        when(articleService.publicSearchArticle(anyString())).thenReturn(successResult);
        when(articleService.archive()).thenReturn(successResult);
        when(articleService.articleLike(any())).thenReturn(successResult);
        when(articleService.checkSecret(anyString())).thenReturn(successResult);

        // 执行所有方法
        apiArticleController.selectPublicArticleList(1, 2);
        apiArticleController.selectPublicArticleInfo(1);
        apiArticleController.publicSearchArticle("test");
        apiArticleController.archive();
        apiArticleController.articleLike(1);
        apiArticleController.checkSecret("code");

        // 验证所有方法都被调用
        verify(articleService, times(1)).selectPublicArticleList(any(), any());
        verify(articleService, times(1)).selectPublicArticleInfo(any());
        verify(articleService, times(1)).publicSearchArticle(anyString());
        verify(articleService, times(1)).archive();
        verify(articleService, times(1)).articleLike(any());
        verify(articleService, times(1)).checkSecret(anyString());
    }

    @Test
    @DisplayName("测试Service抛出异常")
    void testServiceException() {
        // 准备测试数据
        Integer articleId = 1;

        // 模拟Service抛出异常
        when(articleService.articleLike(articleId))
                .thenThrow(new RuntimeException("服务异常"));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            apiArticleController.articleLike(articleId);
        });

        verify(articleService, times(1)).articleLike(articleId);
    }
}

