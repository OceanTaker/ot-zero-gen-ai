package com.oceantaker.otzerogenai.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);  // 调用父类构造方法，RuntimeException类已经定义了构造方法，用于设置错误信息
        this.code = code;
    }

    // 使用自己刚刚定义的枚举类中的错误码和错误信息
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    // 使用自己刚刚定义的枚举类中的错误码和错误信息，并添加自定义错误信息
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
