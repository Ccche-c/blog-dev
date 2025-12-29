package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.service.CategoryService;
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
 * ApiCategoryController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("门户分类管理接口测试")
class ApiCategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ApiCategoryController apiCategoryController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试获取分类列表-成功")
    void testSelectPublicCategoryList_Success() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(categoryService.selectPublicCategoryList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCategoryController.selectPublicCategoryList();

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(categoryService, times(1)).selectPublicCategoryList();
    }
}

