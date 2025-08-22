package com.oceantaker.otzerogenai.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.oceantaker.otzerogenai.model.dto.user.UserQueryRequest;
import com.oceantaker.otzerogenai.model.entity.User;
import com.oceantaker.otzerogenai.model.vo.LoginUserVO;
import com.oceantaker.otzerogenai.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/OceanTaker">程序员OceanTaker</a>
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return 脱敏的已登录用户信息
     */

    LoginUserVO getLoginUserVO(User user);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 请求
     * @return 脱敏后的登录用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     * 这里不脱敏是因为后续改造成微服务的话会有很多服务都要调用这个方法，会需要不止脱敏后的信息。
     * 这里使用的话在返回接口之前脱敏即可
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户信息（脱敏）
     * @param user 用户信息
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取当前登录用户信息（脱敏）（分页）
     * @param userList 用户信息
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);


    /**
     * 用户注销
     *
     * @param request
     * @return 退出登录是否成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 根据查询条件构造数据查询参数
     * @param userQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取加密密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);
}
