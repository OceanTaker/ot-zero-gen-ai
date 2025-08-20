package com.oceantaker.otzerogenai.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //限定注解仅能标注在方法上
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留，可通过 反射机制 动态读取
public @interface AuthCheck {

    /**
     * 必须有某个角色
     */
    // 通过 mustRole()指定访问该方法所需的角色（如 "admin"），默认值为空字符串（表示无需特定角色）
    String mustRole() default "";
}