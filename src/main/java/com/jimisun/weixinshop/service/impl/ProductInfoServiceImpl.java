package com.jimisun.weixinshop.service.impl;

import com.jimisun.weixinshop.dao.ProductInfoDao;
import com.jimisun.weixinshop.dto.CartDTO;
import com.jimisun.weixinshop.entity.ProductInfo;
import com.jimisun.weixinshop.enums.ProductInfoStatusEnum;
import com.jimisun.weixinshop.enums.ResultVoCodeEnum;
import com.jimisun.weixinshop.exception.SellException;
import com.jimisun.weixinshop.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    private ProductInfoDao productInfoDao;

    @Override
    public ProductInfo findOne(String productId) {
        return productInfoDao.getOne(productId);
    }

    @Override
    public List<ProductInfo> findUpAll() {
        return productInfoDao.findByProductStatus(ProductInfoStatusEnum.UP.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return productInfoDao.findAll(pageable);
    }

    @Override
    public ProductInfo save(ProductInfo productInfo) {
        return productInfoDao.save(productInfo);
    }

    @Override
    public void increaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDao : cartDTOList) {
            ProductInfo productInfo = productInfoDao.getOne(cartDao.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultVoCodeEnum.PRODUCT_NOT_EXIST);
            }
            Integer result = productInfo.getProductStock() + cartDao.getProductQuantity();
            productInfo.setProductStock(result);
            productInfoDao.save(productInfo);
        }
    }

    @Override
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO : cartDTOList) {
            ProductInfo productInfo = productInfoDao.getOne(cartDTO.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultVoCodeEnum.PRODUCT_NOT_EXIST);
            }
            int result = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if (result < 0) {
                throw new SellException(ResultVoCodeEnum.PRODUCT_STOCK_ERROR);
            }
            productInfo.setProductStock(result);

            productInfoDao.save(productInfo);
        }
    }

    @Override
    public ProductInfo onSale(String productId) {
        ProductInfo productInfo =
                productInfoDao.getOne(productId);
        if (productInfo == null) {
            throw new SellException(ResultVoCodeEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductInfoStatusEnum() == ProductInfoStatusEnum.UP) {
            throw new SellException(ResultVoCodeEnum.PRODUCT_STATUS_ERROR);
        }

        //更新商品状态
        productInfo.setProductStatus(ProductInfoStatusEnum.UP.getCode());
        return productInfoDao.save(productInfo);
    }

    @Override
    public ProductInfo offSale(String productId) {
        ProductInfo productInfo = productInfoDao.getOne(productId);
        if (productInfo == null) {
            throw new SellException(ResultVoCodeEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductInfoStatusEnum() == ProductInfoStatusEnum.DOWN) {
            throw new SellException(ResultVoCodeEnum.PRODUCT_STATUS_ERROR);
        }

        //更新商品状态
        productInfo.setProductStatus(ProductInfoStatusEnum.DOWN.getCode());
        return productInfoDao.save(productInfo);
    }

    @Override
    public List<ProductInfo> findByCategoryType(Pageable pageable,Integer categoryType) {
        return productInfoDao.findByCategoryType(categoryType,pageable);
    }



    @Override
    public List<ProductInfo> findIndexHotVo(Pageable pageable) {
        List<ProductInfo> list = productInfoDao.findByCategoryType(8, pageable);
        return list;
    }

    @Override
    public List<ProductInfo> findIndexyoulike(Pageable pageable) {
        List<ProductInfo> list = productInfoDao.findByCategoryType(9, pageable);
        return list;
    }

    @Override
    public List<ProductInfo> findBySousuo( String sousuo) {
        List<ProductInfo>sousuoResult=productInfoDao.findByProductName(sousuo);
        return sousuoResult;
    }

    @Override
    public List<ProductInfo> findByCategoryType(Integer categoryType) {
        return productInfoDao.findByCategoryType(categoryType);
    }
}
