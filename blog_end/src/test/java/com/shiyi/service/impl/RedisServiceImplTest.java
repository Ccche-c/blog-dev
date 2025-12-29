package com.shiyi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedisServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Redis服务实现类测试")
@SuppressWarnings({"rawtypes", "unchecked"})
class RedisServiceImplTest {

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    @Mock
    private ListOperations listOperations;

    @Mock
    private HashOperations hashOperations;

    @Mock
    private SetOperations setOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    @BeforeEach
    void setUp() {
        // Mock RedisTemplate 的各种操作（使用 lenient 避免不必要的 stubbing 警告）
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    // ==================== setCacheObject 方法测试 ====================

    @Test
    @DisplayName("测试设置缓存对象-成功（无过期时间）")
    void testSetCacheObject_Success() {
        // 准备测试数据
        String key = "test:key";
        String value = "test_value";

        // Mock
        doNothing().when(valueOperations).set(eq(key), eq(value));

        // 执行测试
        redisService.setCacheObject(key, value);

        // 验证调用
        verify(valueOperations, times(1)).set(eq(key), eq(value));
    }

    @Test
    @DisplayName("测试设置缓存对象-成功（带过期时间）")
    void testSetCacheObject_WithTimeout() {
        // 准备测试数据
        String key = "test:key";
        String value = "test_value";
        Integer timeout = 60;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // Mock (注意：ValueOperations.set 接受 long timeout，Integer 会自动拆箱为 int 然后提升为 long)
        doNothing().when(valueOperations).set(eq(key), eq(value), anyLong(), eq(timeUnit));

        // 执行测试
        redisService.setCacheObject(key, value, timeout, timeUnit);

        // 验证调用 (使用 anyLong() 匹配，因为 Integer 会被转换为 long)
        verify(valueOperations, times(1)).set(eq(key), eq(value), anyLong(), eq(timeUnit));
    }

    // ==================== getCacheObject 方法测试 ====================

    @Test
    @DisplayName("测试获取缓存对象-成功")
    void testGetCacheObject_Success() {
        // 准备测试数据
        String key = "test:key";
        String expectedValue = "test_value";

        // Mock
        when(valueOperations.get(key)).thenReturn(expectedValue);

        // 执行测试
        Object result = redisService.getCacheObject(key);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedValue, result, "返回值应匹配");

        // 验证调用
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("测试获取缓存对象-不存在")
    void testGetCacheObject_NotExists() {
        // 准备测试数据
        String key = "test:key";

        // Mock
        when(valueOperations.get(key)).thenReturn(null);

        // 执行测试
        Object result = redisService.getCacheObject(key);

        // 验证结果
        assertNull(result, "返回结果应为null");

        // 验证调用
        verify(valueOperations, times(1)).get(key);
    }

    // ==================== expire 方法测试 ====================

    @Test
    @DisplayName("测试设置过期时间-成功")
    void testExpire_Success() {
        // 准备测试数据
        String key = "test:key";
        long timeout = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // Mock
        when(redisTemplate.expire(key, timeout, timeUnit)).thenReturn(true);

        // 执行测试
        boolean result = redisService.expire(key, timeout, timeUnit);

        // 验证结果
        assertTrue(result, "设置过期时间应成功");

        // 验证调用
        verify(redisTemplate, times(1)).expire(key, timeout, timeUnit);
    }

    @Test
    @DisplayName("测试设置过期时间-失败")
    void testExpire_Failed() {
        // 准备测试数据
        String key = "test:key";
        long timeout = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // Mock
        when(redisTemplate.expire(key, timeout, timeUnit)).thenReturn(false);

        // 执行测试
        boolean result = redisService.expire(key, timeout, timeUnit);

        // 验证结果
        assertFalse(result, "设置过期时间应失败");

        // 验证调用
        verify(redisTemplate, times(1)).expire(key, timeout, timeUnit);
    }

    // ==================== deleteObject 方法测试 ====================

    @Test
    @DisplayName("测试删除对象-成功")
    void testDeleteObject_Success() {
        // 准备测试数据
        String key = "test:key";

        // Mock
        when(redisTemplate.delete(key)).thenReturn(true);

        // 执行测试
        boolean result = redisService.deleteObject(key);

        // 验证结果
        assertTrue(result, "删除应成功");

        // 验证调用
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    @DisplayName("测试批量删除对象-成功")
    void testDeleteObject_Batch() {
        // 准备测试数据
        Collection<String> keys = Arrays.asList("key1", "key2", "key3");

        // Mock
        when(redisTemplate.delete(keys)).thenReturn(3L);

        // 执行测试
        long result = redisService.deleteObject(keys);

        // 验证结果
        assertEquals(3L, result, "删除数量应匹配");

        // 验证调用
        verify(redisTemplate, times(1)).delete(keys);
    }

    // ==================== setCacheList 方法测试 ====================

    @Test
    @DisplayName("测试设置缓存列表-成功")
    void testSetCacheList_Success() {
        // 准备测试数据
        String key = "test:list";
        List<Object> dataList = Arrays.asList("item1", "item2", "item3");

        // Mock
        when(listOperations.rightPushAll(key, dataList)).thenReturn(3L);

        // 执行测试
        long result = redisService.setCacheList(key, dataList);

        // 验证结果
        assertEquals(3L, result, "返回数量应匹配");

        // 验证调用
        verify(listOperations, times(1)).rightPushAll(key, dataList);
    }

    @Test
    @DisplayName("测试设置缓存列表-返回null")
    void testSetCacheList_ReturnsNull() {
        // 准备测试数据
        String key = "test:list";
        List<Object> dataList = Arrays.asList("item1", "item2");

        // Mock
        when(listOperations.rightPushAll(key, dataList)).thenReturn(null);

        // 执行测试
        long result = redisService.setCacheList(key, dataList);

        // 验证结果
        assertEquals(0L, result, "返回数量应为0");

        // 验证调用
        verify(listOperations, times(1)).rightPushAll(key, dataList);
    }

    // ==================== getCacheList 方法测试 ====================

    @Test
    @DisplayName("测试获取缓存列表-成功")
    void testGetCacheList_Success() {
        // 准备测试数据
        String key = "test:list";
        List<Object> expectedList = Arrays.asList("item1", "item2", "item3");

        // Mock
        when(listOperations.range(key, 0, -1)).thenReturn(expectedList);

        // 执行测试
        List<Object> result = redisService.getCacheList(key);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedList, result, "返回列表应匹配");

        // 验证调用
        verify(listOperations, times(1)).range(key, 0, -1);
    }

    // ==================== setCacheMap 方法测试 ====================

    @Test
    @DisplayName("测试设置缓存Map-成功")
    void testSetCacheMap_Success() {
        // 准备测试数据
        String key = "test:map";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("field1", "value1");
        dataMap.put("field2", "value2");

        // Mock
        doNothing().when(hashOperations).putAll(key, dataMap);

        // 执行测试
        redisService.setCacheMap(key, dataMap);

        // 验证调用
        verify(hashOperations, times(1)).putAll(key, dataMap);
    }

    @Test
    @DisplayName("测试设置缓存Map-空Map")
    void testSetCacheMap_NullMap() {
        // 准备测试数据
        String key = "test:map";

        // 执行测试
        redisService.setCacheMap(key, null);

        // 验证调用（null Map 不应调用 putAll）
        verify(hashOperations, never()).putAll(anyString(), anyMap());
    }

    // ==================== getCacheMap 方法测试 ====================

    @Test
    @DisplayName("测试获取缓存Map-成功")
    void testGetCacheMap_Success() {
        // 准备测试数据
        String key = "test:map";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("field1", "value1");
        expectedMap.put("field2", "value2");

        // Mock
        when(hashOperations.entries(key)).thenReturn(expectedMap);

        // 执行测试
        Map<String, Object> result = redisService.getCacheMap(key);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedMap, result, "返回Map应匹配");

        // 验证调用
        verify(hashOperations, times(1)).entries(key);
    }

    // ==================== setCacheMapValue 方法测试 ====================

    @Test
    @DisplayName("测试设置Hash值-成功")
    void testSetCacheMapValue_Success() {
        // 准备测试数据
        String key = "test:hash";
        String hKey = "field1";
        String value = "value1";

        // Mock
        doNothing().when(hashOperations).put(key, hKey, value);

        // 执行测试
        redisService.setCacheMapValue(key, hKey, value);

        // 验证调用
        verify(hashOperations, times(1)).put(key, hKey, value);
    }

    // ==================== getCacheMapValue 方法测试 ====================

    @Test
    @DisplayName("测试获取Hash值-成功")
    void testGetCacheMapValue_Success() {
        // 准备测试数据
        String key = "test:hash";
        String hKey = "field1";
        String expectedValue = "value1";

        // Mock
        when(hashOperations.get(key, hKey)).thenReturn(expectedValue);

        // 执行测试
        String result = redisService.getCacheMapValue(key, hKey);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedValue, result, "返回值应匹配");

        // 验证调用
        verify(hashOperations, times(1)).get(key, hKey);
    }

    // ==================== getMultiCacheMapValue 方法测试 ====================

    @Test
    @DisplayName("测试批量获取Hash值-成功")
    void testGetMultiCacheMapValue_Success() {
        // 准备测试数据
        String key = "test:hash";
        Collection<Object> hKeys = Arrays.asList("field1", "field2");
        List<Object> expectedValues = Arrays.asList("value1", "value2");

        // Mock
        when(hashOperations.multiGet(eq(key), any(Collection.class))).thenReturn(expectedValues);

        // 执行测试
        List<Object> result = redisService.getMultiCacheMapValue(key, hKeys);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedValues, result, "返回列表应匹配");

        // 验证调用
        verify(hashOperations, times(1)).multiGet(eq(key), any(Collection.class));
    }

    // ==================== keys 方法测试 ====================

    @Test
    @DisplayName("测试获取键列表-成功")
    void testKeys_Success() {
        // 准备测试数据
        String pattern = "test:*";
        Set<String> expectedKeys = new HashSet<>(Arrays.asList("test:key1", "test:key2"));

        // Mock
        when(redisTemplate.keys(pattern)).thenReturn(expectedKeys);

        // 执行测试
        Collection<String> result = redisService.keys(pattern);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedKeys, result, "返回键集合应匹配");

        // 验证调用
        verify(redisTemplate, times(1)).keys(pattern);
    }

    // ==================== diff 方法测试 ====================

    @Test
    @DisplayName("测试集合差集-成功")
    void testDiff_Success() {
        // 准备测试数据
        String key1 = "set1";
        String key2 = "set2";
        Set<Object> mockDiff = new HashSet<>(Arrays.asList("item1", "item2"));
        Set<String> expectedDiff = new HashSet<>(Arrays.asList("item1", "item2"));

        // Mock
        when(setOperations.difference(key1, key2)).thenReturn(mockDiff);

        // 执行测试
        Set<String> result = redisService.diff(key1, key2);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedDiff.size(), result.size(), "返回差集大小应匹配");

        // 验证调用
        verify(setOperations, times(1)).difference(key1, key2);
    }

    // ==================== sIsMember 方法测试 ====================

    @Test
    @DisplayName("测试判断集合成员-是成员")
    void testSIsMember_True() {
        // 准备测试数据
        String key = "test:set";
        String value = "member1";

        // Mock
        when(setOperations.isMember(key, value)).thenReturn(true);

        // 执行测试
        Boolean result = redisService.sIsMember(key, value);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result, "应是成员");

        // 验证调用
        verify(setOperations, times(1)).isMember(key, value);
    }

    @Test
    @DisplayName("测试判断集合成员-不是成员")
    void testSIsMember_False() {
        // 准备测试数据
        String key = "test:set";
        String value = "member1";

        // Mock
        when(setOperations.isMember(key, value)).thenReturn(false);

        // 执行测试
        Boolean result = redisService.sIsMember(key, value);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertFalse(result, "不应是成员");

        // 验证调用
        verify(setOperations, times(1)).isMember(key, value);
    }

    // ==================== hIncr 方法测试 ====================

    @Test
    @DisplayName("测试Hash递增-成功")
    void testHIncr_Success() {
        // 准备测试数据
        String key = "test:hash";
        String hashKey = "counter";
        Long delta = 5L;
        Long expectedResult = 10L;

        // Mock
        when(hashOperations.increment(key, hashKey, delta)).thenReturn(expectedResult);

        // 执行测试
        Long result = redisService.hIncr(key, hashKey, delta);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedResult, result, "返回值应匹配");

        // 验证调用
        verify(hashOperations, times(1)).increment(key, hashKey, delta);
    }

    // ==================== hDecr 方法测试 ====================

    @Test
    @DisplayName("测试Hash递减-成功")
    void testHDecr_Success() {
        // 准备测试数据
        String key = "test:hash";
        String hashKey = "counter";
        Long delta = 3L;
        Long expectedResult = 7L;

        // Mock
        when(hashOperations.increment(key, hashKey, -delta)).thenReturn(expectedResult);

        // 执行测试
        Long result = redisService.hDecr(key, hashKey, delta);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedResult, result, "返回值应匹配");

        // 验证调用
        verify(hashOperations, times(1)).increment(key, hashKey, -delta);
    }

    // ==================== sMembers 方法测试 ====================

    @Test
    @DisplayName("测试获取集合所有成员-成功")
    void testSMembers_Success() {
        // 准备测试数据
        String key = "test:set";
        Set<Object> expectedMembers = new HashSet<>(Arrays.asList("member1", "member2", "member3"));

        // Mock
        when(setOperations.members(key)).thenReturn(expectedMembers);

        // 执行测试
        Set<Object> result = redisService.sMembers(key);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedMembers, result, "返回集合应匹配");

        // 验证调用
        verify(setOperations, times(1)).members(key);
    }

    // ==================== sRemove 方法测试 ====================

    @Test
    @DisplayName("测试删除集合成员-成功")
    void testSRemove_Success() {
        // 准备测试数据
        String key = "test:set";
        Object value1 = "member1";
        Object value2 = "member2";

        // Mock
        when(setOperations.remove(key, value1, value2)).thenReturn(2L);

        // 执行测试
        Long result = redisService.sRemove(key, value1, value2);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(2L, result, "删除数量应匹配");

        // 验证调用
        verify(setOperations, times(1)).remove(key, value1, value2);
    }

    // ==================== hGet 方法测试 ====================

    @Test
    @DisplayName("测试获取Hash值-成功")
    void testHGet_Success() {
        // 准备测试数据
        String key = "test:hash";
        String hashKey = "field1";
        String expectedValue = "value1";

        // Mock
        when(hashOperations.get(key, hashKey)).thenReturn(expectedValue);

        // 执行测试
        Object result = redisService.hGet(key, hashKey);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedValue, result, "返回值应匹配");

        // 验证调用
        verify(hashOperations, times(1)).get(key, hashKey);
    }

    // ==================== incr 方法测试 ====================

    @Test
    @DisplayName("测试值递增-成功")
    void testIncr_Success() {
        // 准备测试数据
        String key = "test:counter";
        long delta = 5L;
        Long expectedResult = 10L;

        // Mock
        when(valueOperations.increment(key, delta)).thenReturn(expectedResult);

        // 执行测试
        Long result = redisService.incr(key, delta);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(expectedResult, result, "返回值应匹配");

        // 验证调用
        verify(valueOperations, times(1)).increment(key, delta);
    }

    // ==================== sAdd 方法测试 ====================

    @Test
    @DisplayName("测试添加集合成员-成功")
    void testSAdd_Success() {
        // 准备测试数据
        String key = "test:set";
        Object value1 = "member1";
        Object value2 = "member2";

        // Mock
        when(setOperations.add(key, value1, value2)).thenReturn(2L);

        // 执行测试
        Long result = redisService.sAdd(key, value1, value2);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(2L, result, "添加数量应匹配");

        // 验证调用
        verify(setOperations, times(1)).add(key, value1, value2);
    }

    // ==================== getRedisTemplate 方法测试 ====================

    @Test
    @DisplayName("测试获取RedisTemplate-成功")
    void testGetRedisTemplate_Success() {
        // 执行测试
        RedisTemplate result = redisService.getRedisTemplate();

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(redisTemplate, result, "返回的RedisTemplate应匹配");
    }

    // ==================== incrArticle 方法测试 ====================

    @Test
    @DisplayName("测试增加文章阅读量-新文章")
    void testIncrArticle_NewArticle() {
        // 准备测试数据
        Long id = 1L;
        String key = "article:read";
        Map<String, Object> emptyMap = new HashMap<>();

        // Mock
        when(hashOperations.entries(key)).thenReturn(emptyMap);
        doNothing().when(hashOperations).putAll(eq(key), anyMap());

        // 执行测试
        redisService.incrArticle(id, key);

        // 验证调用
        verify(hashOperations, times(1)).entries(key);
        verify(hashOperations, times(1)).putAll(eq(key), anyMap());
    }

    @Test
    @DisplayName("测试增加文章阅读量-已存在文章")
    void testIncrArticle_ExistingArticle() {
        // 准备测试数据
        Long id = 1L;
        String key = "article:read";
        Map<String, Object> existingMap = new HashMap<>();
        existingMap.put("1", 10);

        // Mock
        when(hashOperations.entries(key)).thenReturn(existingMap);
        doNothing().when(hashOperations).putAll(eq(key), anyMap());

        // 执行测试
        redisService.incrArticle(id, key);

        // 验证调用
        verify(hashOperations, times(1)).entries(key);
        verify(hashOperations, times(1)).putAll(eq(key), anyMap());
    }
}

