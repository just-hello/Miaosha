package com.farmer.seckill.inout.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqGoodsDetailVO {

    @NotNull(message = "商品ID不能为空")
    private Long id;
}
