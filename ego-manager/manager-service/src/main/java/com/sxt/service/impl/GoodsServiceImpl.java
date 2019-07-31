package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.sxt.domain.Goods;
import com.sxt.domain.GoodsExample;
import com.sxt.mapper.GoodsMapper;
import com.sxt.model.OrderGoodsVo;
import com.sxt.model.OrderVo;
import com.sxt.model.Page;
import com.sxt.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author WWF
 * @title: GoodsServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/21 20:34
 */
@Service
public class GoodsServiceImpl extends AService<Goods> implements GoodsService {

    @Autowired
    private GoodsMapper dao;
    @Autowired
    private JedisPool jedisPool;

    /**
     * 该方法可以给父类里面注入一个子类的dao
     *
     */
    @PostConstruct
    public void setDao(){
        super.dao=this.dao;
    }
    //重写add方法
    @Override
    public Integer add(Goods goods) {
        Integer result=super.add(goods);
        if (result > 0) {// 添加商品成功
            Jedis jedis=null;
            try {
                jedis = jedisPool.getResource();
                // 添加到redis 里面
                jedis.hset("goods-stock",goods.getId()+"",goods.getStoreCount()+"");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis!=null){
                    jedis.close();
                }
            }
        }

        return result;
    }

    @Override
    public void importAllStockToRedis() {
        //查询所有商品
        long total = dao.countByExample(null);
        long page=total%1000==0?total/1000:(total/1000+1);
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            // 分页导入到redis 里面
            for (int i = 1; i <=page; i++) {
                PageHelper.startPage(i,1000,false);
                List<Goods> goods = dao.selectByExample(null);
                HashMap<String,String> stock=new HashMap<String, String>();
                goods.forEach((good)->{
                    stock.put(good.getId()+"",good.getStoreCount()+"");
                });
                jedis.hmset("goods-stock",stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis!=null){
                jedis.close();
            }
        }

    }

    /**
     *批量下架
     * @param ids
     */
    @Override
    public Integer batchDown(String ids) {
        if (ids==null&&ids==""){
            throw new RuntimeException("id不能为空");
        }
        //分割ids字符串
        String[] split = ids.split(",");
        //创建一个集合
        List<Integer> list=new ArrayList<Integer>();
        for (String s:split){
            list.add(Integer.valueOf(s));

        }
        //创建查询条件
        GoodsExample goodsExample = new GoodsExample();
        GoodsExample.Criteria criteria = goodsExample.createCriteria();
        criteria.andIdIn(list);
        Goods goods = new Goods();
        goods.setIsOnSale(false);
        int i = dao.updateByExampleSelective(goods, goodsExample);

       return i;
    }

    /**
     * 批量上架
     * @param ids
     * @return
     */
    @Override
    public Integer batchIsOnSale(String ids) {
        if (ids==null&&ids==""){
            throw new RuntimeException("id不能为空");
        }
        String[] split = ids.split(",");
        List<Integer> list=new ArrayList<Integer>();
        for (String s:split){
            list.add(Integer.valueOf(s));

        }
        //创建查询条件
        GoodsExample goodsExample = new GoodsExample();
        GoodsExample.Criteria criteria = goodsExample.createCriteria();
        criteria.andIdIn(list);
        Goods goods = new Goods();
        goods.setIsOnSale(true);
        int i = dao.updateByExampleSelective(goods, goodsExample);


        return i;
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @Override
    public Integer batchDeleteGoods(String ids) {
        if (ids==null&&ids==""){
            throw new RuntimeException("id不能为空");
        }
        String[] split = ids.split(",");
        List<Integer> list=new ArrayList<Integer>();
        for (String s:split){
            list.add(Integer.valueOf(s));

        }
        //创建查询条件
        GoodsExample goodsExample = new GoodsExample();
        GoodsExample.Criteria criteria = goodsExample.createCriteria();
        criteria.andIdIn(list);
        Goods goods = new Goods();
        goods.setIsOnSale(true);
        int i = dao.deleteByExample(goodsExample);

        return i;
    }

    @Override
    public Page<Goods> queryByTime(Date startTime, Date deadTime, Integer page, Integer size) {
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.createCriteria().andUpdateTimeBetween(startTime,deadTime);
        com.github.pagehelper.Page<Object> startPage = PageHelper.startPage(page, size);
        List<Goods> goods = dao.selectByExample(goodsExample);

        return new Page<Goods>(page,size,startPage.getTotal(),goods);
    }

    @Override
    public Long countByTime(Date startTime, Date deadTime) {
        GoodsExample goodsExample = new GoodsExample();
        GoodsExample.Criteria criteria = goodsExample.createCriteria().andUpdateTimeBetween(startTime, deadTime);
        long total = dao.countByExample(goodsExample);

        return total;
    }

    @Override
    public List<Goods> findByIds(List<Integer> goodsList) {
        GoodsExample goodsExample = new GoodsExample();
        GoodsExample.Criteria criteria = goodsExample.createCriteria().andIdIn(goodsList);
        List<Goods> goods = dao.selectByExample(goodsExample);
        //保存着id和goods对应关系，方便后面使用goodsid来获取goods
        HashMap<Integer,Goods> goodsId = new HashMap<Integer, Goods>();

        for (Goods good : goods) {
            goodsId.put(good.getId(),good);

        }

        //从小到大排列，但是我们需要用户按点击的顺序排列
        List<Goods> sortGoods=new ArrayList<Goods>();
        for (Integer id : goodsList) {
             sortGoods.add(goodsId.get(id));
        }

        return sortGoods;
    }

    /**
     * 执行库存--
     * @param goodsId
     * @param num
     */
    @Override
    public void decrStock(Integer goodsId, Integer num) {
        //先验证，
        Assert.notNull(goodsId,"商品的id不能为null");
        Assert.notNull(num,"商品的数量不能为null");
        //通过商品的goodsId去查询goods
        Goods goods = findById(goodsId);
        if (goods==null) throw new RuntimeException("输入的商品id没有对应的值");
        //得到数据库商品库存
        Short storeCount = goods.getStoreCount();
        int decrNum=storeCount-num;
        if(decrNum<0){
            throw new RuntimeException("该商品库存不足！");

        }
        // 修改该商品的库存
         goods.setStoreCount((short)decrNum);
         update(goods);
    }

    /**
     * 执行库存回滚
     * @param goodsInfo
     */
    @Override
    public void recoveryStock(HashMap<Integer, Integer> goodsInfo) {
        //得到备份里面的goodsId
        Set<Integer> goodsIds = goodsInfo.keySet();
        for (Integer goodsId : goodsIds) {
            //通过goodsId，去查询数据库
            Goods goods = findById(goodsId);
            int dbNum = goods.getStoreCount().intValue();
            if (dbNum!=goodsInfo.get(goodsId)){
                // 改为一个影子值，或者备份的值
                 goods.setStoreCount(goodsInfo.get(goodsId).shortValue());
                 update(goods);
            }
        }

    }
    /**
     * 回滚库存
     * 这个回滚发生在往订单表写数据时，有异常，才回滚的
     * 意味着之前的库存-- 已经成功了，在这里面直接++ 该库存就ok 了
     */
    @Override
    public void recoveryStock(OrderVo orderVo) {
        List<OrderGoodsVo> goodsVoList = orderVo.getGoodsVoList();
        for (OrderGoodsVo orderGoodsVo : goodsVoList) {
            Goods goods = findById(orderGoodsVo.getId());
            //从数据库得到商品的商品的库存，再加上购物车的购买的数量
            int recoveryNum=goods.getStoreCount()+orderGoodsVo.getNum();
            goods.setStoreCount((short)recoveryNum);
            update(goods);
        }

    }
}
