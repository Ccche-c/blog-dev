//package com.shiyi.service.impl;
//
//
//import cn.dev33.satoken.stp.StpUtil;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.shiyi.common.ResponseResult;
//import com.shiyi.entity.Sponsor;
//import com.shiyi.exception.BusinessException;
//import com.shiyi.mapper.SponsorMapper;
//import com.shiyi.service.SponsorService;
//import com.walter.epay.param.request.EpayCreateOrderParam;
//import com.walter.epay.param.response.EpayCreateOrderResponse;
//import com.walter.epay.param.response.EpayResponse;
//import com.walter.epay.paramenum.createorder.EpayCreateOrderIsHtmlEnum;
//import com.walter.epay.requestapi.EpayRequestApi;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.security.NoSuchAlgorithmException;
//
//
///**
// * <p>
// * 服务实现类
// * </p>
// *
// * @author blue
// * @since 2021-11-10
// */
//@Slf4j
//@Service
//public class SponsorServiceImpl extends ServiceImpl<SponsorMapper, Sponsor> implements SponsorService {
//
//    /**
//     * 支付秘钥
//     */
//    private static final String KEY = "c9341871b4c146658417a4a83c07c4b4";
//
//    /**
//     * 商户id
//     */
//    private static final String MCH_ID = "1686022998";
//
//    @Override
//    public ResponseResult createOrder(Double price,int payType) throws NoSuchAlgorithmException {
//        EpayCreateOrderParam epayCreateOrderParam = new EpayCreateOrderParam();
//        epayCreateOrderParam.setParam("test");
//        epayCreateOrderParam.setIsHtml(EpayCreateOrderIsHtmlEnum.IS_JSON.getCode());
//        epayCreateOrderParam.setPrice(price);
//        epayCreateOrderParam.setType(payType);
//        epayCreateOrderParam.setGoodsName("博客打赏");
//        epayCreateOrderParam.setMchId(MCH_ID);
//        epayCreateOrderParam.setPayId(System.currentTimeMillis() + "");
//
//
//        EpayResponse<EpayCreateOrderResponse> orderWithJson = EpayRequestApi.createOrderWithJson(epayCreateOrderParam, KEY);
//        if (!orderWithJson.isSuccess()) {
//            throw new BusinessException("订单创建失败，失败原因：" + orderWithJson.getMsg());
//        }
//        JSONObject jsonObject = JSON.parseObject(JSONUtil.toJsonStr(orderWithJson.getData()));
//        Sponsor sponsor = Sponsor.builder().userId(StpUtil.getLoginIdAsInt()).price(price)
//                .orderId(jsonObject.get("orderId").toString()).isPay(0).payType(payType).build();
//        baseMapper.insert(sponsor);
//
//        System.out.println("订单创建成功，订单响应数据是：" + JSONUtil.toJsonStr(orderWithJson.getData()));
//        return ResponseResult.success(orderWithJson.getData());
//    }
//
//    @Override
//    public String callBack(String orderId, Double price, Double reallyPrice) {
//        log.info("用户支付成功回调触发，orderId:{},price:{},reallyPrice:{}",orderId,price,reallyPrice);
//        Sponsor sponsor = baseMapper.selectOne(new LambdaQueryWrapper<Sponsor>().eq(Sponsor::getOrderId, orderId));
//        if (sponsor != null){
//            sponsor.setIsPay(1);
//            baseMapper.updateById(sponsor);
//            return "success";
//        }
//        return "error";
//    }
//}
