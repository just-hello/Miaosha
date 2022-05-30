package com.farmer.seckill.inout.out;

import com.farmer.seckill.entity.Goods;
import lombok.Builder;
import lombok.Data;

import java.util.List;



@Data
@Builder
public class SeckillGoodsListVO {

    private List<Object> list;
}
