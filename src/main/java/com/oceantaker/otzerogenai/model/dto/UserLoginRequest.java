package com.oceantaker.otzerogenai.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    /**
     * 用户登录
     */

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */private String userAccount;

    /**
     * 密码
     */private String userPassword;
}