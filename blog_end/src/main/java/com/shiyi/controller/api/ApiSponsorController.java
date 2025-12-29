//package com.shiyi.controller.api;
//
//
//import com.shiyi.common.ResponseResult;
//import com.shiyi.service.SponsorService;
//import io.swagger.annotations.Api;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.security.NoSuchAlgorithmException;
//
//@RequestMapping("/v1/sponsor")
//@RestController
//@Api(tags = "打赏接口")
//@RequiredArgsConstructor
//public class ApiSponsorController {
//
//    private final SponsorService sponsorService;
//
//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String callBack(String orderId,Double price,Double reallyPrice) {
//        return sponsorService.callBack(orderId,price,reallyPrice);
//    }
//
//    @RequestMapping(value = "createOrder", method = RequestMethod.GET)
//    public ResponseResult createOrder(Double price,int payType) throws NoSuchAlgorithmException {
//        return sponsorService.createOrder(price,payType);
//    }
//}
