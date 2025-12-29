package com.shiyi.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.ResponseResult;
import com.shiyi.config.satoken.MySaTokenListener;
import com.shiyi.config.satoken.OnlineUser;
import com.shiyi.dto.SystemUserDTO;
import com.shiyi.entity.Menu;
import com.shiyi.entity.User;
import com.shiyi.entity.UserInfo;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.RoleMapper;
import com.shiyi.mapper.UserInfoMapper;
import com.shiyi.mapper.UserMapper;
import com.shiyi.service.MenuService;
import com.shiyi.utils.AesEncryptUtils;
import com.shiyi.utils.PageUtils;
import com.shiyi.vo.SystemUserInfoVO;
import com.shiyi.vo.SystemUserVO;
import com.shiyi.vo.UserInfoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务实现类测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private MenuService menuService;

    @Mock
    private UserInfoMapper userInfoMapper;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper
        Field baseMapperField = userService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(userService, userMapper);
    }

    // ==================== listUser 方法测试 ====================

    @Test
    @DisplayName("测试获取用户列表-成功")
    void testListUser_Success() {
        // 准备测试数据
        String username = "test";
        Integer loginType = 1;
        Page<SystemUserInfoVO> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());
        page.setTotal(0L);

        // 设置分页信息
        PageUtils.setCurrentPage(new Page<>(1L, 10L));

        // Mock
        when(userMapper.selectPageRecord(any(Page.class), eq(username), eq(loginType)))
                .thenReturn(page);

        // 执行测试
        ResponseResult result = userService.listUser(username, loginType);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");

        // 验证调用
        verify(userMapper, times(1)).selectPageRecord(any(Page.class), eq(username), eq(loginType));

        // 清理
        PageUtils.remove();
    }

    // ==================== getUserById 方法测试 ====================

    @Test
    @DisplayName("测试根据ID获取用户-成功")
    void testGetUserById_Success() {
        // 准备测试数据
        Integer id = 1;
        SystemUserVO user = SystemUserVO.builder()
                .id(id)
                .username("test")
                .nickname("测试用户")
                .build();

        // Mock
        when(userMapper.getById(id)).thenReturn(user);

        // 执行测试
        ResponseResult result = userService.getUserById(id);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertEquals(user, result.getData(), "返回数据应匹配");

        // 验证调用
        verify(userMapper, times(1)).getById(id);
    }

    // ==================== insertUser 方法测试 ====================

    @Test
    @DisplayName("测试添加用户-成功")
    void testInsertUser_Success() {
        // 准备测试数据
        SystemUserDTO dto = new SystemUserDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setNickname("测试用户");
        dto.setAvatar("avatar.jpg");
        dto.setStatus(1);

        // Mock
        when(userMapper.selectByUserName(dto.getUsername())).thenReturn(null);
        when(userInfoMapper.insert(any(UserInfo.class))).thenAnswer(invocation -> {
            UserInfo info = invocation.getArgument(0);
            info.setId(1);
            return 1;
        });
        when(userMapper.insert(any(User.class))).thenReturn(1);

        try (MockedStatic<AesEncryptUtils> aesMock = mockStatic(AesEncryptUtils.class)) {
            aesMock.when(() -> AesEncryptUtils.aesEncrypt(dto.getPassword()))
                    .thenReturn("encrypted_password");

            // 执行测试
            ResponseResult result = userService.insertUser(dto);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "返回数据不应为null");

            // 验证调用
            verify(userMapper, times(1)).selectByUserName(dto.getUsername());
            verify(userInfoMapper, times(1)).insert(any(UserInfo.class));
            verify(userMapper, times(1)).insert(any(User.class));
        }
    }

    @Test
    @DisplayName("测试添加用户-用户名已存在")
    void testInsertUser_UsernameExists() {
        // 准备测试数据
        SystemUserDTO dto = new SystemUserDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setNickname("测试用户");

        UserInfoVO existingUser = new UserInfoVO();

        // Mock
        when(userMapper.selectByUserName(dto.getUsername())).thenReturn(existingUser);

        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> userService.insertUser(dto),
                "应抛出用户名已存在异常");

        // 验证调用
        verify(userMapper, times(1)).selectByUserName(dto.getUsername());
        verify(userInfoMapper, never()).insert(any(UserInfo.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    // ==================== updateUser 方法测试 ====================

    @Test
    @DisplayName("测试修改用户-成功")
    void testUpdateUser_Success() {
        // 准备测试数据
        User user = User.builder()
                .id(1)
                .username("testuser")
                .status(1)
                .build();

        // Mock
        when(userMapper.updateById(user)).thenReturn(1);

        // 执行测试
        ResponseResult result = userService.updateUser(user);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(userMapper, times(1)).updateById(user);
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除用户-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Integer> ids = Arrays.asList(1, 2, 3);

        // Mock
        when(userMapper.deleteBatchIds(ids)).thenReturn(3);
        doNothing().when(userInfoMapper).deleteByUserIds(ids);

        // 执行测试
        ResponseResult result = userService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(userMapper, times(1)).deleteBatchIds(ids);
        verify(userInfoMapper, times(1)).deleteByUserIds(ids);
    }

    // ==================== getCurrentUserInfo 方法测试 ====================

    @Test
    @DisplayName("测试获取当前登录用户信息-成功")
    void testGetCurrentUserInfo_Success() {
        // 准备测试数据
        Integer userId = 1;
        SystemUserVO user = SystemUserVO.builder()
                .id(userId)
                .username("testuser")
                .nickname("测试用户")
                .build();

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdAsInt).thenReturn(userId);
            when(userMapper.getById(userId)).thenReturn(user);

            // 执行测试
            ResponseResult result = userService.getCurrentUserInfo();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals("获取当前登录用户信息成功", result.getMessage(), "消息应匹配");
            assertEquals(user, result.getData(), "返回数据应匹配");

            // 验证调用
            verify(userMapper, times(1)).getById(userId);
        }
    }

    // ==================== getCurrentUserMenu 方法测试 ====================

    @Test
    @DisplayName("测试获取当前用户菜单-成功")
    void testGetCurrentUserMenu_Success() {
        // 准备测试数据
        Integer userId = 1;
        List<Integer> menuIds = Arrays.asList(1, 2, 3);
        List<Menu> menus = new ArrayList<>();
        List<Menu> menuTree = new ArrayList<>();

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdAsInt).thenReturn(userId);
            when(userMapper.getMenuId(userId)).thenReturn(menuIds);
            when(menuService.listByIds(menuIds)).thenReturn(menus);
            when(menuService.listMenuTree(menus)).thenReturn(menuTree);

            // 执行测试
            ResponseResult result = userService.getCurrentUserMenu();

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals(menuTree, result.getData(), "返回数据应匹配");

            // 验证调用
            verify(userMapper, times(1)).getMenuId(userId);
            verify(menuService, times(1)).listByIds(menuIds);
            verify(menuService, times(1)).listMenuTree(menus);
        }
    }

    // ==================== updatePassword 方法测试 ====================

    @Test
    @DisplayName("测试修改密码-成功")
    void testUpdatePassword_Success() {
        // 准备测试数据
        Integer userId = 1;
        Map<String, String> map = new HashMap<>();
        map.put("oldPassword", "old123");
        map.put("newPassword", "new123");

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .password("encrypted_old")
                .build();

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<AesEncryptUtils> aesMock = mockStatic(AesEncryptUtils.class)) {
            
            stpUtilMock.when(StpUtil::getLoginIdAsInt).thenReturn(userId);
            when(userMapper.selectById(userId)).thenReturn(user);
            aesMock.when(() -> AesEncryptUtils.validate("encrypted_old", "old123"))
                    .thenReturn(true);
            aesMock.when(() -> AesEncryptUtils.aesEncrypt("new123"))
                    .thenReturn("encrypted_new");
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            // 执行测试
            ResponseResult result = userService.updatePassword(map);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals("SUCCESS", result.getMessage(), "消息应为SUCCESS");
            assertEquals("修改成功", result.getData(), "返回数据应匹配");

            // 验证调用
            verify(userMapper, times(1)).selectById(userId);
            verify(userMapper, times(1)).updateById(any(User.class));
        }
    }

    @Test
    @DisplayName("测试修改密码-旧密码错误")
    void testUpdatePassword_OldPasswordInvalid() {
        // 准备测试数据
        Integer userId = 1;
        Map<String, String> map = new HashMap<>();
        map.put("oldPassword", "wrong");
        map.put("newPassword", "new123");

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .password("encrypted_old")
                .build();

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<AesEncryptUtils> aesMock = mockStatic(AesEncryptUtils.class)) {
            
            stpUtilMock.when(StpUtil::getLoginIdAsInt).thenReturn(userId);
            when(userMapper.selectById(userId)).thenReturn(user);
            aesMock.when(() -> AesEncryptUtils.validate("encrypted_old", "wrong"))
                    .thenReturn(false);

            // 执行测试并验证异常
            assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(map),
                    "应抛出旧密码校验不通过异常");

            // 验证调用
            verify(userMapper, never()).updateById(any(User.class));
        }
    }

    // ==================== listOnlineUsers 方法测试 ====================

    @Test
    @DisplayName("测试获取在线用户列表-成功（无关键词）")
    void testListOnlineUsers_Success_NoKeywords() {
        // 准备测试数据
        PageUtils.setCurrentPage(new Page<>(1L, 10L));

        OnlineUser user1 = OnlineUser.builder()
                .userId(1L)
                .nickname("用户1")
                .tokenValue("token1")
                .loginTime(new Date())
                .build();
        OnlineUser user2 = OnlineUser.builder()
                .userId(2L)
                .nickname("用户2")
                .tokenValue("token2")
                .loginTime(new Date(System.currentTimeMillis() - 1000))
                .build();

        List<OnlineUser> onlineUsers = new ArrayList<>(Arrays.asList(user1, user2));
        MySaTokenListener.ONLINE_USERS.clear();
        MySaTokenListener.ONLINE_USERS.addAll(onlineUsers);

        // 执行测试
        ResponseResult result = userService.listOnlineUsers(null);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");

        // 清理
        PageUtils.remove();
        MySaTokenListener.ONLINE_USERS.clear();
    }

    @Test
    @DisplayName("测试获取在线用户列表-成功（有关键词）")
    void testListOnlineUsers_Success_WithKeywords() {
        // 准备测试数据
        PageUtils.setCurrentPage(new Page<>(1L, 10L));

        OnlineUser user1 = OnlineUser.builder()
                .userId(1L)
                .nickname("张三")
                .tokenValue("token1")
                .loginTime(new Date())
                .build();
        OnlineUser user2 = OnlineUser.builder()
                .userId(2L)
                .nickname("李四")
                .tokenValue("token2")
                .loginTime(new Date(System.currentTimeMillis() - 1000))
                .build();

        MySaTokenListener.ONLINE_USERS.clear();
        MySaTokenListener.ONLINE_USERS.addAll(Arrays.asList(user1, user2));

        // 执行测试
        ResponseResult result = userService.listOnlineUsers("张三");

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 清理
        PageUtils.remove();
        MySaTokenListener.ONLINE_USERS.clear();
    }

    // ==================== kick 方法测试 ====================

    @Test
    @DisplayName("测试踢人下线-成功")
    void testKick_Success() {
        // 准备测试数据
        String token = "test_token";

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(() -> StpUtil.kickoutByTokenValue(token))
                    .thenAnswer(invocation -> null);

            // 执行测试
            ResponseResult result = userService.kick(token);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用 - 静态方法已通过执行测试验证
        }
    }
}

