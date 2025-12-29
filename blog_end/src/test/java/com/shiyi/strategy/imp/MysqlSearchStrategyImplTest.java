package com.shiyi.strategy.imp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.Constants;
import com.shiyi.entity.Tags;
import com.shiyi.mapper.ArticleMapper;
import com.shiyi.mapper.TagsMapper;
import com.shiyi.utils.PageUtils;
import com.shiyi.vo.ApiArticleListVO;
import com.shiyi.vo.ApiArticleSearchVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MysqlSearchStrategyImpl 单元测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MySQL搜索策略实现类测试")
@SuppressWarnings("unchecked")
class MysqlSearchStrategyImplTest {

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private TagsMapper tagsMapper;

    @InjectMocks
    private MysqlSearchStrategyImpl mysqlSearchStrategy;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    // ==================== searchArticle 方法测试 ====================

    @Test
    @DisplayName("测试搜索文章-成功")
    void testSearchArticle_Success() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String title = "这是一篇测试文章";
        
        ApiArticleListVO article = ApiArticleListVO.builder()
                .id(articleId)
                .title(title)
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article);
        
        List<Tags> tags = new ArrayList<>();
        Tags tag = Tags.builder()
                .id(1L)
                .name("测试标签")
                .build();
        tags.add(tag);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(articleId)).thenReturn(tags);
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(1, result.size(), "应返回1篇文章");
            
            ApiArticleSearchVO searchVO = result.get(0);
            assertEquals(articleId, searchVO.getId(), "文章ID应匹配");
            assertNotNull(searchVO.getTitle(), "标题不应为null");
            assertTrue(searchVO.getTitle().contains(Constants.PRE_TAG), "标题应包含高亮标签");
            assertTrue(searchVO.getTitle().contains(keywords), "标题应包含关键词");
            assertTrue(searchVO.getTitle().contains(Constants.POST_TAG), "标题应包含结束标签");
            
            // 验证方法调用
            verify(articleMapper, times(1)).publicPageSearchArticle(any(Page.class), eq(keywords));
            verify(tagsMapper, times(1)).selectTagByArticleId(articleId);
        }
    }

    @Test
    @DisplayName("测试搜索文章-关键词高亮")
    void testSearchArticle_KeywordHighlight() {
        // 准备测试数据
        String keywords = "Java";
        Long articleId = 1L;
        String title = "学习Java编程语言";
        
        ApiArticleListVO article = ApiArticleListVO.builder()
                .id(articleId)
                .title(title)
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(articleId)).thenReturn(new ArrayList<>());
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(1, result.size(), "应返回1篇文章");
            
            ApiArticleSearchVO searchVO = result.get(0);
            String highlightedTitle = searchVO.getTitle();
            assertTrue(highlightedTitle.contains(Constants.PRE_TAG + keywords + Constants.POST_TAG), 
                    "标题应包含高亮的关键词");
        }
    }

    @Test
    @DisplayName("测试搜索文章-内容截取和高亮")
    void testSearchArticle_ContentHighlight() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        // 创建一个较长的标题，确保会触发内容截取逻辑
        String title = "这是一篇关于测试的文章，内容非常丰富，包含了很多测试相关的知识点";
        
        ApiArticleListVO article = ApiArticleListVO.builder()
                .id(articleId)
                .title(title)
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(articleId)).thenReturn(new ArrayList<>());
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(1, result.size(), "应返回1篇文章");
            
            ApiArticleSearchVO searchVO = result.get(0);
            assertNotNull(searchVO.getContent(), "内容不应为null");
            assertTrue(searchVO.getContent().contains(Constants.PRE_TAG + keywords + Constants.POST_TAG), 
                    "内容应包含高亮的关键词");
        }
    }

    @Test
    @DisplayName("测试搜索文章-关键词在标题开头")
    void testSearchArticle_KeywordAtStart() {
        // 准备测试数据
        String keywords = "Java";
        Long articleId = 1L;
        String title = "Java编程入门";
        
        ApiArticleListVO article = ApiArticleListVO.builder()
                .id(articleId)
                .title(title)
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(articleId)).thenReturn(new ArrayList<>());
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(1, result.size(), "应返回1篇文章");
            
            ApiArticleSearchVO searchVO = result.get(0);
            assertTrue(searchVO.getTitle().startsWith(Constants.PRE_TAG + keywords), 
                    "标题应以高亮关键词开头");
        }
    }

    @Test
    @DisplayName("测试搜索文章-关键词不在标题中")
    void testSearchArticle_KeywordNotInTitle() {
        // 准备测试数据
        String keywords = "Python";
        Long articleId = 1L;
        String title = "Java编程入门";
        
        ApiArticleListVO article = ApiArticleListVO.builder()
                .id(articleId)
                .title(title)
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(articleId)).thenReturn(new ArrayList<>());
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(1, result.size(), "应返回1篇文章");
            
            ApiArticleSearchVO searchVO = result.get(0);
            // 即使关键词不在标题中，标题仍然会被处理（替换操作）
            assertEquals(title, searchVO.getTitle(), "标题应保持不变（因为没有匹配）");
            assertEquals(title, searchVO.getContent(), "内容应为标题（因为没有匹配）");
        }
    }

    @Test
    @DisplayName("测试搜索文章-多篇文章")
    void testSearchArticle_MultipleArticles() {
        // 准备测试数据
        String keywords = "测试";
        
        ApiArticleListVO article1 = ApiArticleListVO.builder()
                .id(1L)
                .title("第一篇测试文章")
                .build();
        
        ApiArticleListVO article2 = ApiArticleListVO.builder()
                .id(2L)
                .title("第二篇测试文章")
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article1);
        articlePage.getRecords().add(article2);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(1L)).thenReturn(new ArrayList<>());
            when(tagsMapper.selectTagByArticleId(2L)).thenReturn(new ArrayList<>());
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(2, result.size(), "应返回2篇文章");
            
            // 验证每篇文章都被处理
            for (ApiArticleSearchVO searchVO : result) {
                assertNotNull(searchVO.getId(), "文章ID不应为null");
                assertNotNull(searchVO.getTitle(), "标题不应为null");
                assertNotNull(searchVO.getContent(), "内容不应为null");
            }
            
            // 验证方法调用
            verify(articleMapper, times(1)).publicPageSearchArticle(any(Page.class), eq(keywords));
            verify(tagsMapper, times(1)).selectTagByArticleId(1L);
            verify(tagsMapper, times(1)).selectTagByArticleId(2L);
        }
    }

    @Test
    @DisplayName("测试搜索文章-空结果")
    void testSearchArticle_EmptyResult() {
        // 准备测试数据
        String keywords = "不存在的关键词";
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertTrue(result.isEmpty(), "结果应为空列表");
            
            // 验证方法调用
            verify(articleMapper, times(1)).publicPageSearchArticle(any(Page.class), eq(keywords));
            verify(tagsMapper, never()).selectTagByArticleId(anyLong());
        }
    }

    @Test
    @DisplayName("测试搜索文章-带标签")
    void testSearchArticle_WithTags() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String title = "测试文章";
        
        ApiArticleListVO article = ApiArticleListVO.builder()
                .id(articleId)
                .title(title)
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article);
        
        List<Tags> tags = new ArrayList<>();
        Tags tag1 = Tags.builder()
                .id(1L)
                .name("标签1")
                .build();
        Tags tag2 = Tags.builder()
                .id(2L)
                .name("标签2")
                .build();
        tags.add(tag1);
        tags.add(tag2);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(articleId)).thenReturn(tags);
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(1, result.size(), "应返回1篇文章");
            
            // 验证标签被设置到文章对象中
            assertEquals(2, article.getTagList().size(), "文章应包含2个标签");
            assertEquals(tag1, article.getTagList().get(0), "第一个标签应匹配");
            assertEquals(tag2, article.getTagList().get(1), "第二个标签应匹配");
            
            verify(tagsMapper, times(1)).selectTagByArticleId(articleId);
        }
    }

    @Test
    @DisplayName("测试搜索文章-关键词多次出现")
    void testSearchArticle_MultipleKeywordOccurrences() {
        // 准备测试数据
        String keywords = "测试";
        Long articleId = 1L;
        String title = "测试文章测试内容";
        
        ApiArticleListVO article = ApiArticleListVO.builder()
                .id(articleId)
                .title(title)
                .build();
        
        Page<ApiArticleListVO> articlePage = new Page<>(1, 10);
        articlePage.setRecords(new ArrayList<>());
        articlePage.getRecords().add(article);
        
        // 模拟Mapper返回
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            
            when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                    .thenReturn(articlePage);
            when(tagsMapper.selectTagByArticleId(articleId)).thenReturn(new ArrayList<>());
            
            // 执行测试
            List<ApiArticleSearchVO> result = mysqlSearchStrategy.searchArticle(keywords);
            
            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(1, result.size(), "应返回1篇文章");
            
            ApiArticleSearchVO searchVO = result.get(0);
            // 验证所有出现的关键词都被高亮
            long highlightCount = searchVO.getTitle().split(Constants.PRE_TAG + keywords + Constants.POST_TAG).length - 1;
            assertTrue(highlightCount >= 1, "标题中应至少有一个高亮的关键词");
        }
    }
}

