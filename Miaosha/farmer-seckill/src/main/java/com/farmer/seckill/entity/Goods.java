package com.farmer.seckill.entity;

import lombok.Data;
import org.springframework.data.redis.core.ZSetOperations;

@Data
public class Goods extends BaseGoods implements ZSetOperations.TypedTuple<Object> {

    private Long startTimeLong;


    @Override
    public Goods getValue() {
        return null;
    }

    @Override
    public Double getScore() {
        return null;
    }

    @Override
    public int compareTo(ZSetOperations.TypedTuple<Object> o) {
        return 0;
    }
}