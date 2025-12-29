package com.shiyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Tags;
import com.shiyi.mapper.TagsMapper;
import com.shiyi.utils.PageUtils;
import com.shiyi.vo.ApiTagVO;
import com.shiyi.vo.SystemTagListVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TagsServiceImpl 单元测试
 * 使用JaCoCo进行代码覆盖率测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("标签服务实现类测试")
@SuppressWarnings("unchecked")
class TagsServiceImplTest {

    @Mock
    private TagsMapper tagsMapper;

    private TagsServiceImpl tagsService;

    @BeforeEach
    void setUp() throws Exception {
        // 重置Mock
        reset(tagsMapper);

        // 创建Service实例
        tagsService = new TagsServiceImpl();

        // 使用反射设置baseMapper
        Field baseMapperField = tagsService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(tagsService, tagsMapper);

        // 设置PageUtils的默认值（如果没有设置，会使用默认Page）
        PageUtils.setCurrentPage(new Page<>(1L, 10L));
    }

    // ==================== listTags 方法测试 ====================

    @Test
    @DisplayName("测试标签列表-成功场景（带关键词）")
    void testListTags_Success_WithKeyword() {
        // 准备测试数据
        String keyword = "Java";
        Page<SystemTagListVo> page = new Page<>(1L, 10L);
        List<SystemTagListVo> records = new ArrayList<>();
        SystemTagListVo tag = new SystemTagListVo();
        tag.setId(1L);
        tag.setName("Java");
        tag.setArticleCount(10);
        records.add(tag);
        page.setRecords(records);
        page.setTotal(1L);

        // 模拟Mapper返回
        when(tagsMapper.selectPageRecord(any(Page.class), eq(keyword)))
                .thenReturn(page);

        // 执行测试
        ResponseResult result = tagsService.listTags(keyword);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(tagsMapper, times(1)).selectPageRecord(any(Page.class), eq(keyword));
    }

    @Test
    @DisplayName("测试标签列表-成功场景（无关键词）")
    void testListTags_Success_WithoutKeyword() {
        // 准备测试数据
        String keyword = null;
        Page<SystemTagListVo> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());
        page.setTotal(0L);

        // 模拟Mapper返回
        when(tagsMapper.selectPageRecord(any(Page.class), isNull()))
                .thenReturn(page);

        // 执行测试
        ResponseResult result = tagsService.listTags(keyword);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).selectPageRecord(any(Page.class), isNull());
    }

    @Test
    @DisplayName("测试标签列表-空关键词")
    void testListTags_EmptyKeyword() {
        // 准备测试数据
        String keyword = "";
        Page<SystemTagListVo> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // 模拟Mapper返回
        when(tagsMapper.selectPageRecord(any(Page.class), eq("")))
                .thenReturn(page);

        // 执行测试
        ResponseResult result = tagsService.listTags(keyword);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).selectPageRecord(any(Page.class), eq(""));
    }

    // ==================== getTagsById 方法测试 ====================

    @Test
    @DisplayName("测试获取标签详情-成功场景")
    void testGetTagsById_Success() {
        // 准备测试数据
        Long id = 1L;
        Tags tag = Tags.builder()
                .id(id)
                .name("Java")
                .sort(1)
                .clickVolume(100)
                .avatar("avatar.jpg")
                .build();

        // 模拟Mapper返回
        when(tagsMapper.selectById(id))
                .thenReturn(tag);

        // 执行测试
        ResponseResult result = tagsService.getTagsById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        Tags resultTag = (Tags) result.getData();
        assertEquals(id, resultTag.getId(), "标签ID应匹配");
        assertEquals("Java", resultTag.getName(), "标签名称应匹配");
        verify(tagsMapper, times(1)).selectById(id);
    }

    @Test
    @DisplayName("测试获取标签详情-标签不存在")
    void testGetTagsById_NotFound() {
        // 准备测试数据
        Long id = 999L;

        // 模拟Mapper返回null
        when(tagsMapper.selectById(id))
                .thenReturn(null);

        // 执行测试
        ResponseResult result = tagsService.getTagsById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNull(result.getData(), "返回数据应为null");
        verify(tagsMapper, times(1)).selectById(id);
    }

    // ==================== insertTag 方法测试 ====================

    @Test
    @DisplayName("测试添加标签-成功场景")
    void testInsertTag_Success() {
        // 准备测试数据
        Tags tag = Tags.builder()
                .name("新标签")
                .sort(1)
                .clickVolume(0)
                .avatar("avatar.jpg")
                .build();

        // 模拟Mapper返回（validateName不抛出异常，insert返回1）
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(null); // 标签名不存在
        when(tagsMapper.insert(any(Tags.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = tagsService.insertTag(tag);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(tagsMapper, times(1)).insert(any(Tags.class));
    }

    @Test
    @DisplayName("测试添加标签-标签名已存在")
    void testInsertTag_NameExists() {
        // 准备测试数据
        Tags tag = Tags.builder()
                .name("已存在的标签")
                .build();

        Tags existingTag = Tags.builder()
                .id(1L)
                .name("已存在的标签")
                .build();

        // 模拟Mapper返回（标签名已存在）
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(existingTag);

        // 执行测试并验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            tagsService.insertTag(tag);
        }, "应抛出IllegalArgumentException异常");

        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(tagsMapper, never()).insert(any(Tags.class));
    }

    // ==================== updateTag 方法测试 ====================

    @Test
    @DisplayName("测试修改标签-成功场景（不修改名称）")
    void testUpdateTag_Success_WithoutNameChange() {
        // 准备测试数据
        Tags tag = Tags.builder()
                .id(1L)
                .name("Java")
                .sort(2)
                .clickVolume(100)
                .build();

        Tags existingTag = Tags.builder()
                .id(1L)
                .name("Java")
                .sort(1)
                .clickVolume(50)
                .build();

        // 模拟Mapper返回
        when(tagsMapper.selectById(1L))
                .thenReturn(existingTag);
        when(tagsMapper.updateById(any(Tags.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = tagsService.updateTag(tag);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).selectById(1L);
        verify(tagsMapper, times(1)).updateById(any(Tags.class));
        verify(tagsMapper, never()).selectOne(any(QueryWrapper.class)); // 名称未改变，不验证名称
    }

    @Test
    @DisplayName("测试修改标签-成功场景（修改名称，新名称可用）")
    void testUpdateTag_Success_WithNameChange() {
        // 准备测试数据
        Tags tag = Tags.builder()
                .id(1L)
                .name("新名称")
                .build();

        Tags existingTag = Tags.builder()
                .id(1L)
                .name("旧名称")
                .build();

        // 模拟Mapper返回
        when(tagsMapper.selectById(1L))
                .thenReturn(existingTag);
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(null); // 新名称不存在
        when(tagsMapper.updateById(any(Tags.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = tagsService.updateTag(tag);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).selectById(1L);
        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class)); // 验证新名称
        verify(tagsMapper, times(1)).updateById(any(Tags.class));
    }

    @Test
    @DisplayName("测试修改标签-新名称已存在")
    void testUpdateTag_NewNameExists() {
        // 准备测试数据
        Tags tag = Tags.builder()
                .id(1L)
                .name("已存在的名称")
                .build();

        Tags existingTag = Tags.builder()
                .id(1L)
                .name("旧名称")
                .build();

        Tags conflictingTag = Tags.builder()
                .id(2L)
                .name("已存在的名称")
                .build();

        // 模拟Mapper返回
        when(tagsMapper.selectById(1L))
                .thenReturn(existingTag);
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(conflictingTag); // 新名称已存在

        // 执行测试并验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            tagsService.updateTag(tag);
        }, "应抛出IllegalArgumentException异常");

        verify(tagsMapper, times(1)).selectById(1L);
        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(tagsMapper, never()).updateById(any(Tags.class));
    }

    // ==================== deleteById 方法测试 ====================

    @Test
    @DisplayName("测试删除标签-成功场景")
    void testDeleteById_Success() {
        // 准备测试数据
        Long id = 1L;

        // 模拟Mapper返回
        when(tagsMapper.deleteById(id))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = tagsService.deleteById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("测试删除标签-删除失败")
    void testDeleteById_Failure() {
        // 准备测试数据
        Long id = 999L;

        // 模拟Mapper返回（删除0行）
        when(tagsMapper.deleteById(id))
                .thenReturn(0);

        // 执行测试
        ResponseResult result = tagsService.deleteById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).deleteById(id);
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除标签-成功场景")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // 模拟Mapper返回
        when(tagsMapper.deleteBatchIds(ids))
                .thenReturn(3);

        // 执行测试
        ResponseResult result = tagsService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试批量删除标签-空列表")
    void testDeleteBatch_EmptyList() {
        // 准备测试数据
        List<Long> ids = new ArrayList<>();

        // 模拟Mapper返回
        when(tagsMapper.deleteBatchIds(ids))
                .thenReturn(0);

        // 执行测试
        ResponseResult result = tagsService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).deleteBatchIds(ids);
    }

    // ==================== top 方法测试 ====================

    @Test
    @DisplayName("测试置顶标签-成功场景")
    void testTop_Success() {
        // 准备测试数据
        Long id = 2L;
        Tags topTag = Tags.builder()
                .id(1L)
                .sort(100)
                .build();

        // 模拟Mapper返回
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(topTag);
        when(tagsMapper.updateById(any(Tags.class)))
                .thenReturn(1);

        // 执行测试
        ResponseResult result = tagsService.top(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(tagsMapper, times(1)).updateById(argThat(tag -> 
            tag.getId().equals(id) && tag.getSort() == 101
        ));
    }

    @Test
    @DisplayName("测试置顶标签-标签已在最顶端")
    void testTop_AlreadyAtTop() {
        // 准备测试数据
        Long id = 1L;
        Tags topTag = Tags.builder()
                .id(1L)
                .sort(100)
                .build();

        // 模拟Mapper返回
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(topTag);

        // 执行测试并验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            tagsService.top(id);
        }, "应抛出IllegalArgumentException异常");

        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(tagsMapper, never()).updateById(any(Tags.class));
    }

    @Test
    @DisplayName("测试置顶标签-更新失败")
    void testTop_UpdateFailure() {
        // 准备测试数据
        Long id = 2L;
        Tags topTag = Tags.builder()
                .id(1L)
                .sort(100)
                .build();

        // 模拟Mapper返回
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(topTag);
        when(tagsMapper.updateById(any(Tags.class)))
                .thenReturn(0); // 更新失败

        // 执行测试
        ResponseResult result = tagsService.top(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(tagsMapper, times(1)).updateById(any(Tags.class));
    }

    // ==================== selectPublicTagList 方法测试 ====================

    @Test
    @DisplayName("测试获取所有标签-成功场景（有数据）")
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

        // 模拟Mapper返回
        when(tagsMapper.selectListCountArticle())
                .thenReturn(tagList);

        // 执行测试
        ResponseResult result = tagsService.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        assertTrue(result.getData() instanceof List, "返回数据应为List类型");
        List<?> dataList = (List<?>) result.getData();
        assertEquals(2, dataList.size(), "返回列表应包含2个标签");
        verify(tagsMapper, times(1)).selectListCountArticle();
    }

    @Test
    @DisplayName("测试获取所有标签-成功场景（空列表）")
    void testSelectPublicTagList_Success_EmptyList() {
        // 准备测试数据（空列表）
        List<ApiTagVO> emptyList = new ArrayList<>();

        // 模拟Mapper返回
        when(tagsMapper.selectListCountArticle())
                .thenReturn(emptyList);

        // 执行测试
        ResponseResult result = tagsService.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        List<?> dataList = (List<?>) result.getData();
        assertTrue(dataList.isEmpty(), "返回列表应为空");
        verify(tagsMapper, times(1)).selectListCountArticle();
    }

    @Test
    @DisplayName("测试获取所有标签-多个标签")
    void testSelectPublicTagList_MultipleTags() {
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

        // 模拟Mapper返回
        when(tagsMapper.selectListCountArticle())
                .thenReturn(tagList);

        // 执行测试
        ResponseResult result = tagsService.selectPublicTagList();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        List<?> dataList = (List<?>) result.getData();
        assertEquals(10, dataList.size(), "返回列表应包含10个标签");
        verify(tagsMapper, times(1)).selectListCountArticle();
    }

    // ==================== validateName 方法测试 ====================

    @Test
    @DisplayName("测试验证标签名-名称不存在")
    void testValidateName_NameNotExists() {
        // 准备测试数据
        String name = "新标签名";

        // 模拟Mapper返回null（名称不存在）
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(null);

        // 执行测试（不应抛出异常）
        assertDoesNotThrow(() -> {
            tagsService.validateName(name);
        }, "名称不存在时不应抛出异常");

        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("测试验证标签名-名称已存在")
    void testValidateName_NameExists() {
        // 准备测试数据
        String name = "已存在的标签名";
        Tags existingTag = Tags.builder()
                .id(1L)
                .name(name)
                .build();

        // 模拟Mapper返回（名称已存在）
        when(tagsMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(existingTag);

        // 执行测试并验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            tagsService.validateName(name);
        }, "名称已存在时应抛出IllegalArgumentException异常");

        verify(tagsMapper, times(1)).selectOne(any(QueryWrapper.class));
    }
}

