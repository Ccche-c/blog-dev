package com.shiyi.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Comment;
import com.shiyi.entity.UserInfo;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.CommentMapper;
import com.shiyi.mapper.UserInfoMapper;
import com.shiyi.utils.IpUtil;
import com.shiyi.utils.PageUtils;
import com.shiyi.vo.ApiCommentListVO;
import com.shiyi.vo.SystemCommentVO;
import eu.bitwalker.useragentutils.UserAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CommentServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("评论服务实现类测试")
@SuppressWarnings({"rawtypes", "unchecked"})
class CommentServiceImplTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserInfoMapper userInfoMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        // 使用反射注入 baseMapper
        ReflectionTestUtils.setField(commentService, "baseMapper", commentMapper);
    }

    // ==================== listComment 方法测试 ====================

    @Test
    @DisplayName("测试评论列表-成功")
    void testListComment_Success() {
        // 准备测试数据
        String keywords = "test";
        Page<SystemCommentVO> page = new Page<>(1L, 10L);
        List<SystemCommentVO> records = new ArrayList<>();
        SystemCommentVO vo = SystemCommentVO.builder()
                .id(1)
                .nickname("test_user")
                .content("test comment")
                .build();
        records.add(vo);
        page.setRecords(records);

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(commentMapper.selectPageList(any(Page.class), eq(keywords))).thenReturn(page);

            // 执行测试
            ResponseResult result = commentService.listComment(keywords);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "数据不应为null");

            // 验证调用
            verify(commentMapper, times(1)).selectPageList(any(Page.class), eq(keywords));
        }
    }

    @Test
    @DisplayName("测试评论列表-无关键词")
    void testListComment_NoKeywords() {
        // 准备测试数据
        String keywords = null;
        Page<SystemCommentVO> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());

        // Mock
        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            pageUtilsMock.when(PageUtils::getPageNo).thenReturn(1L);
            pageUtilsMock.when(PageUtils::getPageSize).thenReturn(10L);
            when(commentMapper.selectPageList(any(Page.class), eq(keywords))).thenReturn(page);

            // 执行测试
            ResponseResult result = commentService.listComment(keywords);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(commentMapper, times(1)).selectPageList(any(Page.class), eq(keywords));
        }
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除评论-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Integer> ids = Arrays.asList(1, 2, 3);

        // Mock (deleteBatchIds 返回 int，不是 void)
        when(commentMapper.deleteBatchIds(ids)).thenReturn(3);

        // 执行测试
        ResponseResult result = commentService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(commentMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试批量删除评论-空列表")
    void testDeleteBatch_EmptyList() {
        // 准备测试数据
        List<Integer> ids = new ArrayList<>();

        // Mock (deleteBatchIds 返回 int，不是 void)
        when(commentMapper.deleteBatchIds(ids)).thenReturn(0);

        // 执行测试
        ResponseResult result = commentService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(commentMapper, times(1)).deleteBatchIds(ids);
    }

    // ==================== publicAddComment 方法测试 ====================

    @Test
    @DisplayName("测试发表评论-成功（Mac系统）")
    void testPublicAddComment_Success_Mac() throws Exception {
        // 准备测试数据
        Comment comment = Comment.builder()
                .content("test comment")
                .articleId(1)
                .build();
        String userAgentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)";
        String ip = "127.0.0.1";
        String ipAddress = "北京市";

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class);
             MockedStatic<UserAgent> userAgentMock = mockStatic(UserAgent.class)) {

            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            when(request.getHeader("user-agent")).thenReturn(userAgentString);
            
            UserAgent userAgent = mock(UserAgent.class);
            eu.bitwalker.useragentutils.OperatingSystem os = mock(eu.bitwalker.useragentutils.OperatingSystem.class);
            when(os.getName()).thenReturn("Mac OS X");
            when(userAgent.getOperatingSystem()).thenReturn(os);
            userAgentMock.when(() -> UserAgent.parseUserAgentString(userAgentString)).thenReturn(userAgent);

            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn(ip);
            ipUtilMock.when(() -> IpUtil.getIp2region(ip)).thenReturn(ipAddress);

            when(commentMapper.insert(any(Comment.class))).thenReturn(1);

            // 执行测试
            ResponseResult result = commentService.publicAddComment(comment);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals("mac", comment.getSystem(), "系统应为mac");
            assertEquals("Mac OS X", comment.getSystemVersion(), "系统版本应匹配");
            assertEquals(ipAddress, comment.getIpAddress(), "IP地址应匹配");
            assertEquals(1L, comment.getUserId(), "用户ID应匹配");

            // 验证调用
            verify(commentMapper, times(1)).insert(any(Comment.class));
        }
    }

    @Test
    @DisplayName("测试发表评论-成功（Windows系统）")
    void testPublicAddComment_Success_Windows() throws Exception {
        // 准备测试数据
        Comment comment = Comment.builder()
                .content("test comment")
                .articleId(1)
                .build();
        String userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
        String ip = "127.0.0.1";
        String ipAddress = "上海市";

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class);
             MockedStatic<UserAgent> userAgentMock = mockStatic(UserAgent.class)) {

            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            when(request.getHeader("user-agent")).thenReturn(userAgentString);
            
            UserAgent userAgent = mock(UserAgent.class);
            eu.bitwalker.useragentutils.OperatingSystem os = mock(eu.bitwalker.useragentutils.OperatingSystem.class);
            when(os.getName()).thenReturn("Windows 10");
            when(userAgent.getOperatingSystem()).thenReturn(os);
            userAgentMock.when(() -> UserAgent.parseUserAgentString(userAgentString)).thenReturn(userAgent);

            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn(ip);
            ipUtilMock.when(() -> IpUtil.getIp2region(ip)).thenReturn(ipAddress);

            when(commentMapper.insert(any(Comment.class))).thenReturn(1);

            // 执行测试
            ResponseResult result = commentService.publicAddComment(comment);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals("windowns", comment.getSystem(), "系统应为windowns");
            assertEquals("Windows 10", comment.getSystemVersion(), "系统版本应匹配");

            // 验证调用
            verify(commentMapper, times(1)).insert(any(Comment.class));
        }
    }

    @Test
    @DisplayName("测试发表评论-成功（Android系统）")
    void testPublicAddComment_Success_Android() throws Exception {
        // 准备测试数据
        Comment comment = Comment.builder()
                .content("test comment")
                .articleId(1)
                .build();
        String userAgentString = "Mozilla/5.0 (Linux; Android 11)";
        String ip = "127.0.0.1";
        String ipAddress = "广州市";

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class);
             MockedStatic<UserAgent> userAgentMock = mockStatic(UserAgent.class)) {

            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            when(request.getHeader("user-agent")).thenReturn(userAgentString);
            
            UserAgent userAgent = mock(UserAgent.class);
            eu.bitwalker.useragentutils.OperatingSystem os = mock(eu.bitwalker.useragentutils.OperatingSystem.class);
            when(os.getName()).thenReturn("Android");
            when(userAgent.getOperatingSystem()).thenReturn(os);
            userAgentMock.when(() -> UserAgent.parseUserAgentString(userAgentString)).thenReturn(userAgent);

            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn(ip);
            ipUtilMock.when(() -> IpUtil.getIp2region(ip)).thenReturn(ipAddress);

            when(commentMapper.insert(any(Comment.class))).thenReturn(1);

            // 执行测试
            ResponseResult result = commentService.publicAddComment(comment);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals("android", comment.getSystem(), "系统应为android");
            assertEquals("Android", comment.getSystemVersion(), "系统版本应匹配");

            // 验证调用
            verify(commentMapper, times(1)).insert(any(Comment.class));
        }
    }

    @Test
    @DisplayName("测试发表评论-插入失败")
    void testPublicAddComment_InsertFailed() throws Exception {
        // 准备测试数据
        Comment comment = Comment.builder()
                .content("test comment")
                .articleId(1)
                .build();
        String userAgentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)";
        String ip = "127.0.0.1";
        String ipAddress = "北京市";

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<IpUtil> ipUtilMock = mockStatic(IpUtil.class);
             MockedStatic<UserAgent> userAgentMock = mockStatic(UserAgent.class)) {

            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            when(request.getHeader("user-agent")).thenReturn(userAgentString);
            
            UserAgent userAgent = mock(UserAgent.class);
            eu.bitwalker.useragentutils.OperatingSystem os = mock(eu.bitwalker.useragentutils.OperatingSystem.class);
            when(os.getName()).thenReturn("Mac OS X");
            when(userAgent.getOperatingSystem()).thenReturn(os);
            userAgentMock.when(() -> UserAgent.parseUserAgentString(userAgentString)).thenReturn(userAgent);

            ipUtilMock.when(() -> IpUtil.getIp(request)).thenReturn(ip);
            ipUtilMock.when(() -> IpUtil.getIp2region(ip)).thenReturn(ipAddress);

            when(commentMapper.insert(any(Comment.class))).thenReturn(0);

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                commentService.publicAddComment(comment);
            });

            assertEquals("评论失败", exception.getMessage(), "异常消息应匹配");

            // 验证调用
            verify(commentMapper, times(1)).insert(any(Comment.class));
        }
    }

    // ==================== selectCommentByArticleId 方法测试 ====================

    @Test
    @DisplayName("测试根据文章ID查询评论-成功")
    void testSelectCommentByArticleId_Success() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = 1L;

        // 父级评论
        Page<ApiCommentListVO> page = new Page<>(pageNo, pageSize);
        // 使用 new 创建对象，确保 children 字段被正确初始化
        ApiCommentListVO parentComment = new ApiCommentListVO();
        parentComment.setId(1);
        parentComment.setUserId(1L);
        parentComment.setContent("parent comment");
        parentComment.setNickname("user1");
        parentComment.setChildren(new ArrayList<>()); // 显式初始化 children 列表
        List<ApiCommentListVO> parentRecords = new ArrayList<>();
        parentRecords.add(parentComment);
        page.setRecords(parentRecords);

        // 子级评论
        Comment childComment = Comment.builder()
                .id(2)
                .userId(2L)
                .replyUserId(1L)
                .parentId(1)
                .content("child comment")
                .build();
        List<Comment> childComments = new ArrayList<>();
        childComments.add(childComment);

        // 用户信息
        // 回复用户信息（e.getReplyUserId() = 1L，先调用）
        UserInfo replyUserInfo = new UserInfo();
        replyUserInfo.setNickname("user1");
        replyUserInfo.setWebSite("http://example.com");
        replyUserInfo.setAvatar("avatar1.jpg");

        // 子评论的用户信息（e.getUserId() = 2L，后调用）
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setNickname("user2");
        userInfo1.setWebSite("http://example2.com");
        userInfo1.setAvatar("avatar2.jpg");

        // Mock
        when(commentMapper.selectCommentPage(any(Page.class), eq(articleId))).thenReturn(page);
        when(commentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(childComments);
        // 注意：代码中先调用 getByUserId(e.getReplyUserId())，再调用 getByUserId(e.getUserId())
        // replyUserId = 1L, userId = 2L
        when(userInfoMapper.getByUserId(1L)).thenReturn(replyUserInfo);
        when(userInfoMapper.getByUserId(2L)).thenReturn(userInfo1);

        // 执行测试
        ResponseResult result = commentService.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "数据不应为null");

        // 验证调用
        verify(commentMapper, times(1)).selectCommentPage(any(Page.class), eq(articleId));
        verify(commentMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
        verify(userInfoMapper, times(1)).getByUserId(2L);
        verify(userInfoMapper, times(1)).getByUserId(1L);
    }

    @Test
    @DisplayName("测试根据文章ID查询评论-无评论")
    void testSelectCommentByArticleId_NoComments() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = 1L;

        // 空页面
        Page<ApiCommentListVO> page = new Page<>(pageNo, pageSize);
        page.setRecords(new ArrayList<>());

        // Mock
        when(commentMapper.selectCommentPage(any(Page.class), eq(articleId))).thenReturn(page);

        // 执行测试
        ResponseResult result = commentService.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(commentMapper, times(1)).selectCommentPage(any(Page.class), eq(articleId));
        verify(commentMapper, never()).selectList(any(LambdaQueryWrapper.class));
    }
}

