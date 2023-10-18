package cn.homyit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

//    public static Result ok(String msg){return new Result(Code.LOGIN_OK, msg);}
//    public static Result err(String msg){return new Result(Code.LOGIN_ERR,msg);}
}
