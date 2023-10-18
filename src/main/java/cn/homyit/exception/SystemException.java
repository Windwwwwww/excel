package cn.homyit.exception;

import cn.homyit.domain.Code;

public class SystemException extends RuntimeException{
    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemException(int httpCodeEnum,String msg) {
        super(msg);
        this.code = httpCodeEnum;
        this.msg = msg;
    }
}
