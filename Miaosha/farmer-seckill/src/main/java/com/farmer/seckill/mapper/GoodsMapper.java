package com.farmer.seckill.mapper;

import com.farmer.seckill.entity.Goods;
import com.farmer.seckill.entity.GoodsExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper extends BaseGoodsMapper {

    List<Goods> selectByPageExample(@Param("offset") Integer offset,@Param("rows") Integer rows,@Param("example") GoodsExample example);

    int decrement(Long id);

    int increment(Long goodsId);
}