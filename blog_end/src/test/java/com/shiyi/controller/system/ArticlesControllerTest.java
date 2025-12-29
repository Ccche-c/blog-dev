package com.shiyi.controller.system;

import com.shiyi.common.ResponseResult;
import com.shiyi.dto.ArticleDTO;
import com.shiyi.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * ArticlesController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("系统文章管理接口测试")
class ArticlesControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ArticlesController articlesController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试获取文章列表-成功")
    void testList_Success() {
        // 准备测试数据
        @SuppressWarnings("unchecked")
        Map<String, Object> params = new HashMap<>();
        params.put("title", "测试");
        params.put("categoryId", 1);
        
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.listArticle(params))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.list(params);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).listArticle(params);
    }

    @Test
    @DisplayName("测试获取文章详情-成功")
    void testGetArticleById_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.getArticleById(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.getArticleById(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).getArticleById(id);
    }

    @Test
    @DisplayName("测试保存文章-成功")
    void testInsert_Success() {
        // 准备测试数据
        ArticleDTO article = new ArticleDTO();
        article.setTitle("测试文章");
        article.setContent("测试内容");
        
        ResponseResult expectedResult = ResponseResult.success("保存成功");

        // 模拟Service返回
        when(articleService.insertArticle(any(ArticleDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.insert(article);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).insertArticle(any(ArticleDTO.class));
    }

    @Test
    @DisplayName("测试修改文章-成功")
    void testUpdate_Success() {
        // 准备测试数据
        ArticleDTO article = new ArticleDTO();
        article.setId(1L);
        article.setTitle("修改后的标题");
        
        ResponseResult expectedResult = ResponseResult.success("修改成功");

        // 模拟Service返回
        when(articleService.updateArticle(any(ArticleDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.update(article);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).updateArticle(any(ArticleDTO.class));
    }

    @Test
    @DisplayName("测试删除文章-成功")
    void testDelete_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("删除成功");

        // 模拟Service返回
        when(articleService.deleteArticle(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.delete(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).deleteArticle(id);
    }

    @Test
    @DisplayName("测试批量删除文章-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        ResponseResult expectedResult = ResponseResult.success("批量删除成功");

        // 模拟Service返回
        when(articleService.deleteBatchArticle(anyList()))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.deleteBatch(ids);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).deleteBatchArticle(anyList());
    }

    @Test
    @DisplayName("测试置顶文章-成功")
    void testPutTopArticle_Success() {
        // 准备测试数据
        ArticleDTO article = new ArticleDTO();
        article.setId(1L);
        article.setIsStick(1);
        
        ResponseResult expectedResult = ResponseResult.success("置顶成功");

        // 模拟Service返回
        when(articleService.putTopArticle(any(ArticleDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.putTopArticle(article);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).putTopArticle(any(ArticleDTO.class));
    }

    @Test
    @DisplayName("测试发布或下架文章-成功")
    void testPublishAndShelf_Success() {
        // 准备测试数据
        ArticleDTO article = new ArticleDTO();
        article.setId(1L);
        article.setIsPublish(1);
        
        ResponseResult expectedResult = ResponseResult.success("操作成功");

        // 模拟Service返回
        when(articleService.publishAndShelf(any(ArticleDTO.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.publishAndShelf(article);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).publishAndShelf(any(ArticleDTO.class));
    }

    @Test
    @DisplayName("测试文章SEO-成功")
    void testArticleSeo_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L);
        ResponseResult expectedResult = ResponseResult.success("SEO提交成功");

        // 模拟Service返回
        when(articleService.articleSeo(anyList()))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.articleSeo(ids);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).articleSeo(anyList());
    }

    @Test
    @DisplayName("测试文章爬虫-成功")
    void testReptile_Success() {
        // 准备测试数据
        String url = "https://example.com/article";
        ResponseResult expectedResult = ResponseResult.success("爬取成功");

        // 模拟Service返回
        when(articleService.reptile(url))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.reptile(url);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).reptile(url);
    }

    @Test
    @DisplayName("测试随机获取图片-成功")
    void testRandomImg_Success() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.randomImg())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = articlesController.randomImg();

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).randomImg();
    }
}

