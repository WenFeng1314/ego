package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sxt.domain.Admin;
import com.sxt.domain.Cart;
import com.sxt.domain.CartExample;
import com.sxt.mapper.CartMapper;
import com.sxt.model.OrderGoodsVo;
import com.sxt.model.OrderVo;
import com.sxt.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * @author WWF
 * @title: CartServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/6/1 19:06
 */
@Service
public class CartServiceImpl implements CartService {

    private static final String GOODS_LIST="goods-list";
    private static final String GOODS_INFO="goods-info";

    @Autowired
    JedisPool jedisPool;

    @Autowired
    CartMapper cartMapper;

    /**
     * 添加购物车
     * 用户的唯一凭证
     * @param label
     * 商品id
     * @param goodsId
     * 商品添加的数目
     * @param goodsNum
     * 用于确定用户是否登入
     * @param uss
     */
    @Override
    public void addCart(String label, Integer goodsId, Integer goodsNum, String uss) {
           //用户登录了，添加到数据库，没有登陆添加到redis
             Admin admin=getUserByUss(uss);
             if (uss!=null&&!uss.equals("")){
                 addCartToDatabase(goodsId,goodsNum,admin.getId().intValue());
             }else {
                 addCartToRedis(label,goodsId,goodsNum);
             }

    }
    //1.添加到数据库
    private void addCartToDatabase(Integer goodsId, Integer goodsNum, Integer uid) {
        //要判断用户之前是否有添加过
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andUserIdEqualTo(uid).andGoodsIdEqualTo(goodsId);
        List<Cart> carts = cartMapper.selectByExample(cartExample);
     System.out.println(carts.toString());
        if (carts==null||carts.isEmpty()){
            //用户之前没有添加过商品
            Cart cart = new Cart();
            cart.setGoodsId(goodsId);
            cart.setNum(goodsNum);
            cart.setUserId(uid);
            cartMapper.insert(cart);
        }else if (carts.size()==1){
            //用户之前存在过
            System.out.println(carts.size());
            Cart cart = carts.get(0);
            cart.setNum(goodsNum+cart.getNum());
            cartMapper.updateByPrimaryKey(cart);
        }else {
            //若商品条目大于1
            throw new RuntimeException("数据库有误！");
        }

    }
   //2.添加到redis
    private void addCartToRedis(String label, Integer goodsId, Integer goodsNum) {
        String goodsListKey=label+"-"+GOODS_LIST;
        String goodsInfoKey=label+"-"+GOODS_INFO;
        String goodsScoreKey=label+"-score";
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            //先添加goodsScoreKey
            Long goodsScore = jedis.incr(goodsScoreKey);
            //再添加goodsList
            jedis.zadd(goodsListKey,goodsScore,goodsId+"");
            //最后添加goodsInfo
            if (jedis.hexists(goodsInfoKey,goodsId+"")){
                // 若之前用户添加过该商品
                Integer total=goodsNum+ Integer.valueOf(jedis.hget(goodsInfoKey,goodsId+""));
                jedis.hset(goodsInfoKey,goodsId+"",total+"");
            }else {
                //之前用户不存在该商品
                jedis.hset(goodsInfoKey,goodsId+"",goodsNum+"");

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }

    }



    private Admin getUserByUss(String uss) {
         if (uss==null||uss.equals("")){
             return null;
         }
         Jedis jedis =null;
         Admin admin=null;
        try {
            jedis = jedisPool.getResource();
            //判断用户是否存在
            if (jedis.exists(uss)) {
                //存在就取出
                String userJson = jedis.get(uss);
                admin = JSON.parseObject(userJson, Admin.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return  admin;
    }

    @Override
    public long getTotal(String label, String uss) {
        Admin admin=getUserByUss(uss);
        if (uss!=null&&!uss.equals("")){
            //从数据库获得总条数
           return getTotalFromDatabase(admin.getId().intValue());
        }else {
            //从redis里面获得总条数
          return   getTotalFromRedis(label);
        }


    }

    private long getTotalFromRedis(String label) {
        String goodsInfoKey=label+"-"+GOODS_INFO;
        long total= 0;
        Jedis jedis=null;
        try {
             jedis = jedisPool.getResource();
            total = 0L;
            Map<String, String> goodsInfoMap = jedis.hgetAll(goodsInfoKey);
            if (goodsInfoMap==null)return total;
            Set<String> goodsIds = goodsInfoMap.keySet();
            for (String goodsId : goodsIds) {
               String goodsNum=goodsInfoMap.get(goodsId);
               total+=Integer.valueOf(goodsNum);

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return  total;

    }

    private long getTotalFromDatabase(Integer  uid) {
        Integer databaseTatol = cartMapper.getDatabaseTatol(uid);
        return databaseTatol;
    }

    @Override
    public List<Integer> getGoodsList(String label, String uss) {
        Admin admin=getUserByUss(uss);
        if (uss!=null&&!uss.equals("")){
            //从数据库获得goodsList
          return  getGoodsListFromDatebase(admin.getId().intValue());
        }else {
            //从redis里面获得goodsList
            return  getGoodsListFromRedis(label);
        }


    }

    private List<Integer> getGoodsListFromRedis(String label) {
        String goodsListKey=label+"-"+GOODS_LIST;
        List<Integer> goodsIds=new ArrayList<Integer>();
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            Set<String> goodsListSet = jedis.zrevrange(goodsListKey, 0, Integer.MAX_VALUE);
            for (String goodsId : goodsListSet) {
                goodsIds.add(Integer.valueOf(goodsId));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return  goodsIds;
    }

    private List<Integer> getGoodsListFromDatebase(Integer uid) {
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andUserIdEqualTo(uid);
        List<Cart> carts = cartMapper.selectByExample(cartExample);
        List<Integer> goodsids =new ArrayList<Integer>();
        if (carts!=null){

        for (Cart cart : carts) {

            goodsids.add(cart.getGoodsId());

        }
        return  goodsids;
        }

        return  goodsids;
    }

    @Override
    public Map<Integer, Integer> getGoodsInfo(String label,String uss) {
         Admin admin=getUserByUss(uss);
        if (uss!=null&&!uss.equals("")){
            //从数据库获得goodsList
            return  getGoodsInfoFromDatebase(admin.getId().intValue());
        }else {
            //从redis里面获得goodsList
            return  getGoodsInfoFromRedis(label);
        }

    }

    private Map<Integer, Integer> getGoodsInfoFromDatebase(Integer uid) {
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andUserIdEqualTo(uid);
        List<Cart> carts = cartMapper.selectByExample(cartExample);
        Map<Integer,Integer> goodsInfo=new HashMap<Integer, Integer>();
        for (Cart cart : carts) {
            goodsInfo.put(cart.getGoodsId(),cart.getNum());
        }
        return goodsInfo;
    }

    private Map<Integer, Integer> getGoodsInfoFromRedis(String label) {
        String goodsInfoKey=label+"-"+GOODS_INFO;
        HashMap<Integer,Integer> goodsInfo=new HashMap<Integer, Integer>();
        Jedis jedis=null;
        try {
             jedis = jedisPool.getResource();
            Map<String, String> goodsInfoMap = jedis.hgetAll(goodsInfoKey);
            if (goodsInfoMap!=null){
                goodsInfoMap.forEach((k,v)->{
                     goodsInfo.put(Integer.valueOf(k),Integer.valueOf(v));
                 });
             }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return  goodsInfo;

    }

    @Override
    public void transDataFromRedisToMysql(String label, Integer uid) {
              if (label==null||uid==null)return;
          // 1 取出redis 里面所有的商品列表
        List<Integer> goodsIds = getGoodsListFromRedis(label);
        Map<Integer, Integer> goodsInfo = getGoodsInfoFromRedis(label);
         // 2 求redis 和mysql 里面的交集
          if (goodsIds!=null&&!goodsIds.isEmpty()){
              CartExample cartExample = new CartExample();
              cartExample.createCriteria().andUserIdEqualTo(uid).andGoodsIdIn(goodsIds);
              List<Cart> carts = cartMapper.selectByExample(cartExample);
              // 交集的修改操作
              for (Cart cart : carts) {
                  Integer num=cart.getNum()+goodsInfo.get(cart.getGoodsId());
                  cart.setNum(num);
                  cartMapper.updateByPrimaryKey(cart);
                   goodsIds.remove(cart.getGoodsId());//去除调交集的goodsId
              }
              // 差集的新增操作
              goodsIds.forEach((goodsId)->{
                  Cart cart = new Cart();
                  cart.setGoodsId(goodsId);
                  cart.setUserId(uid);
                  cart.setNum(goodsInfo.get(goodsId));
                  cartMapper.insert(cart);
              });
          }
          //删除redis中的数据
        Jedis jedis =null;
        try {
            String goodsListKey=label+"-"+GOODS_LIST;
            String goodsInfoKey=label+"-"+GOODS_INFO;
            String goodsScoreKey=label+"-score";
            jedis = jedisPool.getResource();
            jedis.del(goodsListKey,goodsInfoKey,goodsScoreKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }


    }

    /**
     * 清空购物车
     * @param uid
     * @param goodsIdList
     */
    @Override
    public void clearCart(Integer uid, List<Integer> goodsIdList) {
        //验证
        Assert.notNull(uid,"用户id不能为null");
        if (goodsIdList==null||goodsIdList.isEmpty()){
            throw new RuntimeException("购物车数据不存在");
        }
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andUserIdEqualTo(uid).andGoodsIdIn(goodsIdList);
        // 清空购物车
        cartMapper.deleteByExample(cartExample);


    }

    /**
     * 恢复购物车
     * @param orderVo
     */
    @Override
    public void recovery(OrderVo orderVo) {
        Integer uid = orderVo.getUid();
        List<OrderGoodsVo> goodsVoList = orderVo.getGoodsVoList();
        for (OrderGoodsVo orderGoodsVo : goodsVoList) {
            Cart cart = new Cart();
            cart.setUserId(uid);
            cart.setNum(orderGoodsVo.getNum());
            cart.setGoodsId(orderGoodsVo.getId());
            cartMapper.insert(cart);
        }

    }
}
