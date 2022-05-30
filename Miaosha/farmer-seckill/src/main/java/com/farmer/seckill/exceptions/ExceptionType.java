package com.farmer.seckill.exceptions;

import java.util.logging.Level;


public enum ExceptionType {

    /**
     * 自定义异常
     */
    REDIS_EXCEPTION("9991"),
    MYSQL_EXCEPTION("9992"),
    ILLEGAL_PARAM("9993"),
    SYS_ERR("9999");

    final String code;
    final Level level;

    ExceptionType(String code){
        this.code=code;
        this.level= Level.WARNING;
    }

    ExceptionType(String code,Level level){
        this.code=code;
        this.level=level;
    }

    public String getCode(){
        return code;
    }
}
