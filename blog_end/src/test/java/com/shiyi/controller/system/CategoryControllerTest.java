package com.shiyi.controller.system;

import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Category;
import com.shiyi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * CategoryController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("系统分类管理接口测试")
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试获取分类列表-成功")
    void testList_Success() {
        // 准备测试数据
        String name = "测试分类";
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(categoryService.listCategory(name))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = categoryController.list(name);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).listCategory(name);
    }

    @Test
    @DisplayName("测试获取分类详情-成功")
    void testGetCategoryById_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(categoryService.getCategoryById(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = categoryController.getCategoryById(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).getCategoryById(id);
    }

    @Test
    @DisplayName("测试新增分类-成功")
    void testInsertCategory_Success() {
        // 准备测试数据
        Category category = new Category();
        category.setName("新分类");
        
        ResponseResult expectedResult = ResponseResult.success("新增成功");

        // 模拟Service返回
        when(categoryService.insertCategory(any(Category.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = categoryController.insertCategory(category);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).insertCategory(any(Category.class));
    }

    @Test
    @DisplayName("测试修改分类-成功")
    void testUpdate_Success() {
        // 准备测试数据
        Category category = new Category();
        category.setId(1L);
        category.setName("修改后的分类");
        
        ResponseResult expectedResult = ResponseResult.success("修改成功");

        // 模拟Service返回
        when(categoryService.updateCategory(any(Category.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = categoryController.update(category);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).updateCategory(any(Category.class));
    }

    @Test
    @DisplayName("测试删除分类-成功")
    void testDeleteCategory_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("删除成功");

        // 模拟Service返回
        when(categoryService.deleteCategory(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = categoryController.deleteCategory(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).deleteCategory(id);
    }

    @Test
    @DisplayName("测试批量删除分类-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Category> categories = Arrays.asList(new Category(), new Category());
        ResponseResult expectedResult = ResponseResult.success("批量删除成功");

        // 模拟Service返回
        when(categoryService.deleteBatch(anyList()))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = categoryController.deleteBatch(categories);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).deleteBatch(anyList());
    }

    @Test
    @DisplayName("测试置顶分类-成功")
    void testTop_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("置顶成功");

        // 模拟Service返回
        when(categoryService.top(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = categoryController.top(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).top(id);
    }
}

