package com.shiyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Menu;
import com.shiyi.mapper.MenuMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MenuServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单服务实现类测试")
@SuppressWarnings("unchecked")
class MenuServiceImplTest {

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuServiceImpl menuService;

    @BeforeEach
    void setUp() {
        // 使用反射注入 baseMapper
        ReflectionTestUtils.setField(menuService, "baseMapper", menuMapper);
    }

    // ==================== listMenuTree 方法测试 ====================

    @Test
    @DisplayName("测试菜单树-单级菜单")
    void testListMenuTree_SingleLevel() {
        // 准备测试数据
        List<Menu> list = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setId(1);
        menu1.setParentId(null);
        menu1.setTitle("菜单1");
        list.add(menu1);

        Menu menu2 = new Menu();
        menu2.setId(2);
        menu2.setParentId(0);
        menu2.setTitle("菜单2");
        list.add(menu2);

        // 执行测试
        List<Menu> result = menuService.listMenuTree(list);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(2, result.size(), "根菜单数量应为2");
        assertNull(result.get(0).getChildren(), "菜单1应无子菜单");
        assertNull(result.get(1).getChildren(), "菜单2应无子菜单");
    }

    @Test
    @DisplayName("测试菜单树-二级菜单")
    void testListMenuTree_TwoLevel() {
        // 准备测试数据
        List<Menu> list = new ArrayList<>();
        
        // 父菜单
        Menu parent1 = new Menu();
        parent1.setId(1);
        parent1.setParentId(null);
        parent1.setTitle("父菜单1");
        list.add(parent1);

        Menu parent2 = new Menu();
        parent2.setId(2);
        parent2.setParentId(0);
        parent2.setTitle("父菜单2");
        list.add(parent2);

        // 子菜单
        Menu child1 = new Menu();
        child1.setId(3);
        child1.setParentId(1);
        child1.setTitle("子菜单1");
        list.add(child1);

        Menu child2 = new Menu();
        child2.setId(4);
        child2.setParentId(1);
        child2.setTitle("子菜单2");
        list.add(child2);

        Menu child3 = new Menu();
        child3.setId(5);
        child3.setParentId(2);
        child3.setTitle("子菜单3");
        list.add(child3);

        // 执行测试
        List<Menu> result = menuService.listMenuTree(list);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(2, result.size(), "根菜单数量应为2");
        
        // 验证父菜单1的子菜单
        List<Menu> children1 = result.get(0).getChildren();
        assertNotNull(children1, "父菜单1应有子菜单");
        assertEquals(2, children1.size(), "父菜单1应有2个子菜单");
        assertEquals("子菜单1", children1.get(0).getTitle(), "第一个子菜单标题应匹配");
        assertEquals("子菜单2", children1.get(1).getTitle(), "第二个子菜单标题应匹配");
        
        // 验证父菜单2的子菜单
        List<Menu> children2 = result.get(1).getChildren();
        assertNotNull(children2, "父菜单2应有子菜单");
        assertEquals(1, children2.size(), "父菜单2应有1个子菜单");
        assertEquals("子菜单3", children2.get(0).getTitle(), "子菜单标题应匹配");
    }

    @Test
    @DisplayName("测试菜单树-三级菜单")
    void testListMenuTree_ThreeLevel() {
        // 准备测试数据
        List<Menu> list = new ArrayList<>();
        
        // 一级菜单
        Menu level1 = new Menu();
        level1.setId(1);
        level1.setParentId(null);
        level1.setTitle("一级菜单");
        list.add(level1);

        // 二级菜单
        Menu level2 = new Menu();
        level2.setId(2);
        level2.setParentId(1);
        level2.setTitle("二级菜单");
        list.add(level2);

        // 三级菜单
        Menu level3 = new Menu();
        level3.setId(3);
        level3.setParentId(2);
        level3.setTitle("三级菜单");
        list.add(level3);

        // 执行测试
        List<Menu> result = menuService.listMenuTree(list);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.size(), "根菜单数量应为1");
        
        List<Menu> level2Children = result.get(0).getChildren();
        assertNotNull(level2Children, "一级菜单应有子菜单");
        assertEquals(1, level2Children.size(), "一级菜单应有1个子菜单");
        
        List<Menu> level3Children = level2Children.get(0).getChildren();
        assertNotNull(level3Children, "二级菜单应有子菜单");
        assertEquals(1, level3Children.size(), "二级菜单应有1个子菜单");
        assertEquals("三级菜单", level3Children.get(0).getTitle(), "三级菜单标题应匹配");
    }

    @Test
    @DisplayName("测试菜单树-空列表")
    void testListMenuTree_EmptyList() {
        // 准备测试数据
        List<Menu> list = new ArrayList<>();

        // 执行测试
        List<Menu> result = menuService.listMenuTree(list);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(0, result.size(), "结果列表应为空");
    }

    @Test
    @DisplayName("测试菜单树-只有子菜单（无根菜单）")
    void testListMenuTree_OnlyChildren() {
        // 准备测试数据
        List<Menu> list = new ArrayList<>();
        
        Menu child = new Menu();
        child.setId(2);
        child.setParentId(1);
        child.setTitle("子菜单");
        list.add(child);

        // 执行测试
        List<Menu> result = menuService.listMenuTree(list);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(0, result.size(), "结果列表应为空（无根菜单）");
    }

    // ==================== listMenuApi 方法测试 ====================

    @Test
    @DisplayName("测试接口列表-成功（无ID）")
    void testListMenuApi_Success_NoId() {
        // 准备测试数据
        Integer id = null;
        List<Menu> parentMenus = new ArrayList<>();
        Menu parentMenu = new Menu();
        parentMenu.setId(1);
        parentMenu.setLevel(1);
        parentMenu.setTitle("父菜单");
        parentMenus.add(parentMenu);

        List<Menu> childrenMenus = new ArrayList<>();
        Menu childMenu = new Menu();
        childMenu.setId(2);
        childMenu.setParentId(1);
        childMenu.setTitle("子菜单");
        childrenMenus.add(childMenu);

        // Mock
        when(menuMapper.selectList(any(QueryWrapper.class))).thenReturn(parentMenus, childrenMenus);

        // 执行测试
        ResponseResult result = menuService.listMenuApi(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "数据不应为null");

        // 验证调用
        verify(menuMapper, times(2)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("测试接口列表-成功（有ID）")
    void testListMenuApi_Success_WithId() {
        // 准备测试数据
        Integer id = 1;
        List<Menu> parentMenus = new ArrayList<>();
        Menu parentMenu = new Menu();
        parentMenu.setId(1);
        parentMenu.setLevel(1);
        parentMenu.setTitle("父菜单");
        parentMenus.add(parentMenu);

        List<Menu> childrenMenus = new ArrayList<>();
        Menu childMenu = new Menu();
        childMenu.setId(2);
        childMenu.setParentId(1);
        childMenu.setTitle("子菜单");
        childrenMenus.add(childMenu);

        // Mock
        when(menuMapper.selectList(any(QueryWrapper.class))).thenReturn(parentMenus, childrenMenus);

        // 执行测试
        ResponseResult result = menuService.listMenuApi(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "数据不应为null");

        // 验证调用
        verify(menuMapper, times(2)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("测试接口列表-空列表")
    void testListMenuApi_EmptyList() {
        // 准备测试数据
        Integer id = null;
        List<Menu> emptyList = new ArrayList<>();

        // Mock
        when(menuMapper.selectList(any(QueryWrapper.class))).thenReturn(emptyList);

        // 执行测试
        ResponseResult result = menuService.listMenuApi(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "数据不应为null");

        // 验证调用（只调用一次，因为没有父菜单，不会查询子菜单）
        verify(menuMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    // ==================== insertMenu 方法测试 ====================

    @Test
    @DisplayName("测试添加菜单-成功")
    void testInsertMenu_Success() {
        // 准备测试数据
        Menu menu = new Menu();
        menu.setTitle("新菜单");
        menu.setLevel(1);

        // Mock
        when(menuMapper.insert(menu)).thenReturn(1);

        // 执行测试
        ResponseResult result = menuService.insertMenu(menu);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(menuMapper, times(1)).insert(menu);
    }

    @Test
    @DisplayName("测试添加菜单-插入失败")
    void testInsertMenu_InsertFailed() {
        // 准备测试数据
        Menu menu = new Menu();
        menu.setTitle("新菜单");
        menu.setLevel(1);

        // Mock
        when(menuMapper.insert(menu)).thenReturn(0);

        // 执行测试
        ResponseResult result = menuService.insertMenu(menu);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("添加菜单失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(menuMapper, times(1)).insert(menu);
    }

    // ==================== updateMenu 方法测试 ====================

    @Test
    @DisplayName("测试修改菜单-成功")
    void testUpdateMenu_Success() {
        // 准备测试数据
        Menu menu = new Menu();
        menu.setId(1);
        menu.setTitle("更新菜单");
        menu.setLevel(1);

        // Mock
        when(menuMapper.updateById(menu)).thenReturn(1);

        // 执行测试
        ResponseResult result = menuService.updateMenu(menu);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(menuMapper, times(1)).updateById(menu);
    }

    @Test
    @DisplayName("测试修改菜单-更新失败")
    void testUpdateMenu_UpdateFailed() {
        // 准备测试数据
        Menu menu = new Menu();
        menu.setId(1);
        menu.setTitle("更新菜单");
        menu.setLevel(1);

        // Mock
        when(menuMapper.updateById(menu)).thenReturn(0);

        // 执行测试
        ResponseResult result = menuService.updateMenu(menu);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("修改菜单失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(menuMapper, times(1)).updateById(menu);
    }

    // ==================== deleteMenuById 方法测试 ====================

    @Test
    @DisplayName("测试删除菜单-成功")
    void testDeleteMenuById_Success() {
        // 准备测试数据
        Integer id = 1;

        // Mock
        when(menuMapper.deleteById(id)).thenReturn(1);

        // 执行测试
        ResponseResult result = menuService.deleteMenuById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(menuMapper, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("测试删除菜单-删除失败")
    void testDeleteMenuById_DeleteFailed() {
        // 准备测试数据
        Integer id = 1;

        // Mock
        when(menuMapper.deleteById(id)).thenReturn(0);

        // 执行测试
        ResponseResult result = menuService.deleteMenuById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        assertEquals("删除菜单失败", result.getMessage(), "错误消息应匹配");

        // 验证调用
        verify(menuMapper, times(1)).deleteById(id);
    }
}

