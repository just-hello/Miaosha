package com.farmer.seckill.inout.in;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class ReqPayCallbackVO {

    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotBlank(message = "支付流水号不能为空")
    private String paySeq;
    @NotBlank(message = "支付状态不能为空")
    private String payStatus;
}
