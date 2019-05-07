package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductExample;
import com.macro.mall.portal.service.PmsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class PmsProductServiceImpl implements PmsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductServiceImpl.class);
    @Autowired
    private PmsProductMapper productMapper;


    @Override
    public List<PmsProduct> getProductByCategoryId(Long categoryId, Integer pageNum, Integer pageSize) {
        PmsProductExample pmsProductExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = pmsProductExample.createCriteria();
        criteria.andProductCategoryIdEqualTo(categoryId);
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProduct> pmsProducts = productMapper.selectByExample(pmsProductExample);
        return pmsProducts;
    }

    @Override
    public PmsProduct getProductDetail(Long categoryId, Long productId) {
        PmsProductExample pmsProductExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = pmsProductExample.createCriteria();
        criteria.andProductCategoryIdEqualTo(categoryId);
        criteria.andIdEqualTo(productId);
        List<PmsProduct> pmsProducts = productMapper.selectByExample(pmsProductExample);
        PmsProduct pmsProduct = pmsProducts.get(0);
        return pmsProduct;
    }




}
