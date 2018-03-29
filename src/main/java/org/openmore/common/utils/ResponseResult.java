package org.openmore.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by michaeltang on 2018/3/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T>{
    private String message;
    private int code;
    private T data;
    private Pagination pagination;

    public ResponseResult(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseResult(int code, String message, T data, Pagination pagination){
        this.code = code;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
