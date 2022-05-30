package com.farmer.seckill.service;

import com.farmer.seckill.constants.TotalConstants;
import com.farmer.seckill.entity.Goods;
import com.farmer.seckill.entity.GoodsExample;
import com.farmer.seckill.entity.redis.RedisGoods;
import com.farmer.seckill.exceptions.ExceptionType;
import com.farmer.seckill.exceptions.ServiceException;
import com.farmer.seckill.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoodsService {

    private final GoodsMapper goodsMapper;

    private final RedisTemplate<String,Object> redisTemplate;


    public Object list4Page(Integer page, Integer rows) {
        Map<String,Object> retMap = new HashMap<>(2);
        final long l = goodsMapper.countByExample(null);
        final List<Goods> goods = goodsMapper.selectByPageExample((page-1)*rows, rows, null);
        retMap.put("total",l);
        retMap.put("rows",goods);
        return retMap;
    }

    public Integer addGoods(Goods goods, HttpSession session) {
        Date now = new Date();
        final String userId = session.getId();
        //System.out.println(userId);
        goods.setCreateTime(now);
        goods.setCreateUser(userId);
        goods.setUpdateTime(now);
        goods.setUpdateUser(userId);
        goods.setTotalCount(goods.getGoodsCount());
        return goodsMapper.insertSelective(goods);
    }

    public List<Goods> listSeckillGoods() {
        GoodsExample example = new GoodsExample();
        example.createCriteria()
                .andEndTimeGreaterThan(new Date());
        example.setOrderByClause(" start_time asc ");
        return goodsMapper.selectByExample(example);
    }

    public Goods getOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }


    public Goods decrementGoods(Long id) {
        int i = goodsMapper.decrement(id);
        if(i <= 0){
            throw new ServiceException("库存减扣失败", ExceptionType.SYS_ERR);
        }
        return goodsMapper.selectByPrimaryKey(id);
    }

    public int incrementGoods(Long goodsId) {
        int i = goodsMapper.increment(goodsId);
        if(i <= 0){
            throw new ServiceException("库存归还失败", ExceptionType.SYS_ERR);
        }
        return i;
    }
}
