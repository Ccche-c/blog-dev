//package com.shiyi.strategy.imp;
//
//import cn.dev33.satoken.stp.StpUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
//import com.shiyi.enums.UserStatusEnum;
//import com.shiyi.vo.SocialTokenVO;
//import com.shiyi.vo.SocialUserInfoVO;
//import com.shiyi.vo.UserDetailVO;
//import com.shiyi.vo.UserInfoVO;
//import com.shiyi.entity.User;
//import com.shiyi.entity.UserInfo;
//import com.shiyi.enums.LoginTypeEnum;
//import com.shiyi.mapper.RoleMapper;
//import com.shiyi.mapper.UserInfoMapper;
//import com.shiyi.mapper.UserMapper;
//import com.shiyi.strategy.SocialLoginStrategy;
//import com.shiyi.utils.*;
//import eu.bitwalker.useragentutils.UserAgent;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.*;
//
//import static com.shiyi.common.ResultCode.DISABLE_ACCOUNT;
//import static com.shiyi.enums.UserStatusEnum.disable;
//
///**
// * 第三方登录抽象模板
// *
// * @author blue
// * @date 2021/07/28
// */
//@Service
//public abstract class AbstractSocialLoginStrategyImpl implements SocialLoginStrategy {
//    @Autowired
//    private UserInfoMapper userInfoMapper;
//    @Autowired
//    private UserMapper userMapper;
//    @Resource
//    private HttpServletRequest request;
//    @Autowired
//    private RoleMapper roleMapper;
//
//    @Override
//    public UserInfoVO login(String data) {
//        // 创建登录信息
//        UserDetailVO userDetailVO;
//        // 获取第三方token信息
//        SocialTokenVO socialToken = getSocialToken(data);
//        // 获取用户ip信息
//        String ipAddress = IpUtil.getIp(request);
//        String ipSource = IpUtil.getIp2region(ipAddress);
//        // 获取第三方用户信息
//        SocialUserInfoVO socialUserInfo = getSocialUserInfo(socialToken);
//        if (socialToken.getLoginType().equals(LoginTypeEnum.GITEE.getType())){
//            socialToken.setOpenId(socialUserInfo.getId());
//        }
//        // 判断是否已注册
//        User user = getUser(socialToken);
//        if (Objects.nonNull(user)) {
//            // 返回数据库用户信息
//            userDetailVO = getUserDetail(user, ipAddress, ipSource,socialUserInfo);
//        } else {
//            // 获取第三方用户信息，保存到数据库返回
//            userDetailVO = saveUserDetail(socialToken, ipAddress, ipSource,socialUserInfo);
//        }
//        // 判断账号是否禁用
//        Assert.isTrue(!userDetailVO.getIsDisable().equals(disable.code),DISABLE_ACCOUNT.desc);
//
//        // 返回用户信息
//        UserInfoVO userInfoVO = BeanCopyUtils.copyObject(userDetailVO, UserInfoVO.class);
//        StpUtil.login(userInfoVO.getId().longValue());
//        userInfoVO.setToken(StpUtil.getTokenValue());
//        return userInfoVO;
//    }
//
//    /**
//     * 获取第三方token信息
//     *
//     * @param data 数据
//     * @return {@link SocialTokenVO} 第三方token信息
//     */
//    public abstract SocialTokenVO getSocialToken(String data);
//
//    /**
//     * 获取第三方用户信息
//     *
//     * @param socialTokenVO 第三方token信息
//     * @return {@link SocialUserInfoVO} 第三方用户信息
//     */
//    public abstract SocialUserInfoVO getSocialUserInfo(SocialTokenVO socialTokenVO);
//
//    /**
//     * 获取用户账号
//     *
//     * @return {@link UserInfo} 用户账号
//     */
//    private User getUser(SocialTokenVO socialTokenVO) {
//        return userMapper.selectOne(new LambdaQueryWrapper<User>()
//                .eq(User::getUsername, socialTokenVO.getOpenId())
//                .eq(User::getLoginType, socialTokenVO.getLoginType()));
//    }
//
//    /**
//     * 获取用户信息
//     *
//     * @param user      用户账号
//     * @param ipAddress ip地址
//     * @param ipSource  ip源
//     * @return {@link UserDetailVO} 用户信息
//     */
//    private UserDetailVO getUserDetail(User user, String ipAddress, String ipSource, SocialUserInfoVO socialUserInfo) {
//        // 更新登录信息
//        userMapper.update(new User(), new LambdaUpdateWrapper<User>()
//                .set(User::getLastLoginTime, LocalDateTime.now())
//                .set(User::getIpAddress, ipAddress)
//                .set(User::getIpSource, ipSource)
//                .eq(User::getId, user.getId()));
//
//        //更新头像和昵称
//        userInfoMapper.update(new UserInfo(),new LambdaUpdateWrapper<UserInfo>()
//                .set(UserInfo::getAvatar, socialUserInfo.getAvatar())
//                .set(UserInfo::getNickname, socialUserInfo.getNickname())
//                .eq(UserInfo::getId, user.getUserInfoId()));
//
//        // 封装信息
//        return convertUserDetail(user);
//    }
//
//
//    /**
//     * 新增用户信息
//     *
//     * @param socialToken token信息
//     * @param ipAddress   ip地址
//     * @param ipSource    ip源
//     * @return {@link UserDetailVO} 用户信息
//     */
//    private UserDetailVO saveUserDetail(SocialTokenVO socialToken, String ipAddress, String ipSource, SocialUserInfoVO socialUserInfo) {
//
//        // 保存用户信息
//        UserInfo userInfo = UserInfo.builder()
//                .nickname(socialUserInfo.getNickname())
//                .avatar(socialUserInfo.getAvatar())
//                .build();
//        userInfoMapper.insert(userInfo);
//        // 保存账号信息
//        User user = User.builder()
//                .userInfoId(userInfo.getId())
//                .username(socialToken.getOpenId())
//                .password(socialToken.getAccessToken())
//                .loginType(socialToken.getLoginType())
//                .lastLoginTime(DateUtil.getNowDate())
//                .ipAddress(ipAddress)
//                .ipSource(ipSource)
//                .roleId(2)
//                .status(UserStatusEnum.normal.getCode())
//                .build();
//        userMapper.insert(user);
//
//        return convertUserDetail(user);
//    }
//
//    private UserDetailVO convertUserDetail(User user) {
//        // 查询账号信息
//        UserInfo userInfo = userInfoMapper.selectById(user.getUserInfoId());
//        // 获取设备信息
//        String ipAddress = IpUtil.getIp(request);
//        String ipSource = IpUtil.getIp2region(ipAddress);
//        UserAgent userAgent = IpUtil.getUserAgent(request);
//
//        // 封装权限集合
//        return UserDetailVO.builder()
//                .id(user.getId())
//                .loginType(user.getLoginType())
//                .userAuthId(userInfo.getId())
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .email(userInfo.getEmail())
//                .nickname(userInfo.getNickname())
//                .avatar(userInfo.getAvatar())
//                .intro(userInfo.getIntro())
//                .webSite(userInfo.getWebSite())
//                .ipAddress(ipAddress)
//                .ipSource(ipSource)
//                .isDisable(user.getStatus())
//                .browser(userAgent.getBrowser().getName())
//                .os(userAgent.getOperatingSystem().getName())
//                .lastLoginTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")))
//                .build();
//    }
//}
