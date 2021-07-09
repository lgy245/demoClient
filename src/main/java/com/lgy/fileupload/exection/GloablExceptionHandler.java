package com.lgy.fileupload.exection;

import com.lgy.fileupload.util.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * @Desc:全局异常捕获
 * @Author:lgy
 * @Time:2021/6/1715:53
 */

@Slf4j
@ControllerAdvice
public class GloablExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)

/*
     *
     * @author lgy
     * @date 2021/6/17 16:42
     * @param e
     * @return com.lgy.util.ResultInfo
    */

    public ResultInfo handleException(Exception e){

        String msg = e.getMessage();
        if(msg=="" || msg == null){
            msg ="服务器出错";
        }
        log.warn("handleException, : {}", e);
        System.err.println(e);
        return ResultInfo.fail(msg);
    }
}
