package com.sxt.vo;

import java.io.Serializable;

/**
 * 展示的商品
 * @author CodeLab
 *
 */
public class GoodsVo  implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // 商品的id
    private Integer id;
    // 商品的图片
    private String originImg;
    // 商品的高亮名称
    private String goodsNameHl;
    // 商品的价格
    private Double shopPrice;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getOriginImg() {
        return originImg;
    }
    public void setOriginImg(String originImg) {
        this.originImg = originImg;
    }
    public String getGoodsNameHl() {
        return goodsNameHl;
    }
    public void setGoodsNameHl(String goodsNameHl) {
        this.goodsNameHl = goodsNameHl;
    }
    public Double getShopPrice() {
        return shopPrice;
    }
    public void setShopPrice(Double shopPrice) {
        this.shopPrice = shopPrice;
    }


}
