package com.shiyi.strategy.imp;

import com.shiyi.common.Constants;
import com.shiyi.common.FieldConstants;
import com.shiyi.vo.ApiArticleSearchVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EsSearchStrategyImpl 单元测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Elasticsearch搜索策略实现类测试")
@SuppressWarnings("unchecked")
class EsSearchStrategyImplTest {

    @Mock
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @InjectMocks
    private EsSearchStrategyImpl esSearchStrategy;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    // ==================== searchArticle 方法测试 ====================

    @Test
    @DisplayName("测试搜索文章-空关键词")
    void testSearchArticle_BlankKeywords() {
        // 测试空字符串
        List<ApiArticleSearchVO> result1 = esSearchStrategy.searchArticle("");
        assertNotNull(result1, "返回结果不应为null");
        assertTrue(result1.isEmpty(), "空关键词应返回空列表");

        // 测试null
        List<ApiArticleSearchVO> result2 = esSearchStrategy.searchArticle(null);
        assertNotNull(result2, "返回结果不应为null");
        assertTrue(result2.isEmpty(), "null关键词应返回空列表");

        // 测试空白字符串
        List<ApiArticleSearchVO> result3 = esSearchStrategy.searchArticle("   ");
        assertNotNull(result3, "返回结果不应为null");
        assertTrue(result3.isEmpty(), "空白关键词应返回空列表");

        // 验证没有调用Elasticsearch
        verify(elasticsearchRestTemplate, never()).search(any(NativeSearchQuery.class), any(Class.class));
    }

    @Test
    @DisplayName("测试搜索文章-成功")
    void testSearchArticle_Success() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String title = "测试文章";
        String content = "这是一篇测试文章的内容";

        ApiArticleSearchVO article = ApiArticleSearchVO.builder()
                .id(articleId)
                .title(title)
                .content(content)
                .build();

        // 创建模拟的SearchHit
        SearchHit<ApiArticleSearchVO> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(article);

        // 创建模拟的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(searchHit));

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "应返回1篇文章");
        assertEquals(articleId, result.get(0).getId(), "文章ID应匹配");
        assertEquals(title, result.get(0).getTitle(), "标题应匹配");
        assertEquals(content, result.get(0).getContent(), "内容应匹配");

        // 验证方法调用
        verify(elasticsearchRestTemplate, times(1)).search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class));
    }

    @Test
    @DisplayName("测试搜索文章-带标题高亮")
    void testSearchArticle_WithTitleHighlight() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String originalTitle = "测试文章";
        String highlightedTitle = Constants.PRE_TAG + "测试" + Constants.POST_TAG + "文章";
        String content = "这是一篇测试文章的内容";

        ApiArticleSearchVO article = ApiArticleSearchVO.builder()
                .id(articleId)
                .title(originalTitle)
                .content(content)
                .build();

        // 创建高亮字段映射
        Map<String, List<String>> highlightFields = new HashMap<>();
        highlightFields.put(FieldConstants.TITLE, Arrays.asList(highlightedTitle));

        // 创建模拟的SearchHit
        SearchHit<ApiArticleSearchVO> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(article);
        when(searchHit.getHighlightFields()).thenReturn(highlightFields);

        // 创建模拟的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(searchHit));

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "应返回1篇文章");
        assertEquals(highlightedTitle, result.get(0).getTitle(), "标题应被高亮替换");
        assertEquals(content, result.get(0).getContent(), "内容应保持不变");
    }

    @Test
    @DisplayName("测试搜索文章-带内容高亮")
    void testSearchArticle_WithContentHighlight() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String title = "测试文章";
        String originalContent = "这是一篇测试文章的内容";
        String highlightedContent = "这是一篇" + Constants.PRE_TAG + "测试" + Constants.POST_TAG + "文章的内容";

        ApiArticleSearchVO article = ApiArticleSearchVO.builder()
                .id(articleId)
                .title(title)
                .content(originalContent)
                .build();

        // 创建高亮字段映射
        Map<String, List<String>> highlightFields = new HashMap<>();
        highlightFields.put(FieldConstants.CONTENT, Arrays.asList(highlightedContent, "另一个片段"));

        // 创建模拟的SearchHit
        SearchHit<ApiArticleSearchVO> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(article);
        when(searchHit.getHighlightFields()).thenReturn(highlightFields);

        // 创建模拟的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(searchHit));

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "应返回1篇文章");
        assertEquals(title, result.get(0).getTitle(), "标题应保持不变");
        // 应该使用最后一个高亮片段
        assertEquals("另一个片段", result.get(0).getContent(), "内容应使用最后一个高亮片段");
    }

    @Test
    @DisplayName("测试搜索文章-标题和内容都高亮")
    void testSearchArticle_WithBothHighlights() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String originalTitle = "测试文章";
        String highlightedTitle = Constants.PRE_TAG + "测试" + Constants.POST_TAG + "文章";
        String originalContent = "这是一篇测试文章的内容";
        String highlightedContent = "这是一篇" + Constants.PRE_TAG + "测试" + Constants.POST_TAG + "文章的内容";

        ApiArticleSearchVO article = ApiArticleSearchVO.builder()
                .id(articleId)
                .title(originalTitle)
                .content(originalContent)
                .build();

        // 创建高亮字段映射
        Map<String, List<String>> highlightFields = new HashMap<>();
        highlightFields.put(FieldConstants.TITLE, Arrays.asList(highlightedTitle));
        highlightFields.put(FieldConstants.CONTENT, Arrays.asList(highlightedContent));

        // 创建模拟的SearchHit
        SearchHit<ApiArticleSearchVO> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(article);
        when(searchHit.getHighlightFields()).thenReturn(highlightFields);

        // 创建模拟的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(searchHit));

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "应返回1篇文章");
        assertEquals(highlightedTitle, result.get(0).getTitle(), "标题应被高亮替换");
        assertEquals(highlightedContent, result.get(0).getContent(), "内容应被高亮替换");
    }

    @Test
    @DisplayName("测试搜索文章-无高亮数据")
    void testSearchArticle_NoHighlight() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String title = "测试文章";
        String content = "这是一篇测试文章的内容";

        ApiArticleSearchVO article = ApiArticleSearchVO.builder()
                .id(articleId)
                .title(title)
                .content(content)
                .build();

        // 创建空的高亮字段映射
        Map<String, List<String>> highlightFields = new HashMap<>();

        // 创建模拟的SearchHit
        SearchHit<ApiArticleSearchVO> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(article);
        when(searchHit.getHighlightFields()).thenReturn(highlightFields);

        // 创建模拟的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(searchHit));

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "应返回1篇文章");
        assertEquals(title, result.get(0).getTitle(), "标题应保持不变");
        assertEquals(content, result.get(0).getContent(), "内容应保持不变");
    }

    @Test
    @DisplayName("测试搜索文章-多篇文章")
    void testSearchArticle_MultipleArticles() {
        // 准备测试数据
        String keywords = "测试";

        ApiArticleSearchVO article1 = ApiArticleSearchVO.builder()
                .id(1L)
                .title("第一篇测试文章")
                .content("内容1")
                .build();

        ApiArticleSearchVO article2 = ApiArticleSearchVO.builder()
                .id(2L)
                .title("第二篇测试文章")
                .content("内容2")
                .build();

        // 创建模拟的SearchHit
        SearchHit<ApiArticleSearchVO> searchHit1 = mock(SearchHit.class);
        when(searchHit1.getContent()).thenReturn(article1);
        when(searchHit1.getHighlightFields()).thenReturn(new HashMap<>());

        SearchHit<ApiArticleSearchVO> searchHit2 = mock(SearchHit.class);
        when(searchHit2.getContent()).thenReturn(article2);
        when(searchHit2.getHighlightFields()).thenReturn(new HashMap<>());

        // 创建模拟的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(searchHit1, searchHit2));

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(2, result.size(), "应返回2篇文章");
        assertEquals(1L, result.get(0).getId(), "第一篇文章ID应匹配");
        assertEquals(2L, result.get(1).getId(), "第二篇文章ID应匹配");
    }

    @Test
    @DisplayName("测试搜索文章-空结果")
    void testSearchArticle_EmptyResult() {
        // 准备测试数据
        String keywords = "不存在的关键词";

        // 创建空的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(new ArrayList<>());

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.isEmpty(), "结果应为空列表");

        // 验证方法调用
        verify(elasticsearchRestTemplate, times(1)).search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class));
    }

    @Test
    @DisplayName("测试搜索文章-异常处理")
    void testSearchArticle_Exception() {
        // 准备测试数据
        String keywords = "测试";

        // 模拟Elasticsearch抛出异常
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenThrow(new RuntimeException("Elasticsearch连接失败"));

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.isEmpty(), "异常时应返回空列表");

        // 验证方法调用
        verify(elasticsearchRestTemplate, times(1)).search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class));
    }

    @Test
    @DisplayName("测试搜索文章-内容高亮多个片段")
    void testSearchArticle_ContentMultipleFragments() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String title = "测试文章";
        String originalContent = "这是一篇测试文章的内容";

        ApiArticleSearchVO article = ApiArticleSearchVO.builder()
                .id(articleId)
                .title(title)
                .content(originalContent)
                .build();

        // 创建多个高亮片段
        String fragment1 = "第一个" + Constants.PRE_TAG + "测试" + Constants.POST_TAG + "片段";
        String fragment2 = "第二个" + Constants.PRE_TAG + "测试" + Constants.POST_TAG + "片段";
        String fragment3 = "第三个" + Constants.PRE_TAG + "测试" + Constants.POST_TAG + "片段";

        // 创建高亮字段映射
        Map<String, List<String>> highlightFields = new HashMap<>();
        highlightFields.put(FieldConstants.CONTENT, Arrays.asList(fragment1, fragment2, fragment3));

        // 创建模拟的SearchHit
        SearchHit<ApiArticleSearchVO> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(article);
        when(searchHit.getHighlightFields()).thenReturn(highlightFields);

        // 创建模拟的SearchHits
        SearchHits<ApiArticleSearchVO> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(searchHit));

        // 模拟Elasticsearch返回
        when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ApiArticleSearchVO.class)))
                .thenReturn(searchHits);

        // 执行测试
        List<ApiArticleSearchVO> result = esSearchStrategy.searchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "应返回1篇文章");
        // 应该使用最后一个片段
        assertEquals(fragment3, result.get(0).getContent(), "内容应使用最后一个高亮片段");
    }
}

