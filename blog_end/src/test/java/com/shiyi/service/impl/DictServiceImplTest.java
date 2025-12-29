package com.shiyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Dict;
import com.shiyi.mapper.DictMapper;
import com.shiyi.service.DictDataService;
import com.shiyi.utils.HumpLineUtils;
import com.shiyi.utils.PageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DictServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("字典服务实现类测试")
@SuppressWarnings("unchecked")
class DictServiceImplTest {

    @Mock
    private DictMapper dictMapper;

    @Mock
    private DictDataService dictDataService;

    @InjectMocks
    private DictServiceImpl dictService;

    @BeforeEach
    void setUp() {
        // 使用反射注入 baseMapper
        ReflectionTestUtils.setField(dictService, "baseMapper", dictMapper);
    }

    // ==================== listDict 方法测试 ====================

    @Test
    @DisplayName("测试字典列表-成功（无筛选条件）")
    void testListDict_Success_NoFilters() {
        // 准备测试数据
        Page<Dict> page = new Page<>(1L, 10L);
        List<Dict> records = new ArrayList<>();
        Dict dict = new Dict();
        dict.setId(1L);
        dict.setName("测试字典");
        dict.setType("test_type");
        records.add(dict);
        page.setRecords(records);

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(dictMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

            // 执行测试
            ResponseResult result = dictService.listDict(null, null, null, null);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "数据不应为null");

            // 验证调用
            verify(dictMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
        }
    }

    @Test
    @DisplayName("测试字典列表-成功（带名称筛选）")
    void testListDict_Success_WithName() {
        // 准备测试数据
        String name = "测试";
        Page<Dict> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(dictMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

            // 执行测试
            ResponseResult result = dictService.listDict(name, null, null, null);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(dictMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
        }
    }

    @Test
    @DisplayName("测试字典列表-成功（带发布状态筛选）")
    void testListDict_Success_WithIsPublish() {
        // 准备测试数据
        Integer isPublish = 1;
        Page<Dict> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(dictMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

            // 执行测试
            ResponseResult result = dictService.listDict(null, isPublish, null, null);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(dictMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
        }
    }

    @Test
    @DisplayName("测试字典列表-成功（升序排序）")
    void testListDict_Success_WithAscColumn() {
        // 准备测试数据
        String ascColumn = "createTime";
        String convertedColumn = "create_time";
        Page<Dict> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class);
             MockedStatic<HumpLineUtils> humpLineUtilsMock = mockStatic(HumpLineUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            humpLineUtilsMock.when(() -> HumpLineUtils.humpToLine2(ascColumn)).thenReturn(convertedColumn);
            when(dictMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

            // 执行测试
            ResponseResult result = dictService.listDict(null, null, null, ascColumn);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            humpLineUtilsMock.verify(() -> HumpLineUtils.humpToLine2(ascColumn));
            verify(dictMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
        }
    }

    @Test
    @DisplayName("测试字典列表-成功（降序排序）")
    void testListDict_Success_WithDescColumn() {
        // 准备测试数据
        String descColumn = "updateTime";
        String convertedColumn = "update_time";
        Page<Dict> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class);
             MockedStatic<HumpLineUtils> humpLineUtilsMock = mockStatic(HumpLineUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            humpLineUtilsMock.when(() -> HumpLineUtils.humpToLine2(descColumn)).thenReturn(convertedColumn);
            when(dictMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

            // 执行测试
            ResponseResult result = dictService.listDict(null, null, descColumn, null);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            humpLineUtilsMock.verify(() -> HumpLineUtils.humpToLine2(descColumn));
            verify(dictMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
        }
    }

    // ==================== insertDict 方法测试 ====================

    @Test
    @DisplayName("测试添加字典-成功")
    void testInsertDict_Success() {
        // 准备测试数据
        Dict dict = new Dict();
        dict.setId(1L);
        dict.setName("测试字典");
        dict.setType("test_type");

        // Mock
        when(dictMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // validateType 返回 null
        when(dictMapper.insert(dict)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictService.insertDict(dict);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictMapper, times(1)).insert(dict);
    }

    @Test
    @DisplayName("测试添加字典-类型已存在")
    void testInsertDict_TypeExists() {
        // 准备测试数据
        Dict dict = new Dict();
        dict.setName("测试字典");
        dict.setType("existing_type");

        Dict existingDict = new Dict();
        existingDict.setId(2L);
        existingDict.setType("existing_type");

        // Mock
        when(dictMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingDict); // validateType 返回已存在的字典

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dictService.insertDict(dict);
        }, "应该抛出 IllegalArgumentException");

        assertEquals("该字典类型已存在!", exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(dictMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictMapper, never()).insert(any(Dict.class));
    }

    // ==================== updateDict 方法测试 ====================

    @Test
    @DisplayName("测试修改字典-成功（类型未改变）")
    void testUpdateDict_Success_TypeUnchanged() {
        // 准备测试数据
        Dict dict = new Dict();
        dict.setId(1L);
        dict.setName("修改后的字典");
        dict.setType("test_type");

        Dict existingDict = new Dict();
        existingDict.setId(1L);
        existingDict.setName("原字典");
        existingDict.setType("test_type"); // 类型相同

        // Mock
        when(dictMapper.selectById(1L)).thenReturn(existingDict);
        when(dictMapper.updateById(dict)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictService.updateDict(dict);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictMapper, times(1)).selectById(1L);
        verify(dictMapper, never()).selectOne(any(QueryWrapper.class)); // validateType 不应被调用
        verify(dictMapper, times(1)).updateById(dict);
    }

    @Test
    @DisplayName("测试修改字典-成功（类型改变且新类型不存在）")
    void testUpdateDict_Success_TypeChanged() {
        // 准备测试数据
        Dict dict = new Dict();
        dict.setId(1L);
        dict.setName("修改后的字典");
        dict.setType("new_type");

        Dict existingDict = new Dict();
        existingDict.setId(1L);
        existingDict.setName("原字典");
        existingDict.setType("old_type"); // 类型不同

        // Mock
        when(dictMapper.selectById(1L)).thenReturn(existingDict);
        when(dictMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // validateType 返回 null
        when(dictMapper.updateById(dict)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictService.updateDict(dict);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictMapper, times(1)).selectById(1L);
        verify(dictMapper, times(1)).selectOne(any(QueryWrapper.class)); // validateType 被调用
        verify(dictMapper, times(1)).updateById(dict);
    }

    @Test
    @DisplayName("测试修改字典-类型已存在")
    void testUpdateDict_TypeExists() {
        // 准备测试数据
        Dict dict = new Dict();
        dict.setId(1L);
        dict.setName("修改后的字典");
        dict.setType("existing_type");

        Dict existingDict = new Dict();
        existingDict.setId(1L);
        existingDict.setName("原字典");
        existingDict.setType("old_type"); // 类型不同

        Dict conflictingDict = new Dict();
        conflictingDict.setId(2L);
        conflictingDict.setType("existing_type"); // 新类型已存在

        // Mock
        when(dictMapper.selectById(1L)).thenReturn(existingDict);
        when(dictMapper.selectOne(any(QueryWrapper.class))).thenReturn(conflictingDict); // validateType 返回已存在的字典

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dictService.updateDict(dict);
        }, "应该抛出 IllegalArgumentException");

        assertEquals("该字典类型已存在!", exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(dictMapper, times(1)).selectById(1L);
        verify(dictMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictMapper, never()).updateById(any(Dict.class));
    }

    // ==================== deleteDict 方法测试 ====================

    @Test
    @DisplayName("测试删除字典-成功")
    void testDeleteDict_Success() {
        // 准备测试数据
        int id = 1;

        // Mock
        when(dictDataService.count(any(QueryWrapper.class))).thenReturn(0); // 没有字典数据
        when(dictMapper.deleteById(id)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictService.deleteDict(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataService, times(1)).count(any(QueryWrapper.class));
        verify(dictMapper, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("测试删除字典-存在字典数据")
    void testDeleteDict_HasDictData() {
        // 准备测试数据
        int id = 1;

        // Mock
        when(dictDataService.count(any(QueryWrapper.class))).thenReturn(5); // 存在字典数据

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dictService.deleteDict(id);
        }, "应该抛出 IllegalArgumentException");

        assertEquals("该字典类型存在字典数据!", exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(dictDataService, times(1)).count(any(QueryWrapper.class));
        verify(dictMapper, never()).deleteById(anyInt());
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除字典-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // Mock
        when(dictDataService.count(any(QueryWrapper.class))).thenReturn(0); // 没有字典数据
        when(dictMapper.deleteBatchIds(ids)).thenReturn(3);

        // 执行测试
        ResponseResult result = dictService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataService, times(1)).count(any(QueryWrapper.class));
        verify(dictMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试批量删除字典-存在字典数据")
    void testDeleteBatch_HasDictData() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // Mock
        when(dictDataService.count(any(QueryWrapper.class))).thenReturn(2); // 存在字典数据

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dictService.deleteBatch(ids);
        }, "应该抛出 IllegalArgumentException");

        assertEquals("所选字典类型中存在字典数据!", exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(dictDataService, times(1)).count(any(QueryWrapper.class));
        verify(dictMapper, never()).deleteBatchIds(anyList());
    }

    @Test
    @DisplayName("测试批量删除字典-空列表")
    void testDeleteBatch_EmptyList() {
        // 准备测试数据
        List<Long> ids = new ArrayList<>();

        // Mock
        when(dictDataService.count(any(QueryWrapper.class))).thenReturn(0);

        // 执行测试
        ResponseResult result = dictService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataService, times(1)).count(any(QueryWrapper.class));
        verify(dictMapper, times(1)).deleteBatchIds(ids);
    }
}

