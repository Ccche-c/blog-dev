package com.shiyi.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Job;
import com.shiyi.entity.User;
import com.shiyi.enums.ScheduleConstants;
import com.shiyi.enums.TaskException;
import com.shiyi.mapper.JobMapper;
import com.shiyi.mapper.UserMapper;
import com.shiyi.quartz.CronUtils;
import com.shiyi.quartz.ScheduleUtils;
import com.shiyi.utils.PageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * JobServiceImpl 单元测试
 * 
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("定时任务服务实现类测试")
class JobServiceImplTest {

    @Mock
    private JobMapper jobMapper;

    @Mock
    private Scheduler scheduler;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JobServiceImpl jobService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper
        Field baseMapperField = jobService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(jobService, jobMapper);
    }

    // ==================== listJob 方法测试 ====================

    @Test
    @DisplayName("测试获取任务列表-成功（无过滤条件）")
    void testListJob_Success_NoFilters() {
        // 准备测试数据
        PageUtils.setCurrentPage(new Page<>(1L, 10L));
        Page<Job> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());
        page.setTotal(0L);

        // Mock
        when(jobMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(page);

        // 执行测试
        ResponseResult result = jobService.listJob(null, null, null);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");
        assertNotNull(result.getData(), "返回数据不应为null");

        // 验证调用
        verify(jobMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));

        // 清理
        PageUtils.remove();
    }

    @Test
    @DisplayName("测试获取任务列表-成功（带过滤条件）")
    void testListJob_Success_WithFilters() {
        // 准备测试数据
        String jobName = "测试任务";
        String jobGroup = "DEFAULT";
        String status = "0";
        PageUtils.setCurrentPage(new Page<>(1L, 10L));
        Page<Job> page = new Page<>(1L, 10L);
        page.setRecords(new ArrayList<>());
        page.setTotal(0L);

        // Mock
        when(jobMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(page);

        // 执行测试
        ResponseResult result = jobService.listJob(jobName, jobGroup, status);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(jobMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));

        // 清理
        PageUtils.remove();
    }

    // ==================== getJobById 方法测试 ====================

    @Test
    @DisplayName("测试根据ID获取任务-成功")
    void testGetJobById_Success() {
        // 准备测试数据
        Long jobId = 1L;
        Job job = new Job();
        job.setJobId(jobId);
        job.setJobName("测试任务");
        job.setCronExpression("0 0 12 * * ?");
        Date nextExecution = new Date();

        // Mock
        when(jobMapper.selectById(jobId)).thenReturn(job);
        try (MockedStatic<CronUtils> cronUtilsMock = mockStatic(CronUtils.class)) {
            cronUtilsMock.when(() -> CronUtils.getNextExecution(job.getCronExpression()))
                    .thenReturn(nextExecution);

            // 执行测试
            ResponseResult result = jobService.getJobById(jobId);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertNotNull(result.getData(), "返回数据不应为null");
            Job resultJob = (Job) result.getData();
            assertEquals(nextExecution, resultJob.getNextValidTime(), "下次执行时间应匹配");

            // 验证调用
            verify(jobMapper, times(1)).selectById(jobId);
        }
    }

    // ==================== insertJob 方法测试 ====================

    @Test
    @DisplayName("测试新增任务-成功")
    void testInsertJob_Success() throws SchedulerException, TaskException {
        // 准备测试数据
        Job job = new Job();
        job.setJobName("测试任务");
        job.setJobGroup("DEFAULT");
        job.setCronExpression("0 0 12 * * ?");
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());

        User user = new User();
        user.setUsername("admin");

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<CronUtils> cronUtilsMock = mockStatic(CronUtils.class);
             MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            
            stpUtilMock.when(StpUtil::getLoginIdAsInt).thenReturn(1);
            when(userMapper.selectById(1)).thenReturn(user);
            cronUtilsMock.when(() -> CronUtils.isValid(job.getCronExpression())).thenReturn(true);
            when(jobMapper.insert(job)).thenAnswer(invocation -> {
                Job j = invocation.getArgument(0);
                j.setJobId(1L);
                return 1;
            });
            scheduleUtilsMock.when(() -> ScheduleUtils.createScheduleJob(any(Scheduler.class), any(Job.class)))
                    .thenAnswer(invocation -> null);

            // 执行测试
            ResponseResult result = jobService.insertJob(job);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(userMapper, times(1)).selectById(1);
            verify(jobMapper, times(1)).insert(job);
            // 静态方法已通过执行测试验证
        }
    }

    @Test
    @DisplayName("测试新增任务-Cron表达式无效")
    void testInsertJob_InvalidCron() {
        // 准备测试数据
        Job job = new Job();
        job.setJobName("测试任务");
        job.setCronExpression("invalid cron");

        // Mock
        try (MockedStatic<CronUtils> cronUtilsMock = mockStatic(CronUtils.class)) {
            cronUtilsMock.when(() -> CronUtils.isValid(job.getCronExpression())).thenReturn(false);

            // 执行测试并验证异常
            assertThrows(IllegalArgumentException.class, () -> jobService.insertJob(job),
                    "应抛出Cron表达式无效异常");

            // 验证调用
            verify(jobMapper, never()).insert(any(Job.class));
        }
    }

    // ==================== updateJob 方法测试 ====================

    @Test
    @DisplayName("测试更新任务-成功")
    void testUpdateJob_Success() throws SchedulerException, TaskException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobName("更新后的任务");
        job.setJobGroup("DEFAULT");
        job.setCronExpression("0 0 12 * * ?");
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());

        Job existingJob = new Job();
        existingJob.setJobId(1L);
        existingJob.setJobGroup("DEFAULT");

        User user = new User();
        user.setUsername("admin");

        // Mock
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class);
             MockedStatic<CronUtils> cronUtilsMock = mockStatic(CronUtils.class);
             MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            
            stpUtilMock.when(StpUtil::getLoginIdAsInt).thenReturn(1);
            when(userMapper.selectById(1)).thenReturn(user);
            when(jobMapper.selectById(1L)).thenReturn(existingJob);
            cronUtilsMock.when(() -> CronUtils.isValid(job.getCronExpression())).thenReturn(true);
            when(jobMapper.updateById(job)).thenReturn(1);
            JobKey jobKey = mock(JobKey.class);
            scheduleUtilsMock.when(() -> ScheduleUtils.getJobKey(anyLong(), anyString()))
                    .thenReturn(jobKey);
            when(scheduler.checkExists(jobKey)).thenReturn(false);
            scheduleUtilsMock.when(() -> ScheduleUtils.createScheduleJob(any(Scheduler.class), any(Job.class)))
                    .thenAnswer(invocation -> null);

            // 执行测试
            ResponseResult result = jobService.updateJob(job);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(userMapper, times(1)).selectById(1);
            verify(jobMapper, times(1)).selectById(1L);
            verify(jobMapper, times(1)).updateById(job);
        }
    }

    // ==================== deleteJob 方法测试 ====================

    @Test
    @DisplayName("测试删除任务-成功")
    void testDeleteJob_Success() throws SchedulerException {
        // 准备测试数据
        Long jobId = 1L;
        Job job = new Job();
        job.setJobId(jobId);
        job.setJobGroup("DEFAULT");

        // Mock
        try (MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            when(jobMapper.selectById(jobId)).thenReturn(job);
            when(jobMapper.deleteById(jobId)).thenReturn(1);
            JobKey jobKey = mock(JobKey.class);
            scheduleUtilsMock.when(() -> ScheduleUtils.getJobKey(jobId, job.getJobGroup()))
                    .thenReturn(jobKey);
            when(scheduler.deleteJob(jobKey)).thenReturn(true);

            // 执行测试
            ResponseResult result = jobService.deleteJob(jobId);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(jobMapper, times(1)).selectById(jobId);
            verify(jobMapper, times(1)).deleteById(jobId);
            verify(scheduler, times(1)).deleteJob(jobKey);
        }
    }

    @Test
    @DisplayName("测试删除任务-删除失败")
    void testDeleteJob_DeleteFailed() throws SchedulerException {
        // 准备测试数据
        Long jobId = 1L;
        Job job = new Job();
        job.setJobId(jobId);
        job.setJobGroup("DEFAULT");

        // Mock
        when(jobMapper.selectById(jobId)).thenReturn(job);
        when(jobMapper.deleteById(jobId)).thenReturn(0); // 删除失败

        // 执行测试
        ResponseResult result = jobService.deleteJob(jobId);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用（删除失败时不应调用 scheduler.deleteJob）
        verify(jobMapper, times(1)).selectById(jobId);
        verify(jobMapper, times(1)).deleteById(jobId);
        verify(scheduler, never()).deleteJob(any(JobKey.class));
    }

    // ==================== deleteBatch 方法测试 ====================

    @Test
    @DisplayName("测试批量删除任务-成功")
    void testDeleteBatch_Success() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // Mock
        when(jobMapper.deleteBatchIds(ids)).thenReturn(3);

        // 执行测试
        ResponseResult result = jobService.deleteBatch(ids);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用
        verify(jobMapper, times(1)).deleteBatchIds(ids);
    }

    // ==================== pauseJob 方法测试 ====================

    @Test
    @DisplayName("测试暂停任务-成功")
    void testPauseJob_Success() throws SchedulerException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobGroup("DEFAULT");

        // Mock
        try (MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            when(jobMapper.updateById(job)).thenReturn(1);
            JobKey jobKey = mock(JobKey.class);
            scheduleUtilsMock.when(() -> ScheduleUtils.getJobKey(1L, job.getJobGroup()))
                    .thenReturn(jobKey);
            doNothing().when(scheduler).pauseJob(jobKey);

            // 执行测试
            ResponseResult result = jobService.pauseJob(job);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");
            assertEquals(ScheduleConstants.Status.PAUSE.getValue(), job.getStatus(), "状态应为暂停");

            // 验证调用
            verify(jobMapper, times(1)).updateById(job);
            verify(scheduler, times(1)).pauseJob(jobKey);
        }
    }

    @Test
    @DisplayName("测试暂停任务-更新失败")
    void testPauseJob_UpdateFailed() throws SchedulerException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobGroup("DEFAULT");

        // Mock
        when(jobMapper.updateById(job)).thenReturn(0); // 更新失败

        // 执行测试
        ResponseResult result = jobService.pauseJob(job);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用（更新失败时不应调用 scheduler.pauseJob）
        verify(jobMapper, times(1)).updateById(job);
        verify(scheduler, never()).pauseJob(any(JobKey.class));
    }

    // ==================== run 方法测试 ====================

    @Test
    @DisplayName("测试立即运行任务-成功")
    void testRun_Success() throws SchedulerException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobGroup("DEFAULT");

        // Mock
        try (MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            JobKey jobKey = mock(JobKey.class);
            scheduleUtilsMock.when(() -> ScheduleUtils.getJobKey(1L, job.getJobGroup()))
                    .thenReturn(jobKey);
            doNothing().when(scheduler).triggerJob(eq(jobKey), any(JobDataMap.class));

            // 执行测试
            ResponseResult result = jobService.run(job);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(scheduler, times(1)).triggerJob(eq(jobKey), any(JobDataMap.class));
        }
    }

    @Test
    @DisplayName("测试立即运行任务-运行失败")
    void testRun_Failed() throws SchedulerException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobGroup("DEFAULT");

        // Mock
        try (MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            JobKey jobKey = mock(JobKey.class);
            scheduleUtilsMock.when(() -> ScheduleUtils.getJobKey(1L, job.getJobGroup()))
                    .thenReturn(jobKey);
            doThrow(new SchedulerException("调度器异常")).when(scheduler)
                    .triggerJob(eq(jobKey), any(JobDataMap.class));

            // 执行测试并验证异常
            assertThrows(Exception.class, () -> jobService.run(job),
                    "应抛出定时任务运行失败异常");
        }
    }

    // ==================== changeStatus 方法测试 ====================

    @Test
    @DisplayName("测试修改任务状态-恢复运行")
    void testChangeStatus_Resume() throws SchedulerException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobGroup("DEFAULT");
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());

        // Mock
        try (MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            when(jobMapper.updateById(job)).thenReturn(1);
            JobKey jobKey = mock(JobKey.class);
            scheduleUtilsMock.when(() -> ScheduleUtils.getJobKey(1L, job.getJobGroup()))
                    .thenReturn(jobKey);
            doNothing().when(scheduler).resumeJob(jobKey);

            // 执行测试
            ResponseResult result = jobService.changeStatus(job);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(jobMapper, times(1)).updateById(job);
            verify(scheduler, times(1)).resumeJob(jobKey);
            verify(scheduler, never()).pauseJob(any(JobKey.class));
        }
    }

    @Test
    @DisplayName("测试修改任务状态-暂停")
    void testChangeStatus_Pause() throws SchedulerException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobGroup("DEFAULT");
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());

        // Mock
        try (MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            when(jobMapper.updateById(job)).thenReturn(1);
            JobKey jobKey = mock(JobKey.class);
            scheduleUtilsMock.when(() -> ScheduleUtils.getJobKey(1L, job.getJobGroup()))
                    .thenReturn(jobKey);
            doNothing().when(scheduler).pauseJob(jobKey);

            // 执行测试
            ResponseResult result = jobService.changeStatus(job);

            // 验证结果
            assertNotNull(result, "返回结果不应为null");
            assertEquals(200, result.getCode(), "响应码应为200");

            // 验证调用
            verify(jobMapper, times(1)).updateById(job);
            verify(scheduler, times(1)).pauseJob(jobKey);
            verify(scheduler, never()).resumeJob(any(JobKey.class));
        }
    }

    @Test
    @DisplayName("测试修改任务状态-更新失败")
    void testChangeStatus_UpdateFailed() throws SchedulerException {
        // 准备测试数据
        Job job = new Job();
        job.setJobId(1L);
        job.setJobGroup("DEFAULT");
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());

        // Mock
        when(jobMapper.updateById(job)).thenReturn(0); // 更新失败

        // 执行测试
        ResponseResult result = jobService.changeStatus(job);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(200, result.getCode(), "响应码应为200");

        // 验证调用（更新失败时不应调用 scheduler 方法）
        verify(jobMapper, times(1)).updateById(job);
        verify(scheduler, never()).resumeJob(any(JobKey.class));
        verify(scheduler, never()).pauseJob(any(JobKey.class));
    }

    // ==================== init 方法测试 ====================

    @Test
    @DisplayName("测试初始化-成功")
    void testInit_Success() throws Exception {
        // 准备测试数据
        List<Job> jobList = new ArrayList<>();
        Job job1 = new Job();
        job1.setJobId(1L);
        job1.setJobGroup("DEFAULT");
        job1.setCronExpression("0 0 12 * * ?");
        jobList.add(job1);

        // Mock
        try (MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {
            doNothing().when(scheduler).clear();
            when(jobMapper.selectList(null)).thenReturn(jobList);
            scheduleUtilsMock.when(() -> ScheduleUtils.createScheduleJob(any(Scheduler.class), any(Job.class)))
                    .thenAnswer(invocation -> null);

            // 执行初始化
            Method initMethod = JobServiceImpl.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(jobService);

            // 验证调用
            verify(scheduler, times(1)).clear();
            verify(jobMapper, times(1)).selectList(null);
            // 静态方法已通过执行测试验证
        }
    }
}

