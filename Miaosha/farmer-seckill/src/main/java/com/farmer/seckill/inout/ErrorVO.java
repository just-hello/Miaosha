package com.farmer.seckill.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Data
@Slf4j
public class ErrorVO {

    private Map<String, String[]> params;
    private String desc;

    public ErrorVO( Map<String, String[]> params, String desc) {
        this.params = params;
        this.desc = desc;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            log.warn("toString error.", e);
            return String.format("ErrorDTO{params=%s, desc='%s'}",params,desc);
        }
    }
}
