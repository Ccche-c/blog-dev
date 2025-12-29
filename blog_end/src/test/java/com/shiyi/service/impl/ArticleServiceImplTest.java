package com.shiyi.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.*;
import com.shiyi.dto.ArticleDTO;
import com.shiyi.entity.Article;
import com.shiyi.entity.Category;
import com.shiyi.entity.Comment;
import com.shiyi.entity.Tags;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.ArticleMapper;
import com.shiyi.mapper.CategoryMapper;
import com.shiyi.mapper.CommentMapper;
import com.shiyi.mapper.TagsMapper;
import com.shiyi.service.RedisService;
import com.shiyi.service.SystemConfigService;
import com.shiyi.strategy.context.SearchStrategyContext;
import com.shiyi.utils.ElasticsearchUtil;
import com.shiyi.utils.IpUtil;
import com.shiyi.utils.PageUtils;
import com.shiyi.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Date;

import static com.shiyi.common.RedisConstants.ARTICLE_LIKE_COUNT;
import static com.shiyi.common.RedisConstants.CHECK_CODE_IP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ArticleServiceImpl 单元测试
 * 使用JaCoCo进行代码覆盖率测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("文章服务实现类测试")
@SuppressWarnings("unchecked")
class ArticleServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private SystemConfigService systemConfigService;

    @Mock
    private RedisService redisService;

    @Mock
    private TagsMapper tagsMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private SearchStrategyContext searchStrategyContext;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ElasticsearchUtil elasticsearchUtil;

    @Mock
    private ArticleMapper articleMapper;

    private ArticleServiceImpl articleService;

    @BeforeEach
    void setUp() throws Exception {
        // 重置所有Mock
        reset(categoryMapper, systemConfigService, redisService, tagsMapper,
              commentMapper, searchStrategyContext, restTemplate, request,
              elasticsearchUtil, articleMapper);

        // 创建Service实例
        articleService = new ArticleServiceImpl(
                categoryMapper,
                systemConfigService,
                redisService,
                tagsMapper,
                commentMapper,
                searchStrategyContext,
                restTemplate,
                request,
                elasticsearchUtil
        );

        // 使用反射设置baseMapper
        Field baseMapperField = articleService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(articleService, articleMapper);

        // 使用反射设置baiduUrl
        Field baiduUrlField = articleService.getClass().getDeclaredField("baiduUrl");
        baiduUrlField.setAccessible(true);
        baiduUrlField.set(articleService, "https://data.zz.baidu.com/urls");

        // 设置PageUtils的默认值
        PageUtils.setCurrentPage(new Page<>(1L, 10L));
    }

    // ==================== listArticle 方法测试 ====================

    @Test
    @DisplayName("测试后台获取所有文章-成功场景")
    void testListArticle_Success() {
        // 准备测试数据
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 10);

        Page<SystemArticleListVO> page = new Page<>(1, 10);
        List<SystemArticleListVO> records = new ArrayList<>();
        SystemArticleListVO article = new SystemArticleListVO();
        article.setId(1L);
        article.setTitle("测试文章");
        records.add(article);
        page.setRecords(records);
        page.setTotal(1L);

        // 模拟Mapper返回
        when(articleMapper.selectArticle(any(Page.class), eq(map)))
                .thenReturn(page);

        // 执行测试
        ResponseResult result = articleService.listArticle(map);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(articleMapper, times(1)).selectArticle(any(Page.class), eq(map));
    }

    // ==================== getArticleById 方法测试 ====================

    @Test
    @DisplayName("测试后台获取文章详情-成功场景")
    void testGetArticleById_Success() {
        // 准备测试数据
        Long id = 1L;
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(id);
        articleDTO.setTitle("测试文章");

        List<String> tags = Arrays.asList("Java", "Spring Boot");

        // 模拟Mapper返回
        when(articleMapper.selectPrimaryKey(id))
                .thenReturn(articleDTO);
        when(tagsMapper.selectByArticleId(id))
                .thenReturn(tags);

        // 执行测试
        ResponseResult result = articleService.getArticleById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        ArticleDTO resultDTO = (ArticleDTO) result.getData();
        assertEquals(id, resultDTO.getId(), "文章ID应匹配");
        assertEquals(tags, resultDTO.getTags(), "标签应匹配");
        verify(articleMapper, times(1)).selectPrimaryKey(id);
        verify(tagsMapper, times(1)).selectByArticleId(id);
    }

    // ==================== insertArticle 方法测试 ====================

    @Test
    @DisplayName("测试添加文章-成功场景")
    void testInsertArticle_Success() {
        // 准备测试数据
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTitle("新文章");
        articleDTO.setContent("文章内容");
        articleDTO.setCategoryName("技术");
        articleDTO.setTags(Arrays.asList("Java", "Spring"));

        Category category = Category.builder()
                .id(1L)
                .name("技术")
                .build();

        Tags tag1 = Tags.builder().id(1L).name("Java").build();
        Tags tag2 = Tags.builder().id(2L).name("Spring").build();

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            // 模拟依赖返回
            when(categoryMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(category);
            when(tagsMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(tag1, tag2);
            when(articleMapper.insert(any(Article.class)))
                    .thenAnswer(invocation -> {
                        Article a = invocation.getArgument(0);
                        a.setId(1L);
                        return 1;
                    });
            when(tagsMapper.selectByArticleId(1L))
                    .thenReturn(Arrays.asList("Java", "Spring"));

            // 执行测试
            ResponseResult result = articleService.insertArticle(articleDTO);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "返回数据不应为null");
            verify(articleMapper, times(1)).insert(any(Article.class));
            verify(tagsMapper, times(1)).saveArticleTags(eq(1L), anyList());
        }
    }

    @Test
    @DisplayName("测试添加文章-分类不存在时自动创建")
    void testInsertArticle_CategoryNotExists() {
        // 准备测试数据
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTitle("新文章");
        articleDTO.setCategoryName("新分类");
        articleDTO.setTags(Arrays.asList("Java"));

        Category newCategory = Category.builder()
                .id(2L)
                .name("新分类")
                .build();

        Tags tag = Tags.builder().id(1L).name("Java").build();

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            // 模拟依赖返回
            when(categoryMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(null); // 分类不存在
            when(categoryMapper.insert(any(Category.class)))
                    .thenAnswer(invocation -> {
                        Category c = invocation.getArgument(0);
                        c.setId(2L);
                        return 1;
                    });
            when(tagsMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(tag);
            when(articleMapper.insert(any(Article.class)))
                    .thenAnswer(invocation -> {
                        Article a = invocation.getArgument(0);
                        a.setId(1L);
                        return 1;
                    });
            when(tagsMapper.selectByArticleId(1L))
                    .thenReturn(Arrays.asList("Java"));

            // 执行测试
            ResponseResult result = articleService.insertArticle(articleDTO);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            verify(categoryMapper, times(1)).insert(any(Category.class));
        }
    }

    // ==================== updateArticle 方法测试 ====================

    @Test
    @DisplayName("测试修改文章-成功场景")
    void testUpdateArticle_Success() {
        // 准备测试数据
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(1L);
        articleDTO.setTitle("修改后的文章");
        articleDTO.setCategoryName("技术");
        articleDTO.setTags(Arrays.asList("Java", "Spring"));

        Article existingArticle = Article.builder()
                .id(1L)
                .title("原文章")
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("技术")
                .build();

        Tags tag1 = Tags.builder().id(1L).name("Java").build();
        Tags tag2 = Tags.builder().id(2L).name("Spring").build();

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            // 模拟依赖返回
            when(articleMapper.selectById(1L))
                    .thenReturn(existingArticle);
            when(categoryMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(category);
            when(tagsMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(tag1, tag2);
            when(articleMapper.updateById(any(Article.class)))
                    .thenReturn(1);

            // 执行测试
            ResponseResult result = articleService.updateArticle(articleDTO);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            verify(articleMapper, times(1)).selectById(1L);
            verify(articleMapper, times(1)).updateById(any(Article.class));
            verify(tagsMapper, times(1)).deleteByArticleIds(anyList());
            verify(tagsMapper, times(1)).saveArticleTags(eq(1L), anyList());
        }
    }

    @Test
    @DisplayName("测试修改文章-文章不存在")
    void testUpdateArticle_ArticleNotFound() {
        // 准备测试数据
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(999L);
        articleDTO.setTitle("不存在的文章");

        // 模拟文章不存在
        when(articleMapper.selectById(999L))
                .thenReturn(null);

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            articleService.updateArticle(articleDTO);
        }, "文章不存在时应抛出BusinessException");

        verify(articleMapper, times(1)).selectById(999L);
        verify(articleMapper, never()).updateById(any(Article.class));
    }

    // ==================== deleteArticle 方法测试 ====================

    @Test
    @DisplayName("测试删除文章-成功场景")
    void testDeleteArticle_Success() {
        // 准备测试数据
        Long id = 1L;

        // 模拟依赖返回
        when(articleMapper.deleteById(id))
                .thenReturn(1);
        doNothing().when(elasticsearchUtil).delete(anyList());

        // 执行测试
        ResponseResult result = articleService.deleteArticle(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleMapper, times(1)).deleteById(id);
        verify(tagsMapper, times(1)).deleteByArticleIds(anyList());
        verify(elasticsearchUtil, times(1)).delete(anyList());
    }

    @Test
    @DisplayName("测试删除文章-删除失败")
    void testDeleteArticle_Failure() {
        // 准备测试数据
        Long id = 999L;

        // 模拟删除失败
        when(articleMapper.deleteById(id))
                .thenReturn(0);

        // 执行测试
        ResponseResult result = articleService.deleteArticle(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleMapper, times(1)).deleteById(id);
        verify(tagsMapper, never()).deleteByArticleIds(anyList());
        verify(elasticsearchUtil, never()).delete(anyList());
    }

    // ==================== deleteBatchArticle 方法测试 ====================

    @Test
    @DisplayName("测试批量删除文章-成功场景")
    void testDeleteBatchArticle_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // 模拟依赖返回
        when(articleMapper.deleteBatchIds(ids))
                .thenReturn(3);
        doNothing().when(elasticsearchUtil).delete(anyList());

        // 执行测试
        ResponseResult result = articleService.deleteBatchArticle(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleMapper, times(1)).deleteBatchIds(ids);
        verify(tagsMapper, times(1)).deleteByArticleIds(ids);
        verify(elasticsearchUtil, times(1)).delete(ids);
    }

    // ==================== putTopArticle 方法测试 ====================

    @Test
    @DisplayName("测试置顶文章-成功场景")
    void testPutTopArticle_Success() {
        // 准备测试数据
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(1L);

        // 模拟依赖返回
        doNothing().when(articleMapper).putTopArticle(articleDTO);

        // 执行测试
        ResponseResult result = articleService.putTopArticle(articleDTO);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleMapper, times(1)).putTopArticle(articleDTO);
    }

    // ==================== publishAndShelf 方法测试 ====================

    @Test
    @DisplayName("测试发布或下架文章-成功场景")
    void testPublishAndShelf_Success() {
        // 准备测试数据
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(1L);

        // 模拟依赖返回
        doNothing().when(articleMapper).publishAndShelf(articleDTO);

        // 执行测试
        ResponseResult result = articleService.publishAndShelf(articleDTO);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleMapper, times(1)).publishAndShelf(articleDTO);
    }

    // ==================== articleSeo 方法测试 ====================

    @Test
    @DisplayName("测试文章百度推送-成功场景")
    void testArticleSeo_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L);

        // 模拟RestTemplate返回
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn("success");

        // 执行测试
        ResponseResult result = articleService.articleSeo(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(restTemplate, times(2)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }

    // ==================== randomImg 方法测试 ====================

    @Test
    @DisplayName("测试随机获取图片-成功场景")
    void testRandomImg_Success() {
        // 准备测试数据
        String jsonResponse = "{\"imgurl\":\"https://example.com/image.jpg\"}";

        // 模拟RestTemplate返回
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(jsonResponse);

        // 执行测试
        ResponseResult result = articleService.randomImg();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    // ==================== selectPublicArticleList 方法测试 ====================

    @Test
    @DisplayName("测试获取文章列表-成功场景（无参数）")
    void testSelectPublicArticleList_Success_NoParams() {
        // 准备测试数据
        Page<ApiArticleListVO> page = new Page<>(1L, 10L);
        List<ApiArticleListVO> records = new ArrayList<>();
        ApiArticleListVO article = new ApiArticleListVO();
        article.setId(1L);
        article.setTitle("测试文章");
        records.add(article);
        page.setRecords(records);
        page.setTotal(1L);

        List<Tags> tags = Arrays.asList(
                Tags.builder().id(1L).name("Java").build()
        );

        Map<String, Object> likeCountMap = new HashMap<>();
        likeCountMap.put("1", 10);

        // 模拟依赖返回
        when(articleMapper.selectArticleList(any(Page.class), isNull(), isNull()))
                .thenReturn(page);
        when(tagsMapper.selectTagByArticleId(1L))
                .thenReturn(tags);
        when(commentMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(5);
        when(redisService.getCacheMap(ARTICLE_LIKE_COUNT))
                .thenReturn(likeCountMap);

        // 执行测试
        ResponseResult result = articleService.selectPublicArticleList(null, null);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(articleMapper, times(1)).selectArticleList(any(Page.class), isNull(), isNull());
    }

    @Test
    @DisplayName("测试获取文章列表-成功场景（带分类ID）")
    void testSelectPublicArticleList_Success_WithCategoryId() {
        // 准备测试数据
        Integer categoryId = 1;
        Page<ApiArticleListVO> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());
        page.setTotal(0L);

        // 模拟依赖返回
        when(articleMapper.selectArticleList(any(Page.class), eq(categoryId), isNull()))
                .thenReturn(page);
        // 使用lenient()避免UnnecessaryStubbing，因为当records为空时，forEach不会执行
        lenient().when(redisService.getCacheMap(ARTICLE_LIKE_COUNT))
                .thenReturn(new HashMap<>());

        // 执行测试
        ResponseResult result = articleService.selectPublicArticleList(categoryId, null);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(articleMapper, times(1)).selectArticleList(any(Page.class), eq(categoryId), isNull());
    }

    // ==================== selectUserArticleList 方法测试 ====================

    @Test
    @DisplayName("测试获取当前用户文章列表-成功场景")
    void testSelectUserArticleList_Success() {
        // 准备测试数据
        Page<ApiArticleListVO> page = new Page<>(1L, 10L);
        List<ApiArticleListVO> records = new ArrayList<>();
        ApiArticleListVO article = new ApiArticleListVO();
        article.setId(1L);
        records.add(article);
        page.setRecords(records);

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            // 模拟依赖返回
            when(articleMapper.selectUserArticleList(any(Page.class), eq(1L), isNull(), isNull()))
                    .thenReturn(page);
            when(tagsMapper.selectTagByArticleId(1L))
                    .thenReturn(new ArrayList<>());
            when(commentMapper.selectCount(any(LambdaQueryWrapper.class)))
                    .thenReturn(0);
            when(redisService.getCacheMap(ARTICLE_LIKE_COUNT))
                    .thenReturn(new HashMap<>());

            // 执行测试
            ResponseResult result = articleService.selectUserArticleList(null, null);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            verify(articleMapper, times(1)).selectUserArticleList(any(Page.class), eq(1L), isNull(), isNull());
        }
    }

    // ==================== selectPublicArticleInfo 方法测试 ====================

    @Test
    @DisplayName("测试获取文章详情-成功场景（未登录）")
    void testSelectPublicArticleInfo_Success_NotLoggedIn() {
        // 准备测试数据
        Integer id = 1;
        ApiArticleInfoVO articleInfo = new ApiArticleInfoVO();
        articleInfo.setId(id.longValue());
        articleInfo.setTitle("测试文章");

        List<Tags> tags = Arrays.asList(
                Tags.builder().id(1L).name("Java").build()
        );

        List<Comment> comments = Arrays.asList(
                new Comment(), new Comment()
        );

        Map<String, Object> likeCountMap = new HashMap<>();
        likeCountMap.put("1", 5);

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdDefaultNull).thenReturn(null);

            // 模拟依赖返回
            when(articleMapper.selectArticleByIdToVO(id))
                    .thenReturn(articleInfo);
        when(tagsMapper.selectTagByArticleId(id.longValue()))
                .thenReturn(tags);
            when(commentMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(comments);
            when(redisService.getCacheMap(ARTICLE_LIKE_COUNT))
                    .thenReturn(likeCountMap);

            // 执行测试
            ResponseResult result = articleService.selectPublicArticleInfo(id);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "返回数据不应为null");
            ApiArticleInfoVO resultVO = (ApiArticleInfoVO) result.getData();
            assertEquals(2, resultVO.getCommentCount(), "评论数应匹配");
            assertEquals(5, resultVO.getLikeCount(), "点赞数应匹配");
            assertFalse(resultVO.getIsLike(), "未登录时不应点赞");
        }
    }

    @Test
    @DisplayName("测试获取文章详情-成功场景（已登录且已点赞）")
    void testSelectPublicArticleInfo_Success_LoggedInAndLiked() {
        // 准备测试数据
        Integer id = 1;
        ApiArticleInfoVO articleInfo = new ApiArticleInfoVO();
        articleInfo.setId(id.longValue());

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdDefaultNull).thenReturn(1);
            stpUtilMock.when(StpUtil::getLoginId).thenReturn(1);

            // 模拟依赖返回
            when(articleMapper.selectArticleByIdToVO(id))
                    .thenReturn(articleInfo);
            when(tagsMapper.selectTagByArticleId(id.longValue()))
                    .thenReturn(new ArrayList<>());
            when(commentMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(new ArrayList<>());
            when(redisService.getCacheMap(ARTICLE_LIKE_COUNT))
                    .thenReturn(new HashMap<>());
            when(redisService.sIsMember(anyString(), eq(id)))
                    .thenReturn(true);

            // 执行测试
            ResponseResult result = articleService.selectPublicArticleInfo(id);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            ApiArticleInfoVO resultVO = (ApiArticleInfoVO) result.getData();
            assertTrue(resultVO.getIsLike(), "已点赞时应为true");
        }
    }

    // ==================== archive 方法测试 ====================

    @Test
    @DisplayName("测试获取归档-成功场景")
    void testArchive_Success() {
        // 准备测试数据
        List<ApiArchiveVO> articleList = new ArrayList<>();
        ApiArchiveVO article1 = new ApiArchiveVO();
        article1.setId(1L);
        article1.setTitle("文章1");
        article1.setTime("2024-01");
        article1.setFormatTime(new Date(System.currentTimeMillis() - 86400000)); // 昨天
        articleList.add(article1);

        ApiArchiveVO article2 = new ApiArchiveVO();
        article2.setId(2L);
        article2.setTitle("文章2");
        article2.setTime("2024-01");
        article2.setFormatTime(new Date(System.currentTimeMillis())); // 今天
        articleList.add(article2);

        // 模拟依赖返回
        when(articleMapper.selectListArchive())
                .thenReturn(articleList);

        // 执行测试
        ResponseResult result = articleService.archive();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        assertNotNull(result.getExtra(), "Extra不应为null");
        assertEquals(2, result.getExtra().get("total"), "总数应匹配");
        verify(articleMapper, times(1)).selectListArchive();
    }

    @Test
    @DisplayName("测试获取归档-空列表")
    void testArchive_EmptyList() {
        // 模拟依赖返回
        when(articleMapper.selectListArchive())
                .thenReturn(new ArrayList<>());

        // 执行测试
        ResponseResult result = articleService.archive();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals(0, result.getExtra().get("total"), "总数应为0");
    }

    // ==================== publicSearchArticle 方法测试 ====================

    @Test
    @DisplayName("测试搜索文章-成功场景")
    void testPublicSearchArticle_Success() {
        // 准备测试数据
        String keywords = "Spring Boot";
        Page<ApiArticleListVO> page = new Page<>(1L, 10L);
        List<ApiArticleListVO> records = new ArrayList<>();
        ApiArticleListVO article = new ApiArticleListVO();
        article.setId(1L);
        article.setTitle("Spring Boot教程");
        records.add(article);
        page.setRecords(records);

        // 模拟依赖返回
        when(articleMapper.publicPageSearchArticle(any(Page.class), eq(keywords)))
                .thenReturn(page);
        when(tagsMapper.selectTagByArticleId(1L))
                .thenReturn(new ArrayList<>());

        // 执行测试
        ResponseResult result = articleService.publicSearchArticle(keywords);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(articleMapper, times(1)).publicPageSearchArticle(any(Page.class), eq(keywords));
    }

    // ==================== articleLike 方法测试 ====================

    @Test
    @DisplayName("测试文章点赞-成功场景（未点赞，执行点赞）")
    void testArticleLike_Success_AddLike() {
        // 准备测试数据
        Integer articleId = 1;

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginId).thenReturn(1);

            // 模拟依赖返回
            when(redisService.sIsMember(anyString(), eq(articleId)))
                    .thenReturn(false); // 未点赞
            when(redisService.sAdd(anyString(), eq(articleId)))
                    .thenReturn(1L);
            when(redisService.hIncr(anyString(), anyString(), eq(1L)))
                    .thenReturn(2L);

            // 执行测试
            ResponseResult result = articleService.articleLike(articleId);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            verify(redisService, times(1)).sIsMember(anyString(), eq(articleId));
            verify(redisService, times(1)).sAdd(anyString(), eq(articleId));
            verify(redisService, times(1)).hIncr(anyString(), anyString(), eq(1L));
            verify(redisService, never()).sRemove(anyString(), any());
            verify(redisService, never()).hDecr(anyString(), anyString(), anyLong());
        }
    }

    @Test
    @DisplayName("测试文章点赞-成功场景（已点赞，执行取消点赞）")
    void testArticleLike_Success_RemoveLike() {
        // 准备测试数据
        Integer articleId = 1;

        // 使用MockedStatic模拟StpUtil
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginId).thenReturn(1);

            // 模拟依赖返回
            when(redisService.sIsMember(anyString(), eq(articleId)))
                    .thenReturn(true); // 已点赞
            when(redisService.sRemove(anyString(), eq(articleId)))
                    .thenReturn(1L);
            when(redisService.hDecr(anyString(), anyString(), eq(1L)))
                    .thenReturn(1L);

            // 执行测试
            ResponseResult result = articleService.articleLike(articleId);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            verify(redisService, times(1)).sIsMember(anyString(), eq(articleId));
            verify(redisService, times(1)).sRemove(anyString(), eq(articleId));
            verify(redisService, times(1)).hDecr(anyString(), anyString(), eq(1L));
            verify(redisService, never()).sAdd(anyString(), any());
            verify(redisService, never()).hIncr(anyString(), anyString(), anyLong());
        }
    }

    // ==================== checkSecret 方法测试 ====================

    @Test
    @DisplayName("测试校验文章验证码-成功场景")
    void testCheckSecret_Success() {
        // 准备测试数据
        String code = "test-code-123";
        String ip = "192.168.1.1";

        // 模拟依赖返回
        when(redisService.getCacheObject(RedisConstants.WECHAT_CODE + code))
                .thenReturn("test-code-123");
        when(redisService.getCacheList(CHECK_CODE_IP))
                .thenReturn(new ArrayList<>());
        when(IpUtil.getIp(request))
                .thenReturn(ip);

        // 执行测试
        ResponseResult result = articleService.checkSecret(code);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
        assertEquals("验证成功", result.getData(), "数据应匹配");
        verify(redisService, times(1)).getCacheObject(RedisConstants.WECHAT_CODE + code);
        verify(redisService, times(1)).setCacheList(eq(CHECK_CODE_IP), anyList());
        verify(redisService, times(1)).deleteObject(RedisConstants.WECHAT_CODE + code);
    }

    @Test
    @DisplayName("测试校验文章验证码-验证码不存在")
    void testCheckSecret_CodeNotFound() {
        // 准备测试数据
        String code = "invalid-code";

        // 模拟验证码不存在
        when(redisService.getCacheObject(RedisConstants.WECHAT_CODE + code))
                .thenReturn(null);

        // 执行测试并验证抛出异常
        assertThrows(BusinessException.class, () -> {
            articleService.checkSecret(code);
        }, "验证码不存在时应抛出BusinessException");

        verify(redisService, times(1)).getCacheObject(RedisConstants.WECHAT_CODE + code);
        verify(redisService, never()).setCacheList(anyString(), anyList());
    }

    @Test
    @DisplayName("测试校验文章验证码-IP已存在列表中")
    void testCheckSecret_IpAlreadyExists() {
        // 准备测试数据
        String code = "test-code-123";
        String ip = "192.168.1.1";
        List<Object> existingList = new ArrayList<>();
        existingList.add(ip); // 使用ArrayList而不是Arrays.asList，因为Arrays.asList返回的是不可变列表

        // 模拟依赖返回
        when(redisService.getCacheObject(RedisConstants.WECHAT_CODE + code))
                .thenReturn("test-code-123");
        when(redisService.getCacheList(CHECK_CODE_IP))
                .thenReturn(existingList);
        when(IpUtil.getIp(request))
                .thenReturn(ip);

        // 执行测试
        ResponseResult result = articleService.checkSecret(code);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(redisService, times(1)).setCacheList(eq(CHECK_CODE_IP), anyList());
    }
}

