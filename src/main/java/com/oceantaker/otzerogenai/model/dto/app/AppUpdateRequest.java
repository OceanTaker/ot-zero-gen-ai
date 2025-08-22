package com.oceantaker.otzerogenai.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oceantaker
 *  更新应用请求
 */
@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称 只允许用户修改此字段，初始提示词不能修改
     */
    private String appName;

    private static final long serialVersionUID = 1L;
}
