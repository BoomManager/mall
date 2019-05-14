package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.service.PmsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 *
 */
@Controller
@Api(tags = "PmsProduceController", description = "商品信息管理")
@RequestMapping("/produce")
public class PmsProduceController {

    @Autowired
    private PmsProductService pmsProductService;

    @ApiOperation("查询商品分页信息")
    @RequestMapping(value = "/getProduct",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> getProduct(@RequestParam(required = false) Long categoryId,
                                                    @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                    @RequestParam(required = false, defaultValue = "5") Integer pageSize
    ){
        List<PmsProduct> page = pmsProductService.getProductByCategoryId(categoryId,pageNum,pageSize);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @ApiOperation("分类之后查询商品详情信息")
    @RequestMapping(value = "/getProductDetail",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProduct> getProductDetail(@RequestParam(required = false) Long categoryId,@RequestParam(required = false) Long productId){
        PmsProduct pmsProduct = pmsProductService.getProductDetail(categoryId, productId);
        return CommonResult.success(pmsProduct);
    }
}
