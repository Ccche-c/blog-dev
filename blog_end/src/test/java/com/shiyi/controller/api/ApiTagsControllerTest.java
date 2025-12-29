package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.service.TagsService;
import com.shiyi.vo.ApiTagVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ApiTagsController 单元测试
 * 使用JaCoCo进行代码覆盖率测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("门户标签管理接口测试")
class ApiTagsControllerTest {

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private ApiTagsController apiTagsController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
        reset(tagsService);
    }

    // ==================== selectPublicTagList 方法测试 ====================

    @Test
    @DisplayName("测试获取标签列表-成功场景（有数据）")
    void testSelectPublicTagList_Success_WithData() {
        // 准备测试数据
        List<ApiTagVO> tagList = new ArrayList<>();
        ApiTagVO tag1 = new ApiTagVO();
        tag1.setId(1);
        tag1.setName("Java");
        tag1.setArticleCount(10);
        tag1.setAvatar("avatar1.jpg");
        tagList.add(tag1);

        ApiTagVO tag2 = new ApiTagVO();
        tag2.setId(2);
        tag2.setName("Spring Boot");
        tag2.setArticleCount(5);
        tag2.setAvatar("avatar2.jpg");
        tagList.add(tag2);

        ResponseResult expectedResult = ResponseResult.success(tagList);

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        assertTrue(result.getData() instanceof List, "返回数据应为List类型");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-成功场景（空列表）")
    void testSelectPublicTagList_Success_EmptyList() {
        // 准备测试数据（空列表）
        List<ApiTagVO> emptyList = new ArrayList<>();
        ResponseResult expectedResult = ResponseResult.success(emptyList);

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        assertTrue(result.getData() instanceof List, "返回数据应为List类型");
        List<?> dataList = (List<?>) result.getData();
        assertTrue(dataList.isEmpty(), "返回列表应为空");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-成功场景（单个标签）")
    void testSelectPublicTagList_Success_SingleTag() {
        // 准备测试数据（单个标签）
        ApiTagVO tag = new ApiTagVO();
        tag.setId(1);
        tag.setName("Java");
        tag.setArticleCount(10);
        tag.setAvatar("avatar.jpg");

        ResponseResult expectedResult = ResponseResult.success(Arrays.asList(tag));

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-成功场景（多个标签）")
    void testSelectPublicTagList_Success_MultipleTags() {
        // 准备测试数据（多个标签）
        List<ApiTagVO> tagList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ApiTagVO tag = new ApiTagVO();
            tag.setId(i);
            tag.setName("标签" + i);
            tag.setArticleCount(i * 2);
            tag.setAvatar("avatar" + i + ".jpg");
            tagList.add(tag);
        }

        ResponseResult expectedResult = ResponseResult.success(tagList);

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        List<?> dataList = (List<?>) result.getData();
        assertEquals(10, dataList.size(), "返回列表应包含10个标签");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-Service返回错误")
    void testSelectPublicTagList_ServiceError() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.error("获取标签列表失败");

        // 模拟Service返回错误
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-多次调用")
    void testSelectPublicTagList_MultipleCalls() {
        // 准备测试数据
        List<ApiTagVO> tagList = new ArrayList<>();
        ApiTagVO tag = new ApiTagVO();
        tag.setId(1);
        tag.setName("Java");
        tagList.add(tag);

        ResponseResult expectedResult = ResponseResult.success(tagList);

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行多次调用
        apiTagsController.selectPublicTagList();
        apiTagsController.selectPublicTagList();
        apiTagsController.selectPublicTagList();

        // 验证Service被调用了3次
        verify(tagsService, times(3)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-验证Service调用")
    void testSelectPublicTagList_VerifyServiceCall() {
        // 准备测试数据
        ResponseResult expectedResult = ResponseResult.success(new ArrayList<>());

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        apiTagsController.selectPublicTagList();

        // 验证Service调用
        verify(tagsService, times(1)).selectPublicTagList();
        verify(tagsService, never()).listTags(anyString());
        verify(tagsService, never()).insertTag(any());
        verify(tagsService, never()).updateTag(any());
        verify(tagsService, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试获取标签列表-标签包含文章数量为0")
    void testSelectPublicTagList_TagWithZeroArticleCount() {
        // 准备测试数据（标签文章数量为0）
        ApiTagVO tag = new ApiTagVO();
        tag.setId(1);
        tag.setName("新标签");
        tag.setArticleCount(0);
        tag.setAvatar("avatar.jpg");

        ResponseResult expectedResult = ResponseResult.success(Arrays.asList(tag));

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-标签包含大量文章")
    void testSelectPublicTagList_TagWithLargeArticleCount() {
        // 准备测试数据（标签文章数量很大）
        ApiTagVO tag = new ApiTagVO();
        tag.setId(1);
        tag.setName("热门标签");
        tag.setArticleCount(9999);
        tag.setAvatar("avatar.jpg");

        ResponseResult expectedResult = ResponseResult.success(Arrays.asList(tag));

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-标签名称为空")
    void testSelectPublicTagList_TagWithNullName() {
        // 准备测试数据（标签名称为null）
        ApiTagVO tag = new ApiTagVO();
        tag.setId(1);
        tag.setName(null);
        tag.setArticleCount(10);
        tag.setAvatar("avatar.jpg");

        ResponseResult expectedResult = ResponseResult.success(Arrays.asList(tag));

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(tagsService, times(1)).selectPublicTagList();
    }

    @Test
    @DisplayName("测试获取标签列表-标签头像为空")
    void testSelectPublicTagList_TagWithNullAvatar() {
        // 准备测试数据（标签头像为null）
        ApiTagVO tag = new ApiTagVO();
        tag.setId(1);
        tag.setName("Java");
        tag.setArticleCount(10);
        tag.setAvatar(null);

        ResponseResult expectedResult = ResponseResult.success(Arrays.asList(tag));

        // 模拟Service返回
        when(tagsService.selectPublicTagList())
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiTagsController.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(tagsService, times(1)).selectPublicTagList();
    }
}

