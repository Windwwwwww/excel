package cn.homyit.handler;

import cn.homyit.domain.Code;
import cn.homyit.domain.Result;
import cn.homyit.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class)
    public Result systemExceptionHandler(SystemException e){
        //打印异常信息
        log.error("出现了异常！{}",e);
        //从异常对象中获取提示信息封装返回
        return new Result(e.getCode(),e.getMsg());
    }
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e){
        //打印异常信息
        log.error("出现了异常！{}",e);
        //从异常对象中获取提示信息封装返回
        return new Result(Code.SYSTEM_ERR,e.getMessage());
    }
}
