package com.farmer.seckill.entity.redis;

import lombok.Data;

import java.util.Date;


@Data
public class RedisGoods {

    private Long id;
    private String goodsName;
    private Date startTime;
    private Date endTime;
    private Integer goodsCount;
}
