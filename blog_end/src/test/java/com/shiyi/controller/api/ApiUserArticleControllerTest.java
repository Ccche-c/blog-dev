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
import static org.mockito.Mockito.*;

/**
 * ApiUserArticleController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户文章管理接口测试")
class ApiUserArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ApiUserArticleController apiUserArticleController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试获取当前用户文章列表-成功")
    void testSelectUserArticleList_Success() {
        // 准备测试数据
        Integer categoryId = 1;
        Integer tagId = 2;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.selectUserArticleList(categoryId, tagId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserArticleController.selectUserArticleList(categoryId, tagId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).selectUserArticleList(categoryId, tagId);
    }

    @Test
    @DisplayName("测试获取当前用户文章列表-无参数")
    void testSelectUserArticleList_NoParams() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.selectUserArticleList(null, null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserArticleController.selectUserArticleList(null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).selectUserArticleList(null, null);
    }

    @Test
    @DisplayName("测试获取当前用户文章列表-仅分类ID")
    void testSelectUserArticleList_WithCategoryId() {
        // 准备测试数据
        Integer categoryId = 1;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.selectUserArticleList(categoryId, null))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserArticleController.selectUserArticleList(categoryId, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).selectUserArticleList(categoryId, null);
    }

    @Test
    @DisplayName("测试获取当前用户文章列表-仅标签ID")
    void testSelectUserArticleList_WithTagId() {
        // 准备测试数据
        Integer tagId = 2;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(articleService.selectUserArticleList(null, tagId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiUserArticleController.selectUserArticleList(null, tagId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(articleService, times(1)).selectUserArticleList(null, tagId);
    }
}

