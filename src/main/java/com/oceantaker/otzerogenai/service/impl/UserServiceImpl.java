package com.oceantaker.otzerogenai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.oceantaker.otzerogenai.exception.BusinessException;
import com.oceantaker.otzerogenai.exception.ErrorCode;
import com.oceantaker.otzerogenai.model.dto.UserQueryRequest;
import com.oceantaker.otzerogenai.model.entity.User;
import com.oceantaker.otzerogenai.mapper.UserMapper;
import com.oceantaker.otzerogenai.model.enums.UserRoleEnum;
import com.oceantaker.otzerogenai.model.vo.LoginUserVO;
import com.oceantaker.otzerogenai.model.vo.UserVO;
import com.oceantaker.otzerogenai.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.oceantaker.otzerogenai.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/OceanTaker">程序员OceanTaker</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1、校验参数
        if(StrUtil.hasBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }

        if(userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2、查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
        }

        // 3、密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4、创建用户，存入数据库
        User user = new User().builder()
                .userAccount(userAccount)
                .userPassword(encryptPassword)
                .userName("海嗣")
                .userRole(UserRoleEnum.USER.getValue())
                .build();
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        // 仅当 User（源对象）和 LoginUserVO（目标对象）存在同名属性时，才会复制值。
        // 是浅拷贝
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、校验参数
        if(StrUtil.hasBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 2、加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3、查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        if(user == null){
            // 模糊错误，防止攻击者对症下药
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号或密码错误");
        }
        // 4、如果用户存在，记录用户的登录态
        // 将用户对象存储到当前会话（Session）中
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 5、返回脱敏后的用户信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        /**
         * mybaits flex提供的现成的查询方法
         * default T getById(Serializable id) {
         *         return this.getMapper().selectOneById(id);
         *     }
         */
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public UserVO getUserVO(User user) {
        if(user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        // 仅当 User（源对象）和 LoginUserVO（目标对象）存在同名属性时，才会复制值。
        // 是浅拷贝
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>(); // 若为空返回空列表
        }
        //
        return userList.stream()
                // map将流中每个元素（User）映射为新对象（UserVO），生成新流
                .map(this::getUserVO)
                // 收集成新列表
                // .collect(Collectors.toList())返回一个可变的 ArrayList，支持后续的增删改操作
                .collect(Collectors.toList());
    }

    @Override
    // request 是每个用户都不一样的
    public boolean userLogout(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 1. 参数校验：确保请求对象非空。
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 2. 参数提取：从请求对象中获取查询条件字段
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        // 3. 条件构建：通过链式调用组合查询条件（等值、模糊匹配、排序） 同时返回封装
        return QueryWrapper.create() // QueryWrapper支持链式调用
                .eq("id", id) // where id = ${id}
                .eq("userRole", userRole) // and userRole = ${userRole}
                .like("userAccount", userAccount) // and userAccount like '%${userAccount}%'
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public String getEncryptPassword(String userPassword){
        // 密码加密
        final String SALT = "ocean";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }
}

