package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Comment;
import com.shiyi.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ApiCommentController 单元测试
 * 使用JaCoCo进行代码覆盖率测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("评论接口测试")
class ApiCommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private ApiCommentController apiCommentController;

    @BeforeEach
    void setUp() {
        // 测试前的初始化操作
        reset(commentService);
    }

    // ==================== publicAddComment 方法测试 ====================

    @Test
    @DisplayName("测试添加评论-成功场景")
    void testPublicAddComment_Success() {
        // 准备测试数据
        Comment comment = new Comment();
        comment.setArticleId(1);
        comment.setContent("这是一条测试评论");
        comment.setUserId(100L);
        
        Comment savedComment = new Comment();
        savedComment.setId(1);
        savedComment.setArticleId(1);
        savedComment.setContent("这是一条测试评论");
        savedComment.setUserId(100L);
        
        ResponseResult expectedResult = ResponseResult.success("评论添加成功", savedComment);

        // 模拟Service返回
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.publicAddComment(comment);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(commentService, times(1)).publicAddComment(any(Comment.class));
    }

    @Test
    @DisplayName("测试添加评论-回复评论")
    void testPublicAddComment_ReplyComment() {
        // 准备测试数据（回复评论）
        Comment comment = new Comment();
        comment.setArticleId(1);
        comment.setContent("这是一条回复评论");
        comment.setUserId(100L);
        comment.setParentId(1); // 父评论ID
        comment.setReplyUserId(99L); // 被回复用户ID
        
        Comment savedComment = new Comment();
        savedComment.setId(2);
        savedComment.setArticleId(1);
        savedComment.setContent("这是一条回复评论");
        savedComment.setParentId(1);
        savedComment.setReplyUserId(99L);
        
        ResponseResult expectedResult = ResponseResult.success("评论添加成功", savedComment);

        // 模拟Service返回
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.publicAddComment(comment);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).publicAddComment(any(Comment.class));
    }

    @Test
    @DisplayName("测试添加评论-空内容")
    void testPublicAddComment_EmptyContent() {
        // 准备测试数据（空内容）
        Comment comment = new Comment();
        comment.setArticleId(1);
        comment.setContent("");
        
        ResponseResult expectedResult = ResponseResult.error("评论内容不能为空");

        // 模拟Service返回
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.publicAddComment(comment);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(commentService, times(1)).publicAddComment(any(Comment.class));
    }

    @Test
    @DisplayName("测试添加评论-Null内容")
    void testPublicAddComment_NullContent() {
        // 准备测试数据（null内容）
        Comment comment = new Comment();
        comment.setArticleId(1);
        comment.setContent(null);
        
        ResponseResult expectedResult = ResponseResult.error("评论内容不能为空");

        // 模拟Service返回
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.publicAddComment(comment);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(commentService, times(1)).publicAddComment(any(Comment.class));
    }

    @Test
    @DisplayName("测试添加评论-长内容")
    void testPublicAddComment_LongContent() {
        // 准备测试数据（长内容）
        Comment comment = new Comment();
        comment.setArticleId(1);
        // 生成一个很长的评论内容
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("这是一条很长的评论内容。");
        }
        comment.setContent(longContent.toString());
        
        Comment savedComment = new Comment();
        savedComment.setId(1);
        savedComment.setContent(longContent.toString());
        
        ResponseResult expectedResult = ResponseResult.success("评论添加成功", savedComment);

        // 模拟Service返回
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.publicAddComment(comment);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).publicAddComment(any(Comment.class));
    }

    @Test
    @DisplayName("测试添加评论-Service返回错误")
    void testPublicAddComment_ServiceError() {
        // 准备测试数据
        Comment comment = new Comment();
        comment.setArticleId(1);
        comment.setContent("这是一条测试评论");
        
        ResponseResult expectedResult = ResponseResult.error("评论添加失败");

        // 模拟Service返回错误
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.publicAddComment(comment);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(commentService, times(1)).publicAddComment(any(Comment.class));
    }

    @Test
    @DisplayName("测试添加评论-验证参数传递")
    void testPublicAddComment_VerifyParameter() {
        // 准备测试数据
        Comment comment = new Comment();
        comment.setArticleId(1);
        comment.setContent("这是一条测试评论");
        comment.setUserId(100L);
        comment.setParentId(1);
        
        ResponseResult expectedResult = ResponseResult.success("评论添加成功", comment);

        // 模拟Service返回
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(expectedResult);

        // 执行测试
        apiCommentController.publicAddComment(comment);

        // 验证Service调用时传递的参数
        verify(commentService, times(1)).publicAddComment(argThat(c -> 
            c.getArticleId().equals(1) && 
            c.getContent().equals("这是一条测试评论") &&
            c.getUserId().equals(100L) &&
            c.getParentId().equals(1)
        ));
    }

    // ==================== selectCommentByArticleId 方法测试 ====================

    @Test
    @DisplayName("测试根据文章ID获取评论-成功场景")
    void testSelectCommentByArticleId_Success() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功", "评论列表数据");

        // 模拟Service返回
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");
        verify(commentService, times(1)).selectCommentByArticleId(pageNo, pageSize, articleId);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-第一页")
    void testSelectCommentByArticleId_FirstPage() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).selectCommentByArticleId(1, 10, 1L);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-第二页")
    void testSelectCommentByArticleId_SecondPage() {
        // 准备测试数据
        int pageNo = 2;
        int pageSize = 10;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).selectCommentByArticleId(2, 10, 1L);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-不同页面大小")
    void testSelectCommentByArticleId_DifferentPageSize() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 20;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).selectCommentByArticleId(1, 20, 1L);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-不同文章ID")
    void testSelectCommentByArticleId_DifferentArticleId() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = 999L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).selectCommentByArticleId(1, 10, 999L);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-空结果")
    void testSelectCommentByArticleId_EmptyResult() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功", null);

        // 模拟Service返回空结果
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).selectCommentByArticleId(pageNo, pageSize, articleId);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-Service返回错误")
    void testSelectCommentByArticleId_ServiceError() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.error("获取评论失败");

        // 模拟Service返回错误
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(commentService, times(1)).selectCommentByArticleId(pageNo, pageSize, articleId);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-边界值测试（pageNo=0）")
    void testSelectCommentByArticleId_BoundaryPageNoZero() {
        // 准备测试数据
        int pageNo = 0;
        int pageSize = 10;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).selectCommentByArticleId(0, 10, 1L);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-边界值测试（pageSize=1）")
    void testSelectCommentByArticleId_BoundaryPageSizeOne() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 1;
        Long articleId = 1L;
        
        ResponseResult expectedResult = ResponseResult.success("获取成功");

        // 模拟Service返回
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        verify(commentService, times(1)).selectCommentByArticleId(1, 1, 1L);
    }

    @Test
    @DisplayName("测试根据文章ID获取评论-边界值测试（articleId=null）")
    void testSelectCommentByArticleId_NullArticleId() {
        // 准备测试数据
        int pageNo = 1;
        int pageSize = 10;
        Long articleId = null;
        
        ResponseResult expectedResult = ResponseResult.error("文章ID不能为空");

        // 模拟Service返回错误
        when(commentService.selectCommentByArticleId(pageNo, pageSize, articleId))
                .thenReturn(expectedResult);

        // 执行测试
        ResponseResult result = apiCommentController.selectCommentByArticleId(pageNo, pageSize, articleId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertNotEquals(200, result.getCode(), "响应码不应为200");
        verify(commentService, times(1)).selectCommentByArticleId(pageNo, pageSize, null);
    }

    @Test
    @DisplayName("测试所有方法-验证Service调用")
    void testAllMethods_VerifyServiceCalls() {
        // 测试publicAddComment
        Comment comment = new Comment();
        comment.setContent("测试评论");
        when(commentService.publicAddComment(any(Comment.class)))
                .thenReturn(ResponseResult.success());
        apiCommentController.publicAddComment(comment);
        verify(commentService, times(1)).publicAddComment(any(Comment.class));

        // 测试selectCommentByArticleId
        when(commentService.selectCommentByArticleId(anyInt(), anyInt(), anyLong()))
                .thenReturn(ResponseResult.success());
        apiCommentController.selectCommentByArticleId(1, 10, 1L);
        verify(commentService, times(1)).selectCommentByArticleId(1, 10, 1L);
    }
}

