package com.farmer.seckill.service;

import com.alibaba.fastjson.JSON;
import com.farmer.seckill.entity.Goods;
import com.farmer.seckill.entity.SecOrder;
import com.farmer.seckill.entity.SecOrderExample;
import com.farmer.seckill.exceptions.ExceptionType;
import com.farmer.seckill.exceptions.ServiceException;
import com.farmer.seckill.inout.in.ReqPayCallbackVO;
import com.farmer.seckill.mapper.SecOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final SecOrderMapper orderMapper;

    private final GoodsService goodsService;


    @Transactional(rollbackFor = Exception.class)
    public SecOrder generateSecOrder(Long id, HttpSession session) {
        final LocalDateTime now = LocalDateTime.now();
        final Goods goods = goodsService.decrementGoods(id);
        SecOrder order = new SecOrder();
        order.setOrderNo(UUID.randomUUID().toString().replace("-",""));
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsNum(1);
        order.setGoodsSnapshots(JSON.toJSONString(goods));
        order.setAmount(goods.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNum())));
        order.setOrderStatus("00");
        //模拟微信支付下单，获取下单支付流水号
        final String paySeq = UUID.randomUUID().toString().replace("-", "");
        order.setPaySeq(paySeq);
        final String userId = session.getId();
        order.setUserId(userId);
        order.setCreateTime(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
        order.setUpdateTime(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
        order.setUpdateUser(userId);
        order.setExpireTime(Date.from(now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()));
        final int i = orderMapper.insertSelective(order);
        if(i != 1){
            throw new ServiceException("下单失败", ExceptionType.SYS_ERR);
        }
        return order;
    }

    public void payCallback(ReqPayCallbackVO data) {
        SecOrderExample example = new SecOrderExample();
        example.createCriteria()
                .andOrderNoEqualTo(data.getOrderNo())
                .andPaySeqEqualTo(data.getPaySeq())
                .andOrderStatusEqualTo("00");
        SecOrder order = new SecOrder();
        Date now = new Date();
        order.setPayTime(now);
        order.setUpdateTime(now);
        order.setOrderStatus(data.getPayStatus());
        orderMapper.updateByExampleSelective(order,example);
    }

    public List<SecOrder> queryExpireOrder() {
        SecOrderExample example = new SecOrderExample();
        example.createCriteria()
                .andOrderStatusEqualTo("00")
                .andExpireTimeLessThan(new Date());

        return orderMapper.selectByExample(example);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelExpireOrder(SecOrder secOrder) {
        SecOrderExample example = new SecOrderExample();
        example.createCriteria()
                .andIdEqualTo(secOrder.getId())
                .andOrderStatusEqualTo("00");
        SecOrder updateOrder = new SecOrder();
        updateOrder.setOrderStatus("03");
        updateOrder.setUpdateTime(new Date());
        final int i = orderMapper.updateByExampleSelective(updateOrder, example);
        if(i <= 0){
            throw new RuntimeException("更新订单状态失败");
        }
        goodsService.incrementGoods(secOrder.getGoodsId());
    }
}
