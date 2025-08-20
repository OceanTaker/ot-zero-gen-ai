package com.oceantaker.otzerogenai.aop;

import com.oceantaker.otzerogenai.annotation.AuthCheck;
import com.oceantaker.otzerogenai.exception.BusinessException;
import com.oceantaker.otzerogenai.exception.ErrorCode;
import com.oceantaker.otzerogenai.model.entity.User;
import com.oceantaker.otzerogenai.model.enums.UserRoleEnum;
import com.oceantaker.otzerogenai.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor { // interceptor 拦截器

    @Resource
    private UserService userService;

    /**
     * 拦截所有使用 AuthCheck 注解的方法
     * @param joinPoint 切入点 代表被拦截的目标方法，通过 proceed()方法（必须调用）决定是否/何时执行目标方法
     * @param authCheck 权限校验注解
     * @return
     * @throws Throwable 抛出异常
     */
    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 1、提取注解角色要求
        // 从注解中获取 mustRole属性值（如 "admin"）
        String mustRole = authCheck.mustRole();

        // 2、获取当前请求与登录用户
        // 在非 Web 层（如 Service、AOP 切面）动态获取当前 HTTP 请求对象
        // 获取请求上下文属性 RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 将通用的 RequestAttributes强制转换为 ServletRequestAttributes（专为 Servlet 设计）
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取到当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 3、角色校验逻辑
        // 需要的权限转换为枚举类
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // mustRoleEnum == null说明无需权限，直接放行
        if (mustRoleEnum == null){
            return joinPoint.proceed();
        }
        // 以下代码：必须有这个权限才能通过
        // 当前登录的权限 转化为枚举类
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        // 当前登录用户没有任何权限
        if (userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须有管理员权限
        // 如果 当前需要管理员权限 而 当前登录的用户没有管理员权限
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 通过普通用户的权限校验，放行
        return joinPoint.proceed();
        // 这里只有两种权限，所以可以采用上面这种简单的写法
    }
}
