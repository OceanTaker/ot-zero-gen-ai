package com.oceantaker.otzerogenai.model.dto;

import com.oceantaker.otzerogenai.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

// 智能生成 equals()和 hashCode()方法
// callSuper = true ：生成的 equals()会调用 super.equals()，hashCode()会包含父类字段的哈希值
@EqualsAndHashCode(callSuper = true)
@Data
// 继承之前第二章自定义的通用的请求封装类 PageRequest， 只要分页的查询请求都会用到
// 下面的这些属性， 都是分页查询的请求参数。还有别的想根据查的字段可以添加
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
     private Long id;

    /**
     * 用户昵称
     */
     private String userName;

    /**
     * 账号
     */
     private String userAccount;

    /**
     * 简介
     */
     private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
     private String userRole;

    private static final long serialVersionUID = 1L;
}