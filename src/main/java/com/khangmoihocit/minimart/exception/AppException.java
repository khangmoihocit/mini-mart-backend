package com.khangmoihocit.minimart.exception;

import com.khangmoihocit.minimart.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
