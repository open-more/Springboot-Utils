package org.openmore.common.exception;

/**
 * Created by michaeltang on 2018/3/22.
 */
public class ForbiddenException extends RuntimeException {
    private int statusCode;
    private String msg;

    public ForbiddenException() {
        this("无权访问");
    }

    public ForbiddenException(String msg) {
        super(msg);
        this.statusCode = 400;
        this.msg = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
