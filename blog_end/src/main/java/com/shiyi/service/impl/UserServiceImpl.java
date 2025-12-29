package com.shiyi.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiyi.common.ResponseResult;
import com.shiyi.config.satoken.MySaTokenListener;
import com.shiyi.config.satoken.OnlineUser;
import com.shiyi.dto.SystemUserDTO;
import com.shiyi.entity.UserInfo;
import com.shiyi.enums.LoginTypeEnum;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.RoleMapper;
import com.shiyi.vo.SystemUserVO;
import com.shiyi.vo.SystemUserInfoVO;
import com.shiyi.entity.Menu;
import com.shiyi.entity.User;
import com.shiyi.enums.UserStatusEnum;
import com.shiyi.mapper.UserInfoMapper;
import com.shiyi.mapper.UserMapper;
import com.shiyi.service.MenuService;
import com.shiyi.service.UserService;
import com.shiyi.utils.PageUtils;
import com.shiyi.utils.AesEncryptUtils;
import com.shiyi.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static com.shiyi.common.ResultCode.ERROR_USER_NOT_EXIST;

/**
 * @author blue
 * @description:
 * @date 2021/7/30 17:25
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final MenuService menuService;

    private final UserInfoMapper userInfoMapper;

    private final RoleMapper roleMapper;


    /**
     * 用户列表
     * @param username
     * @param loginType
     * @return
     */
    @Override
    public ResponseResult listUser(String username, Integer loginType) {
        Page<SystemUserInfoVO> page = baseMapper.selectPageRecord(new Page<>(PageUtils.getPageNo(), PageUtils.getPageSize()),username,loginType);
        return ResponseResult.success(page);
    }

    /**
     * 用户详情
     * @param id
     * @return
     */
    @Override
    public ResponseResult getUserById(Integer id) {
        SystemUserVO user = baseMapper.getById(id);
        return ResponseResult.success(user);
    }

    /**
     *  添加用户
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult insertUser(SystemUserDTO dto) {
        UserInfoVO userInfoVO = baseMapper.selectByUserName(dto.getUsername());
        if (userInfoVO != null) {
            throw new BusinessException("用户名已存在!");
        }
        //添加用户信息
        UserInfo userInfo = UserInfo.builder().nickname(dto.getNickname()).avatar(dto.getAvatar()).build();
        userInfoMapper.insert(userInfo);

        //添加用户账号
        User user = User.builder().username(dto.getUsername()).password(AesEncryptUtils.aesEncrypt(dto.getPassword()))
                .status(dto.getStatus()).userInfoId(userInfo.getId()).loginType(LoginTypeEnum.EMAIL.getType()).build();

        baseMapper.insert(user);
        return ResponseResult.success(user);
    }

    /**
     *  修改用户
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateUser(User user) {
        baseMapper.updateById(user);
        //roleMapper.updateByUserId(user.getId(),user.getRoleId());
        return ResponseResult.success();
    }

    /**
     * 删除用户
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteBatch(List<Integer> ids) {
        //先删除用户账号
         baseMapper.deleteBatchIds(ids);
         //删除用户信息
        userInfoMapper.deleteByUserIds(ids);
        return ResponseResult.success();
    }

    /**
     * 获取当前登录用户详情
     * @return
     */
    @Override
    public ResponseResult getCurrentUserInfo() {
        return ResponseResult.success("获取当前登录用户信息成功", baseMapper.getById(StpUtil.getLoginIdAsInt()));
    }

    /**
     * 获取当前登录用户所拥有的菜单权限
     * @return
     */
    @Override
    public ResponseResult getCurrentUserMenu() {
        List<Integer> menuIds = baseMapper.getMenuId(StpUtil.getLoginIdAsInt());
        List<Menu> menus = menuService.listByIds(menuIds);
        List<Menu> menuTree = menuService.listMenuTree(menus);
        return ResponseResult.success(menuTree);
    }

    /**
     * 修改密码
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updatePassword(Map<String, String> map) {

        User user = baseMapper.selectById(StpUtil.getLoginIdAsInt());
        Assert.notNull(user,ERROR_USER_NOT_EXIST.getDesc());

        boolean isValid = AesEncryptUtils.validate(user.getPassword(),map.get("oldPassword"));
        Assert.isTrue(isValid,"旧密码校验不通过!");

        String newPassword = AesEncryptUtils.aesEncrypt(map.get("newPassword"));
        user.setPassword(newPassword);
        baseMapper.updateById(user);
        return ResponseResult.success("修改成功");
    }

    /**
     * 在线用户
     * @param keywords
     * @return
     */
    @Override
    public ResponseResult listOnlineUsers(String keywords) {
        int pageNo = PageUtils.getPageNo().intValue();
        int pageSize = PageUtils.getPageSize().intValue();

        List<OnlineUser> onlineUsers = MySaTokenListener.ONLINE_USERS;
        //根据关键词过滤
        if (StringUtils.isNotBlank(keywords)) {
            onlineUsers = MySaTokenListener.ONLINE_USERS.stream().filter(item -> item.getNickname().contains(keywords)).collect(Collectors.toList());
        }
        //排序
        onlineUsers.sort((o1, o2) -> DateUtil.compare(o2.getLoginTime(), o1.getLoginTime()));
        int fromIndex = (pageNo-1) * pageSize;
        int toIndex = onlineUsers.size() - fromIndex > pageSize ? fromIndex + pageSize : onlineUsers.size();
        List<OnlineUser> userOnlineList = onlineUsers.subList(fromIndex, toIndex);
        logger.info("memory用户数：{}", userOnlineList.size());

        Map<String,Object> map = new HashMap<>();
        map.put("total",onlineUsers.size());
        map.put("records",userOnlineList);
        return ResponseResult.success(map);
    }

    /**
     * 踢人下线
     * @param token
     * @return
     */
    @Override
    public ResponseResult kick(String token) {
        logger.info("当前踢下线的用户token为:{}",token);
        StpUtil.kickoutByTokenValue(token);
        return ResponseResult.success();
    }
}
