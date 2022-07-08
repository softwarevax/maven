package com.github.softwarevax.support.demo.configuration;

import com.github.softwarevax.support.result.ResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ControllerAdvice
public class DefaultGlobalExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultDto<String> illegalArgumentException(IllegalArgumentException e) {
        return ResultDto.failT(e.getMessage());
    }
}
