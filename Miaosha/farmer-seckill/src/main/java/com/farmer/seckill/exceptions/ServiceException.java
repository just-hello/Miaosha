package com.farmer.seckill.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceException extends RuntimeException {
    protected String msg;
    protected ExceptionType exceptionType;
}
