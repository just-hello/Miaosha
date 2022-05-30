package com.farmer.seckill.inout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonJsonRequest<T> {

    @NotNull(message = "请求时间不能为空")
    private Long reqTime;

    @NotNull(message = "请求签名不能为空")
    private String sign;
    
    @Valid
    private T data;
}
