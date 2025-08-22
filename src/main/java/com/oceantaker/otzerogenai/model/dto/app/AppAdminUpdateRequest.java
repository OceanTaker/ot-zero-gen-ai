package com.oceantaker.otzerogenai.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员更新应用请求
 *
 * @author oceantaker
 * @description 针对表【app(应用)】的数据库操作输入对象
 */
@Data
public class AppAdminUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 优先级
     */
    private Integer priority;

    private static final long serialVersionUID = 1L;
}
