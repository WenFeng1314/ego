package com.sxt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.sxt.domain.*;


import com.sxt.mapper.OrderActionMapper;
import com.sxt.mapper.OrderGoodsMapper;
import com.sxt.mapper.OrderMapper;
import com.sxt.model.Page;
import com.sxt.service.CartService;
import com.sxt.service.GoodsService;
import com.sxt.utils.DateUtil;
import com.sxt.utils.ThreadLocalUtil;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.sxt.enums.OrderStatus;
import com.sxt.enums.PayStatus;
import com.sxt.enums.ShipingStatus;
import com.sxt.model.OrderGoodsVo;
import com.sxt.model.OrderVo;
import com.sxt.service.OrderService;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service(timeout = 7000, retries = 0)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderGoodsMapper orderGoodsMapper;
    @Autowired
    private OrderActionMapper orderActionMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Reference
    private GoodsService goodsService;
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private CartService cartService;


    @Override
    public Page<Order> findOrders(Integer uid, Integer status, int page, int size) {
        OrderExample orderExample = new OrderExample();
        OrderExample.Criteria createCriteria = orderExample.createCriteria();
        createCriteria.andUserIdEqualTo(uid);
        createCriteria.andPayStatusEqualTo(status==0?PayStatus.NOT_PAY.getCode():status==1?PayStatus.PAYED.getCode():PayStatus.PAY_FAIL.getCode());
        com.github.pagehelper.Page<Object> startPage = PageHelper.startPage(page, size);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        return new Page<Order>(page, size, startPage.getTotal(), orders);

    }

    @Override
    public void payComplate(String orderSn) {
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andOrderSnEqualTo(orderSn);

        List<Order> orders = orderMapper.selectByExample(orderExample);
        if(orders==null||orders.isEmpty()) {
            throw new RuntimeException("该订单不存在");
        }
        Order order = orders.get(0);
        order.setPayStatus(PayStatus.PAYED.getCode());
        order.setOrderStatus(OrderStatus.PAY_ORDER.getCode());
        orderMapper.updateByPrimaryKey(order);


        // 记录订单的日志
        OrderAction orderAction = new OrderAction();
        orderAction.setActionUser(0);
        orderAction.setActionNote("订单支付成功");
        orderAction.setOrderId(order.getId());
        orderAction.setOrderStatus(OrderStatus.PAY_ORDER.getCode());
        orderAction.setPayStatus(PayStatus.PAYED.getCode());
        orderAction.setShippingStatus(ShipingStatus.NOT_SHIP.getCode());

        orderActionMapper.insert(orderAction);
        // 给用户发微信通知
        jmsTemplate.convertAndSend("ego.order.pay", order);

    }

    @Override
    public int queryOrderPayStatus(String orderSn) {
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andOrderSnEqualTo(orderSn);

        List<Order> orders = orderMapper.selectByExample(orderExample);
        if(orders==null||orders.isEmpty()) {
            throw new RuntimeException("该订单不存在");
        }
        Order order = orders.get(0);
        return order.getPayStatus()==(byte)1?1:0;

    }

    @Override
    public void createPreOrder() {
        System.out.println("生成预订单，需要大量的业务逻辑，还要写很多数据库，耗时很大");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("预订单写数据库成功");
    }

    @Transactional
    public void createPreOrder(OrderVo orderVo) {
        // 生成订单
        //		 1 清空购物车
        List<Integer> goodsIdList = new ArrayList<Integer>();
        orderVo.getGoodsVoList().forEach((gid) -> {
            goodsIdList.add(gid.getId());// 获取所有的商品id
        });
        // 清空购物车简单，但是若中途发生了异常，比较难
        cartService.clearCart(orderVo.getUid(), goodsIdList);

        //		 2 库存--对数据库的操作 GooodsService->manager-api
        // 若库存-- 失败了，则直接返回，没有必要再生成订单
        if (!decrStock(orderVo)) {
            return;
        }


        try {
            //1.生成1个订单消息
            Order order = createOrder(orderVo);
            // 2 写order 和goods的关系表
            handerlOrderGoods(orderVo, order);
            // 3 写orderAction ，记录order 的变化
            handlerOrderLog(order, orderVo.getUid());
            // 4 订单创建成功了，我向队列里面发延迟消息，让它在一段时间后自动删除
            jmsTemplate.send("ORDER.DELETE.QUEUE", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    //通过session创建一个文本消息
                    TextMessage textMessage = session.createTextMessage();
                    // 这里订单的编号，不是id
                    textMessage.setText(order.getOrderSn());
                    System.out.println(order.getOrderSn()+"订单的编号");
                    //设置过期时间
                    textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 220* 1000);
                    return textMessage;
                }
            });
        } catch (JmsException e) {
            // 操作order 表已经相关表，失败了
            //  需要 远程的回滚
            cartService.recovery(orderVo); // 恢复购物车
            goodsService.recoveryStock(orderVo); // 恢复库存

            // 手动扔异常的原因是，我向让本地也回滚
            //因为try/catch之后，就吃掉了异常，默认，不会回滚，所有，需要手动仍异常
            throw new RuntimeException(e);
        }

    }

    /**
     * 在设定的时间里，没有支付成功，取消订单
     *
     * @param orderSn
     */
    @Override
    public void autoCancel(String orderSn) {
        //第一步，验证
        Assert.notNull(orderSn, "订单号不能为null");
        //创建条件，根据orderSn去查询数据库
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        if (orders == null || orders.size() == 0) {
            throw new RuntimeException("该订单号在数据库，不存在！");
        }
        //只有一个订单，则
        Order order = orders.get(0);
        Byte payStatus = order.getPayStatus();
        //从数据库查询的支付状态比较，是否支付，再修改，订单的状态
        if (PayStatus.PAYED.getCode().equals(payStatus)) {
            System.out.println(payStatus+"支付状态");
               return;

        }
        order.setPayStatus(OrderStatus.CANSEL_ORDER.getCode());//// 该订单已经取消
        orderMapper.updateByPrimaryKeySelective(order);
        //生成order和action表的数据
        OrderAction orderAction = new OrderAction();
        orderAction.setOrderId(order.getId());
        orderAction.setActionUser(0);
        orderAction.setPayStatus(PayStatus.NOT_PAY.getCode());
        orderAction.setOrderStatus(OrderStatus.CANSEL_ORDER.getCode());
        orderAction.setShippingStatus(ShipingStatus.NOT_SHIP.getCode());
        orderAction.setActionNote("系统自动延时取消该订单");
        orderActionMapper.insert(orderAction);
        //因为取消了订单，所以都有回滚
        //  redis 库存回滚
        // 数据库里面库存也要回滚
        recoveryStock(order.getId());

    }

    //  redis 库存回滚
    // 数据库里面库存也要回滚
    private void recoveryStock(Integer id) {
        //1.先根据订单id去查询商品订单
        OrderGoodsExample orderGoodsExample = new OrderGoodsExample();
        orderGoodsExample.createCriteria().andOrderIdEqualTo(id);
        //得到的orderGoods，里面有订单商品的数量
        List<OrderGoods> orderGoods = orderGoodsMapper.selectByExample(orderGoodsExample);
        Jedis jedis = null;
        try {
            for (OrderGoods orderGood : orderGoods) {
                //得到需要回滚的数量
                Short goodsNum = orderGood.getGoodsNum();
                //得到所有redis里面的所有库存
                jedis = jedisPool.getResource();
                String goodsNumLast = jedis.hget("goods-stock", orderGood.getGoodsId() + "");
                //回滚redis里面的数量
                jedis.hset("goods-stock", orderGood.getGoodsId() + "", Integer.valueOf(goodsNum) + Integer.valueOf(goodsNumLast) + "");
                //再回滚数据库里的数量
                Goods goods = goodsService.findById(orderGood.getGoodsId());
                goods.setStoreCount((short) (goods.getStoreCount() + orderGood.getGoodsNum()));
                goodsService.update(goods);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    /**
     * 对数据库，redis--
     *
     * @param orderVo
     * @return
     */
    private boolean decrStock(OrderVo orderVo) {
        boolean flag = false;
        // 保存商品在修改之前的影子值
        HashMap<Integer, Integer> goodsInfo = new HashMap<Integer, Integer>();
        String userNote = "" ;
        //先得到订单的所有数量
        List<OrderGoodsVo> goodsVoList = orderVo.getGoodsVoList();
        Jedis jedis = null;
        try {// 远程调用开始
            for (OrderGoodsVo orderGoodsVo : goodsVoList) {
                //得到订单的商品id
                Integer goodsId = orderGoodsVo.getId();
                //得到订单的商品要买的数量
                Integer num = orderGoodsVo.getNum();
                //通过goodsId去查询商品，并得到商品的库存


                Goods goods = goodsService.findById(goodsId);
                if(userNote.length()<=5) {
                    userNote += goods.getGoodsName();
                }

                //把还没有修改的库存值存到goodsInfo里，（影子值，备份）
                goodsInfo.put(goodsId, goods.getStoreCount().intValue());
                //1.再执行库存--a.可能成功 b.可能失败,失败需要手动回滚
                goodsService.decrStock(goodsId, num);
                //2.redis库存减减
                //得到所有redis里面的所有库存
                jedis = jedisPool.getResource();
                String goodsNumLast = jedis.hget("goods-stock", goodsId + "");
                //执行redis里面的数量减减
                jedis.hset("goods-stock", goodsId + "", Integer.valueOf(goodsNumLast)-Integer.valueOf(num) + "");


            }
            ThreadLocalUtil.set("userNote", userNote); // 在线程里面放了一个userNote 值
            flag = true;
        } catch (Exception e) {//再远程调用里面，出了异常需要回滚，只能手动回滚
            e.printStackTrace();
            System.out.println("进来了，库存--失败，需要恢复到影子值");
            goodsService.recoveryStock(goodsInfo);
            //执行了，回滚，则下面的订单，无需再生成了，直接返回false
            return false;

        }
        return flag;

    }

    /**
     * 订单和商品的中间表的对应关系
     *
     * @param orderVo
     * @param order
     */
    private void handerlOrderGoods(OrderVo orderVo, Order order) {
        List<OrderGoodsVo> goodsVoList = orderVo.getGoodsVoList();
        for (OrderGoodsVo orderGoodsVo : goodsVoList) {
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setOrderId(order.getId());
            orderGoods.setGoodsId(orderGoodsVo.getId());
            orderGoods.setGoodsNum(orderGoodsVo.getNum().shortValue());
            orderGoods.setGoodsPrice(new BigDecimal(orderGoodsVo.getShopPrice()));
            orderGoodsMapper.insert(orderGoods);
        }
    }

    /**
     * 订单的改变记录
     *
     * @param order
     * @param uid
     */
    public void handlerOrderLog(Order order, Integer uid) {
        OrderAction orderAction = new OrderAction();
        orderAction.setOrderId(order.getId());
        orderAction.setActionUser(uid);
        orderAction.setActionNote("生成一个预订单");
        // 状态的设置
        orderAction.setOrderStatus(OrderStatus.PRE_ORDER.getCode());
        orderAction.setPayStatus(PayStatus.NOT_PAY.getCode());
        orderAction.setShippingStatus(ShipingStatus.NOT_SHIP.getCode());

        orderAction.setLogTime(Integer.valueOf(DateUtil.getTadayDate("yyyyMMdd")));
        orderActionMapper.insert(orderAction);
    }

    /**
     * 生成一个订单
     *
     * @param orderVo
     * @return
     */
    public Order createOrder(OrderVo orderVo) {
        Order order = new Order();
        String tadayDate = DateUtil.getTadayDate("yyyyMMdd");
        Long incr = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //此方法可以自增
            incr = jedis.incr(tadayDate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        String orderSn = tadayDate + getFullZone(incr.intValue(), 5);//生成订单号2019060400001
        order.setOrderSn(orderSn);
        order.setTotalAmount(new BigDecimal(orderVo.getTotalMoney()));
        order.setUserId(orderVo.getUid());
        order.setUserNote(ThreadLocalUtil.get("userNote").toString());// 运行我这些方法的都是一个线程
        // 状态的设置
        order.setOrderStatus(OrderStatus.PRE_ORDER.getCode());
        order.setPayStatus(PayStatus.NOT_PAY.getCode());
        order.setShippingStatus(ShipingStatus.NOT_SHIP.getCode());
        // 设置添加时间和支付时间
        order.setAddTime(Integer.valueOf(DateUtil.getTadayDate("MMddHHmm")));
        order.setPayTime(0);

        orderMapper.insert(order);

        return order; // 因为写后的order 有id ，我们后面的表，都需要改id
    }
    /**
     * 输入一个整数
     * @param i 整数 1
     * @param size 5  00001
     *  代表返回字符串的长度
     */

       private String getFullZone(int i, int size) {
           String currentValue=i+"";
           if (size<currentValue.length()){
               return "-1";
           }
           while (size-currentValue.length()>0){
               currentValue="0"+currentValue;
           }
           return currentValue;

    }

}
