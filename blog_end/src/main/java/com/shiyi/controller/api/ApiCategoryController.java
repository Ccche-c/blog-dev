package com.shiyi.controller.api;

import com.shiyi.common.ResponseResult;
import com.shiyi.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类处理器
 * @author blue
 */
@Api(tags = "分类管理-API")
@RestController
@RequestMapping("v1/category")
@RequiredArgsConstructor
public class ApiCategoryController {

    private final CategoryService categoryService;

    @RequestMapping(value = "list",method = RequestMethod.GET)
    @ApiOperation(value = "分类列表", httpMethod = "GET", response = ResponseResult.class, notes = "分类列表")
    public ResponseResult selectPublicCategoryList(){
        return categoryService.selectPublicCategoryList();
    }
}
