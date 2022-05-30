package com.farmer.seckill.service;

import com.farmer.seckill.constants.TotalConstants;
import com.farmer.seckill.entity.Goods;
import com.farmer.seckill.entity.SecOrder;
import com.farmer.seckill.entity.redis.RedisGoods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final GoodsService goodsService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final OrderService orderService;

    @Scheduled(cron = "0/5 * * * * *")
    public void loadIntoRedis(){
        //添加未过期的商品
        final List<Goods> goodsList = goodsService.listSeckillGoods();
        if(!goodsList.isEmpty()){
            StringBuilder sb = new StringBuilder("redis.call('lpush',KEYS[1]");
            Object[] argv = new Object[goodsList.size() + 1];
            argv[0] = goodsList.size() - 1;
            for (int i = 0; i < goodsList.size(); i++) {
                RedisGoods redisGoods = new RedisGoods();
                BeanUtils.copyProperties(goodsList.get(i), redisGoods);
                argv[i + 1] = redisGoods;
                sb.append(",").append("ARGV[").append(i + 2).append("]");
            }
            sb.append(")\r\n");
            sb.append("if redis.call('ltrim',KEYS[1],0,ARGV[1]) == 'OK' then return 1 else return 0 end");
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(sb.toString(), Long.class);
            redisTemplate.execute(redisScript, Arrays.asList(TotalConstants.SECKILL_GOODS_LIST_PREFIX), argv);
        }
    }


    @Scheduled(cron = "0/30 * * * * *")
    public void expireOrder(){
        final List<SecOrder> secOrders = orderService.queryExpireOrder();
        log.info("订单超时未支付取消任务，size:{}",secOrders.size());
        for (SecOrder secOrder : secOrders) {
            try {
                orderService.cancelExpireOrder(secOrder);
                final String detailKey = TotalConstants.SECKILL_GOODS_DETAIL_PREFIX + secOrder.getGoodsId();
                String lua = "local c = redis.call('hget',KEYS[1],'goodsCount')\r\n" +
                        "if tonumber(c) > 0 then \r\n" +
                        "return redis.call('hincrby',KEYS[1],'goodsCount',1)\r\n" +
                        "else " +
                        "return redis.call('hset',KEYS[1],'goodsCount',1)\r\n" +
                        "end " ;
                DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>(lua,Long.class);
                final Long execute = redisTemplate.execute(defaultRedisScript, Arrays.asList(detailKey), 1);
                log.info("订单超时未支付取消成功，orderNo:{}",secOrder.getOrderNo());
            } catch (Exception e) {
                log.error("订单超时未支付取消失败",e);
            }
        }
    }
}
