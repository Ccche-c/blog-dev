package com.shiyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Sponsor;

import java.security.NoSuchAlgorithmException;


/**
 *  打赏服务类
 *
 * @author blue
 * @since 2021-11-10
 */
public interface SponsorService extends IService<Sponsor> {


    /**
     * 创建订单
     * @param price
     * @return
     */
    ResponseResult createOrder(Double price,int payType) throws NoSuchAlgorithmException;

    /**
     * 用户支付成功后的回调地址
     * @param orderId
     * @param price
     * @param reallyPrice
     * @return
     */
    String callBack(String orderId, Double price, Double reallyPrice);
}
