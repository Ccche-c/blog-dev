package com.shiyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Dict;
import com.shiyi.entity.DictData;
import com.shiyi.mapper.DictDataMapper;
import com.shiyi.service.DictService;
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
import java.util.Map;

import static com.shiyi.common.Constants.*;
import static com.shiyi.common.ResultCode.DATA_TAG_IS_EXIST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DictDataServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("字典数据服务实现类测试")
@SuppressWarnings("unchecked")
class DictDataServiceImplTest {

    @Mock
    private DictDataMapper dictDataMapper;

    @Mock
    private DictService dictService;

    @InjectMocks
    private DictDataServiceImpl dictDataService;

    @BeforeEach
    void setUp() {
        // 使用反射注入 baseMapper
        ReflectionTestUtils.setField(dictDataService, "baseMapper", dictDataMapper);
    }

    // ==================== listDictData 方法测试 ====================

    @Test
    @DisplayName("测试获取字典数据列表-成功（带发布状态）")
    void testListDictData_Success_WithIsPublish() {
        // 准备测试数据
        Integer dictId = 1;
        Integer isPublish = 1;
        Page<DictData> page = new Page<>(1L, 10L);
        List<DictData> records = new ArrayList<>();
        DictData dictData = new DictData();
        dictData.setId(1L);
        dictData.setDictId(1L);
        dictData.setLabel("测试标签");
        dictData.setValue("test_value");
        records.add(dictData);
        page.setRecords(records);

        Dict dict = new Dict();
        dict.setId(1L);
        dict.setName("测试字典");
        dict.setType("test_type");

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(dictDataMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
            when(dictService.getById(1L)).thenReturn(dict);

            // 执行测试
            ResponseResult result = dictDataService.listDictData(dictId, isPublish);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "数据不应为null");
            assertEquals(dict, dictData.getDict(), "字典数据应关联字典对象");

            // 验证调用
            verify(dictDataMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
            verify(dictService, times(1)).getById(1L);
        }
    }

    @Test
    @DisplayName("测试获取字典数据列表-成功（无发布状态）")
    void testListDictData_Success_NoIsPublish() {
        // 准备测试数据
        Integer dictId = 1;
        Integer isPublish = null;
        Page<DictData> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(dictDataMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

            // 执行测试
            ResponseResult result = dictDataService.listDictData(dictId, isPublish);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(dictDataMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
        }
    }

    // ==================== insertDictData 方法测试 ====================

    @Test
    @DisplayName("测试添加字典数据-成功")
    void testInsertDictData_Success() {
        // 准备测试数据
        DictData dictData = new DictData();
        dictData.setId(1L);
        dictData.setDictId(1L);
        dictData.setLabel("新标签");
        dictData.setValue("new_value");

        // Mock - isExist 方法中 Assert.notNull(temp, ...) 在 temp 为 null 时抛出异常
        // 但根据代码逻辑，如果数据已存在（temp 不为 null），不会抛出异常，可以继续插入
        // 注意：这个逻辑看起来是反的，但按照实际代码行为来测试
        DictData existingDictData = new DictData();
        existingDictData.setId(2L);
        existingDictData.setDictId(1L);
        existingDictData.setLabel("新标签");
        when(dictDataMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingDictData);
        when(dictDataMapper.insert(dictData)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictDataService.insertDictData(dictData);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictDataMapper, times(1)).insert(dictData);
    }

    @Test
    @DisplayName("测试添加字典数据-标签不存在（抛出异常）")
    void testInsertDictData_LabelNotExists() {
        // 准备测试数据
        DictData dictData = new DictData();
        dictData.setDictId(1L);
        dictData.setLabel("不存在标签");
        dictData.setValue("new_value");

        // Mock - isExist 方法中 Assert.notNull(temp, ...) 在 temp 为 null 时抛出异常
        // 如果 selectOne 返回 null（不存在），Assert.notNull 会抛出异常
        when(dictDataMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dictDataService.insertDictData(dictData);
        }, "应该抛出 IllegalArgumentException");

        assertEquals(DATA_TAG_IS_EXIST.getDesc(), exception.getMessage(), "异常消息应匹配");

        // 验证调用
        verify(dictDataMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictDataMapper, never()).insert(any(DictData.class));
    }

    // ==================== updateDictData 方法测试 ====================

    @Test
    @DisplayName("测试修改字典数据-成功（标签不存在）")
    void testUpdateDictData_Success_LabelNotExists() {
        // 准备测试数据
        DictData dictData = new DictData();
        dictData.setId(1L);
        dictData.setLabel("新标签");
        dictData.setValue("new_value");

        // Mock
        when(dictDataMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // 标签不存在
        when(dictDataMapper.updateById(dictData)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictDataService.updateDictData(dictData);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictDataMapper, times(1)).updateById(dictData);
    }

    @Test
    @DisplayName("测试修改字典数据-成功（标签存在但为同一记录）")
    void testUpdateDictData_Success_SameRecord() {
        // 准备测试数据
        DictData dictData = new DictData();
        dictData.setId(1L);
        dictData.setLabel("原标签");
        dictData.setValue("updated_value");

        DictData existingDictData = new DictData();
        existingDictData.setId(1L); // 同一记录
        existingDictData.setLabel("原标签");

        // Mock
        when(dictDataMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingDictData);
        when(dictDataMapper.updateById(dictData)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictDataService.updateDictData(dictData);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictDataMapper, times(1)).updateById(dictData);
    }

    @Test
    @DisplayName("测试修改字典数据-标签已存在（不同记录）")
    void testUpdateDictData_LabelExists_DifferentRecord() {
        // 准备测试数据
        DictData dictData = new DictData();
        dictData.setId(1L);
        dictData.setLabel("已存在标签");
        dictData.setValue("new_value");

        DictData existingDictData = new DictData();
        existingDictData.setId(2L); // 不同记录
        existingDictData.setLabel("已存在标签");

        // Mock
        when(dictDataMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingDictData);

        // 执行测试
        ResponseResult result = dictDataService.updateDictData(dictData);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(400, result.getCode(), "响应码应为400");
        assertEquals("该标签已存在!", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(dictDataMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(dictDataMapper, never()).updateById(any(DictData.class));
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除字典数据-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // Mock
        when(dictDataMapper.deleteBatchIds(ids)).thenReturn(3);

        // 执行测试
        ResponseResult result = dictDataService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试批量删除字典数据-空列表")
    void testDeleteBatch_EmptyList() {
        // 准备测试数据
        List<Long> ids = new ArrayList<>();

        // Mock
        when(dictDataMapper.deleteBatchIds(ids)).thenReturn(0);

        // 执行测试
        ResponseResult result = dictDataService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataMapper, times(1)).deleteBatchIds(ids);
    }

    // ==================== deleteDictData 方法测试 ====================

    @Test
    @DisplayName("测试删除字典数据-成功")
    void testDeleteDictData_Success() {
        // 准备测试数据
        Long id = 1L;

        // Mock
        when(dictDataMapper.deleteById(id)).thenReturn(1);

        // 执行测试
        ResponseResult result = dictDataService.deleteDictData(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(dictDataMapper, times(1)).deleteById(id);
    }

    // ==================== getDataByDictType 方法测试 ====================

    @Test
    @DisplayName("测试根据字典类型获取字典数据-成功（有默认值）")
    void testGetDataByDictType_Success_WithDefaultValue() {
        // 准备测试数据
        List<String> types = Arrays.asList("test_type1", "test_type2");

        Dict dict1 = new Dict();
        dict1.setId(1L);
        dict1.setType("test_type1");
        dict1.setName("测试字典1");

        Dict dict2 = new Dict();
        dict2.setId(2L);
        dict2.setType("test_type2");
        dict2.setName("测试字典2");

        List<Dict> dictList = Arrays.asList(dict1, dict2);

        DictData dictData1 = new DictData();
        dictData1.setId(1L);
        dictData1.setDictId(1L);
        dictData1.setLabel("标签1");
        dictData1.setValue("value1");
        dictData1.setIsDefault("0"); // 非默认

        DictData dictData2 = new DictData();
        dictData2.setId(2L);
        dictData2.setDictId(1L);
        dictData2.setLabel("标签2");
        dictData2.setValue("value2");
        dictData2.setIsDefault("1"); // 默认值

        DictData dictData3 = new DictData();
        dictData3.setId(3L);
        dictData3.setDictId(2L);
        dictData3.setLabel("标签3");
        dictData3.setValue("value3");
        dictData3.setIsDefault("0"); // 非默认

        List<DictData> dataList1 = Arrays.asList(dictData1, dictData2);
        List<DictData> dataList2 = Arrays.asList(dictData3);

        // Mock
        when(dictService.list(any(QueryWrapper.class))).thenReturn(dictList);
        when(dictDataMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(dataList1)  // 第一次调用返回 dict1 的数据
                .thenReturn(dataList2); // 第二次调用返回 dict2 的数据

        // 执行测试
        ResponseResult result = dictDataService.getDataByDictType(types);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "数据不应为null");

        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) result.getData();
        assertEquals(2, map.size(), "应包含2个字典类型的数据");

        // 验证 dict1 的数据
        Map<String, Object> result1 = map.get("test_type1");
        assertNotNull(result1, "test_type1 的数据不应为null");
        assertEquals("value2", result1.get(DEFAULT_VALUE), "默认值应为 value2");
        assertEquals(dataList1, result1.get(LIST), "列表应匹配");

        // 验证 dict2 的数据
        Map<String, Object> result2 = map.get("test_type2");
        assertNotNull(result2, "test_type2 的数据不应为null");
        assertNull(result2.get(DEFAULT_VALUE), "默认值应为 null");
        assertEquals(dataList2, result2.get(LIST), "列表应匹配");

        // 验证调用
        verify(dictService, times(1)).list(any(QueryWrapper.class));
        verify(dictDataMapper, times(2)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("测试根据字典类型获取字典数据-成功（无默认值）")
    void testGetDataByDictType_Success_NoDefaultValue() {
        // 准备测试数据
        List<String> types = Arrays.asList("test_type1");

        Dict dict1 = new Dict();
        dict1.setId(1L);
        dict1.setType("test_type1");
        dict1.setName("测试字典1");

        List<Dict> dictList = Arrays.asList(dict1);

        DictData dictData1 = new DictData();
        dictData1.setId(1L);
        dictData1.setDictId(1L);
        dictData1.setLabel("标签1");
        dictData1.setValue("value1");
        dictData1.setIsDefault("0"); // 非默认

        List<DictData> dataList1 = Arrays.asList(dictData1);

        // Mock
        when(dictService.list(any(QueryWrapper.class))).thenReturn(dictList);
        when(dictDataMapper.selectList(any(QueryWrapper.class))).thenReturn(dataList1);

        // 执行测试
        ResponseResult result = dictDataService.getDataByDictType(types);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) result.getData();
        assertEquals(1, map.size(), "应包含1个字典类型的数据");

        Map<String, Object> result1 = map.get("test_type1");
        assertNotNull(result1, "test_type1 的数据不应为null");
        assertNull(result1.get(DEFAULT_VALUE), "默认值应为 null");
        assertEquals(dataList1, result1.get(LIST), "列表应匹配");

        // 验证调用
        verify(dictService, times(1)).list(any(QueryWrapper.class));
        verify(dictDataMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("测试根据字典类型获取字典数据-空类型列表")
    void testGetDataByDictType_EmptyTypes() {
        // 准备测试数据
        List<String> types = new ArrayList<>();

        // Mock
        when(dictService.list(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // 执行测试
        ResponseResult result = dictDataService.getDataByDictType(types);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) result.getData();
        assertEquals(0, map.size(), "应包含0个字典类型的数据");

        // 验证调用
        verify(dictService, times(1)).list(any(QueryWrapper.class));
        verify(dictDataMapper, never()).selectList(any(QueryWrapper.class));
    }
}

