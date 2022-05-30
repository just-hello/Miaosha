package com.farmer.seckill.inout;

import lombok.Data;


@Data
public class CommonJsonResponse<T> {
    public static final String MSG_SUCCESS = "SUCCESS";
    public static final String MSG_NOT_FOUND = "NOT FOUND";
    public static final String RESP_CODE_SUCCESS = "0000";
    public static final int ERROR_CODE_NO_SUCH_OBJECT = 404;
    /**
     * 0 means success
     */
    private String respCode;
    /**
     * SUCCESS or others
     */
    private String respMsg;

    private Long respTime;
    /**
     * the result data, may user info or others
     */
    private T data;
    public CommonJsonResponse() {
        this.respTime = System.currentTimeMillis();
    }
    public CommonJsonResponse( String respCode) {
        this.respCode = respCode;
        this.respTime = System.currentTimeMillis();
    }
    public CommonJsonResponse(String respCode, String respMsg) {
        this.respCode = respCode;
        this.respMsg = respMsg;
        this.respTime = System.currentTimeMillis();
    }
    public static CommonJsonResponse ok() {
        CommonJsonResponse res = new CommonJsonResponse( CommonJsonResponse.RESP_CODE_SUCCESS);
        res.setRespMsg(CommonJsonResponse.MSG_SUCCESS);
        return res;
    }

    public static<T> CommonJsonResponse<T> ok(T t) {
        CommonJsonResponse<T> res = new CommonJsonResponse<T>(CommonJsonResponse.RESP_CODE_SUCCESS);
        res.setRespMsg(CommonJsonResponse.MSG_SUCCESS);
        res.setData(t);
        return res;
    }
}