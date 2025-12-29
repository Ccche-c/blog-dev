package com.shiyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Category;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.CategoryMapper;
import com.shiyi.utils.DateUtil;
import com.shiyi.utils.PageUtils;
import com.shiyi.vo.ApiCategoryListVO;
import com.shiyi.vo.SystemCategoryListVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.shiyi.common.ResultCode.CATEGORY_IS_EXIST;
import static com.shiyi.common.ResultCode.CATEGORY_IS_TOP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CategoryServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("分类服务实现类测试")
@SuppressWarnings({"rawtypes", "unchecked"})
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        // 使用反射注入 baseMapper
        ReflectionTestUtils.setField(categoryService, "baseMapper", categoryMapper);
    }

    // ==================== listCategory 方法测试 ====================

    @Test
    @DisplayName("测试分类列表-成功")
    void testListCategory_Success() {
        // 准备测试数据
        String name = "test";
        Page<SystemCategoryListVO> page = new Page<>(1L, 10L);
        List<SystemCategoryListVO> records = new ArrayList<>();
        SystemCategoryListVO vo = new SystemCategoryListVO();
        vo.setId(1L);
        vo.setName("test category");
        records.add(vo);
        page.setRecords(records);

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(categoryMapper.selectCategory(any(Page.class), eq(name))).thenReturn(page);

            // 执行测试
            ResponseResult result = categoryService.listCategory(name);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "数据不应为null");

            // 验证调用
            verify(categoryMapper, times(1)).selectCategory(any(Page.class), eq(name));
        }
    }

    @Test
    @DisplayName("测试分类列表-无名称")
    void testListCategory_NoName() {
        // 准备测试数据
        String name = null;
        Page<SystemCategoryListVO> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(categoryMapper.selectCategory(any(Page.class), eq(name))).thenReturn(page);

            // 执行测试
            ResponseResult result = categoryService.listCategory(name);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(categoryMapper, times(1)).selectCategory(any(Page.class), eq(name));
        }
    }

    // ==================== getCategoryById 方法测试 ====================

    @Test
    @DisplayName("测试分类详情-成功")
    void testGetCategoryById_Success() {
        // 准备测试数据
        Long id = 1L;
        Category category = Category.builder()
                .id(id)
                .name("test category")
                .sort(1)
                .build();

        // Mock
        when(categoryMapper.selectById(id)).thenReturn(category);

        // 执行测试
        ResponseResult result = categoryService.getCategoryById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals(category, result.getData(), "分类数据应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).selectById(id);
    }

    @Test
    @DisplayName("测试分类详情-不存在")
    void testGetCategoryById_NotExists() {
        // 准备测试数据
        Long id = 999L;

        // Mock
        when(categoryMapper.selectById(id)).thenReturn(null);

        // 执行测试
        ResponseResult result = categoryService.getCategoryById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNull(result.getData(), "数据应为null");

        // 验证调用
        verify(categoryMapper, times(1)).selectById(id);
    }

    // ==================== insertCategory 方法测试 ====================

    @Test
    @DisplayName("测试添加分类-成功")
    void testInsertCategory_Success() {
        // 准备测试数据
        Category category = Category.builder()
                .name("new category")
                .sort(1)
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(categoryMapper.insert(category)).thenReturn(1);

        // 执行测试
        ResponseResult result = categoryService.insertCategory(category);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, times(1)).insert(category);
    }

    @Test
    @DisplayName("测试添加分类-分类已存在")
    void testInsertCategory_CategoryExists() {
        // 准备测试数据
        Category category = Category.builder()
                .name("existing category")
                .sort(1)
                .build();

        Category existingCategory = Category.builder()
                .id(1L)
                .name("existing category")
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingCategory);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.insertCategory(category);
        });

        assertEquals(CATEGORY_IS_EXIST.getCode(), exception.getCode(), "异常码应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, never()).insert(any(Category.class));
    }

    @Test
    @DisplayName("测试添加分类-插入失败")
    void testInsertCategory_InsertFailed() {
        // 准备测试数据
        Category category = Category.builder()
                .name("new category")
                .sort(1)
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(categoryMapper.insert(category)).thenReturn(0);

        // 执行测试
        ResponseResult result = categoryService.insertCategory(category);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("添加分类失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, times(1)).insert(category);
    }

    // ==================== updateCategory 方法测试 ====================

    @Test
    @DisplayName("测试修改分类-成功")
    void testUpdateCategory_Success() {
        // 准备测试数据
        Category category = Category.builder()
                .id(1L)
                .name("updated category")
                .sort(1)
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(categoryMapper.updateById(category)).thenReturn(1);

        // 执行测试
        ResponseResult result = categoryService.updateCategory(category);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, times(1)).updateById(category);
    }

    @Test
    @DisplayName("测试修改分类-名称相同（同一分类）")
    void testUpdateCategory_SameNameSameId() {
        // 准备测试数据
        Category category = Category.builder()
                .id(1L)
                .name("category")
                .sort(1)
                .build();

        Category existingCategory = Category.builder()
                .id(1L)
                .name("category")
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingCategory);
        when(categoryMapper.updateById(category)).thenReturn(1);

        // 执行测试
        ResponseResult result = categoryService.updateCategory(category);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, times(1)).updateById(category);
    }

    @Test
    @DisplayName("测试修改分类-分类名称已存在（不同ID）")
    void testUpdateCategory_CategoryExists() {
        // 准备测试数据
        Category category = Category.builder()
                .id(1L)
                .name("existing category")
                .sort(1)
                .build();

        Category existingCategory = Category.builder()
                .id(2L)
                .name("existing category")
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingCategory);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.updateCategory(category);
        });

        assertEquals(CATEGORY_IS_EXIST.getCode(), exception.getCode(), "异常码应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, never()).updateById(any(Category.class));
    }

    @Test
    @DisplayName("测试修改分类-更新失败")
    void testUpdateCategory_UpdateFailed() {
        // 准备测试数据
        Category category = Category.builder()
                .id(1L)
                .name("updated category")
                .sort(1)
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(categoryMapper.updateById(category)).thenReturn(0);

        // 执行测试
        ResponseResult result = categoryService.updateCategory(category);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("修改分类失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, times(1)).updateById(category);
    }

    // ==================== deleteCategory 方法测试 ====================

    @Test
    @DisplayName("测试删除分类-成功")
    void testDeleteCategory_Success() {
        // 准备测试数据
        Long id = 1L;

        // Mock
        when(categoryMapper.deleteById(id)).thenReturn(1);

        // 执行测试
        ResponseResult result = categoryService.deleteCategory(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(categoryMapper, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("测试删除分类-删除失败")
    void testDeleteCategory_DeleteFailed() {
        // 准备测试数据
        Long id = 1L;

        // Mock
        when(categoryMapper.deleteById(id)).thenReturn(0);

        // 执行测试
        ResponseResult result = categoryService.deleteCategory(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("删除分类失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).deleteById(id);
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除分类-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Category> list = Arrays.asList(
                Category.builder().id(1L).build(),
                Category.builder().id(2L).build(),
                Category.builder().id(3L).build()
        );

        // Mock
        when(categoryMapper.deleteBatchIds(anyList())).thenReturn(3);

        // 执行测试
        ResponseResult result = categoryService.deleteBatch(list);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(categoryMapper, times(1)).deleteBatchIds(anyList());
    }

    @Test
    @DisplayName("测试批量删除分类-删除失败")
    void testDeleteBatch_DeleteFailed() {
        // 准备测试数据
        List<Category> list = Arrays.asList(
                Category.builder().id(1L).build(),
                Category.builder().id(2L).build()
        );

        // Mock
        when(categoryMapper.deleteBatchIds(anyList())).thenReturn(0);

        // 执行测试
        ResponseResult result = categoryService.deleteBatch(list);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("批量删除分类失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).deleteBatchIds(anyList());
    }

    // ==================== top 方法测试 ====================

    @Test
    @DisplayName("测试置顶分类-成功")
    void testTop_Success() {
        // 准备测试数据
        Long id = 2L;
        Category topCategory = Category.builder()
                .id(1L)
                .sort(10)
                .build();

        // Mock
        try (MockedStatic<DateUtil> dateUtilMock = mockStatic(DateUtil.class)) {
            Date nowDate = new Date();
            dateUtilMock.when(DateUtil::getNowDate).thenReturn(nowDate);
            
            when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(topCategory);
            when(categoryMapper.updateById(any(Category.class))).thenReturn(1);

            // 执行测试
            ResponseResult result = categoryService.top(id);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
            verify(categoryMapper, times(1)).updateById(any(Category.class));
        }
    }

    @Test
    @DisplayName("测试置顶分类-已在顶端")
    void testTop_AlreadyTop() {
        // 准备测试数据
        Long id = 1L;
        Category topCategory = Category.builder()
                .id(1L)
                .sort(10)
                .build();

        // Mock
        when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(topCategory);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.top(id);
        });

        assertEquals(CATEGORY_IS_TOP.getCode(), exception.getCode(), "异常码应匹配");

        // 验证调用
        verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(categoryMapper, never()).updateById(any(Category.class));
    }

    @Test
    @DisplayName("测试置顶分类-置顶失败")
    void testTop_UpdateFailed() {
        // 准备测试数据
        Long id = 2L;
        Category topCategory = Category.builder()
                .id(1L)
                .sort(10)
                .build();

        // Mock
        try (MockedStatic<DateUtil> dateUtilMock = mockStatic(DateUtil.class)) {
            Date nowDate = new Date();
            dateUtilMock.when(DateUtil::getNowDate).thenReturn(nowDate);
            
            when(categoryMapper.selectOne(any(QueryWrapper.class))).thenReturn(topCategory);
            when(categoryMapper.updateById(any(Category.class))).thenReturn(0);

            // 执行测试
            ResponseResult result = categoryService.top(id);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertNotEquals(200, result.getCode(), "响应码不应为200");
            assertEquals("置顶失败", result.getMessage(), "错误消息应匹配");

            // 验证调用
            verify(categoryMapper, times(1)).selectOne(any(QueryWrapper.class));
            verify(categoryMapper, times(1)).updateById(any(Category.class));
        }
    }

    // ==================== selectPublicCategoryList 方法测试 ====================

    @Test
    @DisplayName("测试首页分类列表-成功")
    void testSelectPublicCategoryList_Success() {
        // 准备测试数据
        List<ApiCategoryListVO> list = new ArrayList<>();
        ApiCategoryListVO vo = new ApiCategoryListVO();
        vo.setId(1);
        vo.setName("category");
        vo.setArticleCount(10);
        list.add(vo);

        // Mock
        when(categoryMapper.selectApitCategoryList()).thenReturn(list);

        // 执行测试
        ResponseResult result = categoryService.selectPublicCategoryList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "数据不应为null");

        // 验证调用
        verify(categoryMapper, times(1)).selectApitCategoryList();
    }

    @Test
    @DisplayName("测试首页分类列表-空列表")
    void testSelectPublicCategoryList_EmptyList() {
        // 准备测试数据
        List<ApiCategoryListVO> list = new ArrayList<>();

        // Mock
        when(categoryMapper.selectApitCategoryList()).thenReturn(list);

        // 执行测试
        ResponseResult result = categoryService.selectPublicCategoryList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "数据不应为null");

        // 验证调用
        verify(categoryMapper, times(1)).selectApitCategoryList();
    }
}

