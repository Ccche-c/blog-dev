package com.shiyi.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiyi.common.*;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.UserMapper;
import com.shiyi.service.*;
import com.shiyi.vo.UserInfoVO;
import com.shiyi.entity.User;
import com.shiyi.entity.UserInfo;
import com.shiyi.entity.WebConfig;
import com.shiyi.enums.LoginTypeEnum;
import com.shiyi.enums.UserStatusEnum;
import com.shiyi.mapper.UserInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiyi.strategy.context.SocialLoginStrategyContext;
import com.shiyi.utils.*;
import com.shiyi.dto.EmailLoginDTO;
import com.shiyi.dto.EmailRegisterDTO;
import com.shiyi.dto.QQLoginDTO;
import com.shiyi.dto.UserInfoDTO;
import com.shiyi.vo.WxUserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.shiyi.common.FieldConstants.LIMIT_ONE;
import static com.shiyi.common.ResultCode.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blue
 * @since 2021-12-25
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private final SocialLoginStrategyContext socialLoginStrategyContext;

    private final EmailService emailService;

    private final UserMapper userMapper;

    private final RedisService redisService;

    private final WebConfigService webConfigService;

    private final HttpServletRequest request;

//    @Value("${wechar.secret}")
    private String wxSecret;

    /**
     * 邮箱账号注册
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult emailRegister(EmailRegisterDTO dto) {


        checkCode(dto.getEmail(), dto.getCode());

        User user = userMapper.selectOne(new QueryWrapper<User>().eq(FieldConstants.USERNAME, dto.getEmail()));
        if (user != null){
            throw new BusinessException(EMAIL_IS_EXIST);
        }

        WebConfig config = webConfigService.getOne(new QueryWrapper<WebConfig>().last(LIMIT_ONE));
        UserInfo userInfo = UserInfo.builder().email(dto.getEmail()).avatar(config.getTouristAvatar()).nickname(dto.getNickname()).build();
        baseMapper.insert(userInfo);

        user = User.builder().username(dto.getEmail()).roleId(Constants.USER_ROLE_ID).userInfoId(userInfo.getId()).loginType(LoginTypeEnum.EMAIL.getType())
                .password(AesEncryptUtils.aesEncrypt(dto.getPassword())).build();
        int insert = userMapper.insert(user);

        redisService.deleteObject(RedisConstants.EMAIL_CODE + dto.getEmail());

        return insert > 0  ? ResponseResult.success("注册成功"): ResponseResult.error(ERROR_DEFAULT.getDesc());
    }

    /**
     * 修改密码
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updatePassword(EmailRegisterDTO vo) {

        checkCode(RedisConstants.EMAIL_CODE + vo.getEmail(), vo.getCode());

        //校验邮箱账号是否存在
        User user = userMapper.selectOne(new QueryWrapper<User>().eq(FieldConstants.USERNAME, vo.getEmail()));
        if (user == null){
            throw new BusinessException(ERROR_MUST_REGISTER);
        }

        user.setPassword(AesEncryptUtils.aesEncrypt(vo.getPassword()));
        int update = userMapper.updateById(user);

        redisService.deleteObject(RedisConstants.EMAIL_CODE + vo.getEmail());

        return update > 0  ? ResponseResult.success("修改成功"): ResponseResult.error(ERROR_DEFAULT.getDesc());
    }

    /**
     * 邮箱登录
     * @param vo
     * @return
     */
    @Override
    public ResponseResult emailLogin(EmailLoginDTO vo) {

        User user = userMapper.selectNameAndPassword(vo.getEmail(), AesEncryptUtils.aesEncrypt(vo.getPassword()));
        if (user == null) {
            throw new BusinessException(ERROR_PASSWORD.desc);
        }

        if (user.getStatus() == UserStatusEnum.disable.code) {
            throw new BusinessException(EMAIL_DISABLE_LOGIN.desc);
        }

        UserInfo userInfo = baseMapper.selectById(user.getUserInfoId());

        //登录
        StpUtil.login(user.getId().longValue(),"PC");

        //组装数据
        UserInfoVO userInfoVO = UserInfoVO.builder()
                .avatar(userInfo.getAvatar()).nickname(userInfo.getNickname())
                .intro(userInfo.getIntro()).webSite(userInfo.getWebSite()).email(user.getUsername())
                .loginType(user.getLoginType()).token(StpUtil.getTokenValue()).build();

        return ResponseResult.success(userInfoVO);
    }


    /**
     * qq登录
     * @param qqLoginDTO
     * @return
     */
//    @Override
//    public ResponseResult qqLogin(QQLoginDTO qqLoginDTO) {
//        UserInfoVO userInfoVO = socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(qqLoginDTO), LoginTypeEnum.QQ);
//        return ResponseResult.success(userInfoVO);
//    }

    /**
     * 微博登录
     * @param code
     * @return
     */
//    @Override
//    public ResponseResult weiboLogin(String code) {
//        UserInfoVO userInfoVO = socialLoginStrategyContext.executeLoginStrategy(code, LoginTypeEnum.WEIBO);
//        return ResponseResult.success(userInfoVO);
//    }

    /**
     * 码云登录
     * @param code
     * @return
     */
//    @Override
//    public ResponseResult giteeLogin(String code) {
//        UserInfoVO userInfoVO = socialLoginStrategyContext.executeLoginStrategy(code, LoginTypeEnum.GITEE);
//        return ResponseResult.success(userInfoVO);
//    }

    /**
     * 用户绑定邮箱，发送验证码
     * @param email
     * @return
     */
    @Override
    public ResponseResult sendEmailCode(String email) {
        try {
            // 验证邮箱格式
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return ResponseResult.error("邮箱格式不正确");
            }
            
            emailService.sendCode(email);
            return ResponseResult.success("验证码已发送，请前往邮箱查看!!");
        } catch (MessagingException e) {
            log.error("发送邮箱验证码失败，邮箱: {}", email, e);
            
            // 根据异常类型返回更详细的错误信息
            String errorMessage = "邮件发送失败";
            String exceptionMessage = e.getMessage();
            
            if (exceptionMessage != null) {
                if (exceptionMessage.contains("Authentication failed") || 
                    exceptionMessage.contains("535") || 
                    exceptionMessage.contains("认证失败")) {
                    errorMessage = "邮箱授权码错误，请检查系统配置中的邮箱授权码";
                } else if (exceptionMessage.contains("Connection refused") || 
                          exceptionMessage.contains("连接被拒绝")) {
                    errorMessage = "无法连接到邮件服务器，请检查网络连接";
                } else if (exceptionMessage.contains("timeout") || 
                          exceptionMessage.contains("超时")) {
                    errorMessage = "邮件服务器连接超时，请稍后重试";
                } else if (exceptionMessage.contains("SSL") || 
                          exceptionMessage.contains("证书")) {
                    errorMessage = "SSL连接失败，请检查邮件服务器配置";
                } else {
                    errorMessage = "邮件发送失败: " + exceptionMessage;
                }
            }
            
            return ResponseResult.error(errorMessage);
        } catch (Exception e) {
            log.error("发送邮箱验证码异常，邮箱: {}", email, e);
            return ResponseResult.error("系统异常: " + e.getMessage());
        }
    }

    /**
     * 绑定邮箱
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult publicBindEmail(UserInfoDTO vo) {
        String key = RedisConstants.EMAIL_CODE + vo.getEmail();
        checkCode(key, vo.getCode());

        UserInfo userInfo = BeanCopyUtils.copyObject(vo, UserInfo.class);
        userInfo.setEmail(vo.getEmail());
        boolean update = updateById(userInfo);
        redisService.deleteObject(key);
        return update ? ResponseResult.success("绑定邮箱成功"): ResponseResult.error(ERROR_DEFAULT.getDesc());
    }

    /**
     * 获取微信登录二维码
     * @return
     */
//    @Override
//    public ResponseResult wxQr() {
//        // 请求易登获取二维码接口
//        String s = HttpUtil.get("https://yd.jylt.cc/api/wxLogin/tempUserId?secret=" + wxSecret);
//        JSONObject jsonObject = JSONUtil.parseObj(s);
//        if (jsonObject.getInt("code") != 0) {
//            return ResponseResult.error(jsonObject.getInt("code"), "二维码获取失败");
//        }
//        JSONObject wxQrInfo = jsonObject.get("data", JSONObject.class);
//        log.info("获取到的微信二维码信息:{}",wxQrInfo);
//        return ResponseResult.success(wxQrInfo);
//    }

    /**
     * 微信授权登录回调地址
     * @param body
     * @return
     */
//    @Override
//    public Map<String,Object> wechatLogin(String body) {
//        log.info("用户微信授权登录成功！请求的body:{}",body);
//        Map<String,Object> map = new HashMap<>();
//        WxUserInfoVO wxUserInfo = JSONUtil.toBean(body, WxUserInfoVO.class);
//        if (wxUserInfo.getCancelLogin()){
//            map.put("code",1);
//            map.put("msg","取消授权登录");
//            return map;
//        }
//        if (!wxUserInfo.getScanSuccess()) {
//            WxUserInfoVO.WxMaUserInfo wxMaUserInfo = wxUserInfo.getWxMaUserInfo();
//
//            UserInfoVO userInfoVO = userMapper.selectByUserName(wxMaUserInfo.getOpenId());
//            if (userInfoVO == null) {
//                userInfoVO = checkIsRegister(wxMaUserInfo,request);
//            }
//            //设置到redis 以便监听是否已经授权成功
//            redisService.setCacheObject(RedisConstants.WX_LOGIN_USER + wxUserInfo.getTempUserId(),userInfoVO,60, TimeUnit.SECONDS);
//            map.put("code",0);
//            map.put("msg","登录成功");
//            return map;
//        }
//        return null;
//    }

    /**
     * 判断用户是否微信登录成功
     * @param tempUserId 用户临时id
     * @return
     */
//    @Override
//    public ResponseResult wxIsLogin(String tempUserId) {
//        UserInfoVO obj = (UserInfoVO)redisService.getCacheObject(RedisConstants.WX_LOGIN_USER + tempUserId);
//        if (obj == null) {
//            return ResponseResult.error("用户未被授权");
//        }
//        StpUtil.login(obj.getId().longValue(),"PC");
//        obj.setToken(StpUtil.getTokenValue());
//        return ResponseResult.success(obj);
//    }

    /**
     * 获取用户信息
     * @return
     */
    @Override
    public ResponseResult publicSelectUserInfo() {
        UserInfo userInfo = baseMapper.getByUserId(StpUtil.getLoginIdAsInt());
        return ResponseResult.success(userInfo);
    }

    /**
     * 修改用户信息
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult publicUpdateUser(UserInfoDTO vo) {
        UserInfo userInfo = BeanCopyUtils.copyObject(vo, UserInfo.class);
        boolean update = updateById(userInfo);
        return update ? ResponseResult.success("修改信息成功"): ResponseResult.error(ERROR_DEFAULT.getDesc());
    }


    //---------------自定义方法开始-------------
    /**
     * 检查是否是一次登录
     * @param wxMaUserInfo
     * @param request
     * @return
     */
    private UserInfoVO checkIsRegister(WxUserInfoVO.WxMaUserInfo wxMaUserInfo, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String ipSource = IpUtil.getIp2region(ip);

            // 保存用户信息
            UserInfo userInfo = UserInfo.builder()
                    .nickname(wxMaUserInfo.getNickName())
                    .avatar(wxMaUserInfo.getAvatarUrl())
                    .build();
            baseMapper.insert(userInfo);
            // 保存账号信息
            User user = User.builder()
                    .userInfoId(userInfo.getId())
                    .username(wxMaUserInfo.getOpenId())
                    .password(AesEncryptUtils.aesEncrypt(wxMaUserInfo.getOpenId()))
                    .loginType(LoginTypeEnum.WECHAT.getType())
                    .lastLoginTime(DateUtil.getNowDate())
                    .ipAddress(ip)
                    .ipSource(ipSource)
                    .roleId(2)
                    .build();
            userMapper.insert(user);
            //组装返回信息
        return UserInfoVO.builder().id(user.getId()).loginType(user.getLoginType())
                .avatar(userInfo.getAvatar()).email(userInfo.getEmail()).nickname(userInfo.getNickname())
                .intro(userInfo.getIntro()).webSite(userInfo.getWebSite()).build();
    }

    private void checkCode(String email, String sourCode) {
        //校验验证码是否一致
        Object code = redisService.getCacheObject(RedisConstants.EMAIL_CODE + email);
        if (ObjectUtils.isEmpty(code) || !code.equals(sourCode)){
            throw new BusinessException(EMAIL_CODE_ERROR);
        }
    }
}
