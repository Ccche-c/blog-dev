package com.shiyi.controller.api;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.shiyi.annotation.BusinessLogger;
import com.shiyi.common.ResponseResult;
import com.shiyi.service.UserInfoService;
import com.shiyi.dto.EmailLoginDTO;
import com.shiyi.dto.EmailRegisterDTO;
import com.shiyi.dto.QQLoginDTO;
import com.shiyi.dto.UserInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

@RequestMapping("/v1/user")
@RestController
@Api(tags = "登录接口")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserInfoService userInfoService;

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ApiOperation(value = "邮箱账号注册", httpMethod = "POST", response = ResponseResult.class, notes = "邮箱账号注册")
    public ResponseResult emailRegister(@Valid @RequestBody EmailRegisterDTO emailRegisterDTO){
        return userInfoService.emailRegister(emailRegisterDTO);
    }

    @RequestMapping(value = "/emailLogin",method = RequestMethod.POST)
    @ApiOperation(value = "邮箱登录", httpMethod = "POST", response = ResponseResult.class, notes = "邮箱登录")
    public ResponseResult emailLogin(@Valid @RequestBody EmailLoginDTO emailLoginDTO){
        return userInfoService.emailLogin(emailLoginDTO);
    }

    @RequestMapping(value = "/updatePassword",method = RequestMethod.POST)
    @BusinessLogger(value = "个人中心模块-邮箱账号修改密码",type = "修改",desc = "邮箱账号修改密码")
    public ResponseResult updatePassword(@Valid @RequestBody EmailRegisterDTO emailRegisterDTO){
        return userInfoService.updatePassword(emailRegisterDTO);
    }

//    @RequestMapping(value = "/login",method = RequestMethod.POST)
//    @ApiOperation(value = "QQ登录", httpMethod = "POST", response = ResponseResult.class, notes = "QQ登录")
//    public ResponseResult login(@Valid @RequestBody QQLoginDTO qqLoginDTO){
//        return userInfoService.qqLogin(qqLoginDTO);
//    }
//
//    @RequestMapping(value = "/gitEELogin",method = RequestMethod.GET)
//    @ApiOperation(value = "gitEE登录", httpMethod = "GET", response = ResponseResult.class, notes = "gitEE登录")
//    public ResponseResult gitEELogin(String code){
//        return userInfoService.giteeLogin(code);
//    }
//
//    @RequestMapping(value = "/weiboLogin",method = RequestMethod.GET)
//    @ApiOperation(value = "微博登录", httpMethod = "GET", response = ResponseResult.class, notes = "微博登录")
//    public ResponseResult weiboLogin(String code){
//        return userInfoService.weiboLogin(code);
//    }
//
//    @RequestMapping(value = "/wxQr",method = RequestMethod.GET)
//    @ApiOperation(value = "获取微信登录二维码", httpMethod = "GET", response = ResponseResult.class, notes = "获取微信登录二维码")
//    public ResponseResult wxQr(){
//        return userInfoService.wxQr();
//    }
//
//    @ApiOperation(value = "wx登录授权回调接口", httpMethod = "POST", response = ResponseResult.class, notes = "wx登录授权回调接口")
//    @RequestMapping(value = "/callBack",method = RequestMethod.POST)
//    public Map<String,Object> wxCallBack(@RequestBody String body) {
//        return  userInfoService.wechatLogin(body);
//    }

//    @ApiOperation(value = "判断用户是否微信登录成功", httpMethod = "GET", response = ResponseResult.class, notes = "判断用户是否微信登录成功")
//    @RequestMapping("/wx/is_login")
//    public ResponseResult wxIsLogin(@ApiParam(name = "tempUserId", value = "tempUserId") String tempUserId) {
//        return userInfoService.wxIsLogin(tempUserId);
//    }

    @RequestMapping(value = "/sendEmailCode",method = RequestMethod.GET)
    @ApiOperation(value = "发送邮箱验证码", httpMethod = "GET", response = ResponseResult.class, notes = "发送邮箱验证码")
    public ResponseResult sendEmailCode(String email){
        return userInfoService.sendEmailCode(email);
    }

    @SaCheckLogin
    @RequestMapping(value = "/bindEmail",method = RequestMethod.POST)
    @BusinessLogger(value = "个人中心模块-绑定邮箱",type = "修改",desc = "绑定邮箱")
    public ResponseResult publicBindEmail(@RequestBody UserInfoDTO vo){
        return userInfoService.publicBindEmail(vo);
    }

    @SaCheckLogin
    @BusinessLogger(value = "个人中心模块-获取用户信息",type = "修改",desc = "获取用户信息")
    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public ResponseResult publicSelectUserInfo(){
        return userInfoService.publicSelectUserInfo();
    }

    @SaCheckLogin
    @BusinessLogger(value = "个人中心模块-修改用户信息",type = "修改",desc = "修改用户信息")
    @RequestMapping(value = "/",method = RequestMethod.PUT)
    public ResponseResult publicUpdateUser(@RequestBody UserInfoDTO vo){
        return userInfoService.publicUpdateUser(vo);
    }


}

