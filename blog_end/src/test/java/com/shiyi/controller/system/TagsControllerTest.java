package com.shiyi.controller.system;

import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Tags;
import com.shiyi.service.TagsService;
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
 * TagsController 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("系统标签管理接口测试")
class TagsControllerTest {

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private TagsController tagsController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
    }

    @Test
    @DisplayName("测试获取标签列表-成功")
    void testList_Success() {
        // 准备测试数据
        String name = "测试标签";
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(tagsService.listTags(name))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = tagsController.list(name);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tagsService, times(1)).listTags(name);
    }

    @Test
    @DisplayName("测试新增标签-成功")
    void testInsert_Success() {
        // 准备测试数据
        Tags tags = new Tags();
        tags.setName("新标签");
        
        ResponseResult expectedResult = ResponseResult.success("新增成功");

        // 模拟Service返回
        when(tagsService.insertTag(any(Tags.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = tagsController.insert(tags);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tagsService, times(1)).insertTag(any(Tags.class));
    }

    @Test
    @DisplayName("测试获取标签详情-成功")
    void testGetTagsById_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(tagsService.getTagsById(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = tagsController.getTagsById(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tagsService, times(1)).getTagsById(id);
    }

    @Test
    @DisplayName("测试修改标签-成功")
    void testUpdate_Success() {
        // 准备测试数据
        Tags tags = new Tags();
        tags.setId(1L);
        tags.setName("修改后的标签");
        
        ResponseResult expectedResult = ResponseResult.success("修改成功");

        // 模拟Service返回
        when(tagsService.updateTag(any(Tags.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = tagsController.update(tags);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tagsService, times(1)).updateTag(any(Tags.class));
    }

    @Test
    @DisplayName("测试删除标签-成功")
    void testDeleteById_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("删除成功");

        // 模拟Service返回
        when(tagsService.deleteById(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = tagsController.deleteById(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tagsService, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("测试批量删除标签-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        ResponseResult expectedResult = ResponseResult.success("批量删除成功");

        // 模拟Service返回
        when(tagsService.deleteBatch(anyList()))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = tagsController.deleteBatch(ids);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tagsService, times(1)).deleteBatch(anyList());
    }

    @Test
    @DisplayName("测试置顶标签-成功")
    void testTop_Success() {
        // 准备测试数据
        Long id = 1L;
        ResponseResult expectedResult = ResponseResult.success("置顶成功");

        // 模拟Service返回
        when(tagsService.top(id))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = tagsController.top(id);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(tagsService, times(1)).top(id);
    }
}

