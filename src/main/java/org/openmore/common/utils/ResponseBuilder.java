package org.openmore.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.Nullable;

/**
 * Created by michaeltang on 2018/3/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBuilder<T> {
    public static ResponseResult success(){
        return new ResponseResult(0, "ok", null);
    }
    public static<T> ResponseResult<T> success(@Nullable T body){
        return new ResponseResult<T>(0, "ok", body);
    }

    public static<T> ResponseResult<T> success(@Nullable T body, @Nullable Pagination pagination){
        return new ResponseResult<T>(0, "ok", body, pagination);
    }

    public static ResponseResult error(@Nullable String msg, int code){
        return new ResponseResult(code, msg, null);
    }
}
