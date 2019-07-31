package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.domain.Goods;
import com.sxt.service.CartService;
import com.sxt.service.GoodsService;
import com.sxt.utils.CookieUtil;
import com.sxt.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author WWF
 * @title: RedisCartController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/29 19:43
 */
@RestController
@RequestMapping("/cart")
public class RedisCartController {
    @Reference
    private CartService cartService;
    @Reference
    private GoodsService goodsService;

    @PostMapping("/add")
    public Result addCart(Integer goodsId, Integer goodsNum) {
        String label = getUserLabel();//用户的标识
        String egoUss = getEgoUss();
        cartService.addCart(label, goodsId, goodsNum,egoUss);
        return Result.ok();

    }

    /**
     * 得到用户的唯一标识
     *
     * @return
     */
    private String getUserLabel() {
        /**
         * 用户的标识来做cookie
         *
         */
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userLabel = CookieUtil.getCookieValue(request, "EGO-USER-LABEL");
        if (userLabel != null) {
            return userLabel;//标识已生成
        }
        //		userLable 用户的标签还没有生成
        userLabel = UUID.randomUUID().toString();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        CookieUtil.setCookie(request, response, "EGO-USER-LABEL", userLabel, 30 * 24 * 60 * 60);
        return userLabel;
    }

    /**
     * 获取总条数
     * @return
     */

    @PostMapping("/getTotal")
    public long getTotal() {
      //用户的标识
        String egoUss = getEgoUss();
        return cartService.getTotal(getUserLabel(),egoUss);
    }
    /**
     * 获取购物车的数据并回显
     * @return
     */
    @PostMapping("/list")
    public Result getCartData(){
        String label=getUserLabel();
        // goodsList 它 严格按照用户的添加顺序来排名
        String egoUss = getEgoUss();
        List<Integer> goodsList = cartService.getGoodsList(label,egoUss);
        if (goodsList==null||goodsList.size()==0){
            return Result.error();

        }else {
            // 从redis 里面获取它的idlist集合
//		[39,51,55,50,45] in - >[39,45,50,51,55]
            // finByIds 批量查询，会重新排序  in 查询 会自动使用id 来排序goodsService.finByIds(goodsList );

            List<Goods> goods = goodsService.findByIds(goodsList);
            // goods 该商品里面没有包含用户的数量的问题

            Map<Integer, Integer> goodsInfo = cartService.getGoodsInfo(label,getEgoUss());
            for (Goods good : goods) {
                // 需要手动set 数量
                Integer integer = goodsInfo.get(good.getId());
                good.setStoreCount(integer.shortValue());
                // 从哪里获取用户添加的数据 redisMap
            }
            return  Result.ok(goods);
        }

    }
    /**
     * 获取用户在浏览器里面的登录标识
     * @return
     */
    public String getEgoUss(){
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        // 从cookie 里面取值
        String cookieValue = CookieUtil.getCookieValue(attributes.getRequest(), "EGO-USS");
        return  cookieValue;
    }
}
