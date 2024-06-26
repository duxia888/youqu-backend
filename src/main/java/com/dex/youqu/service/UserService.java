package com.dex.youqu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dex.youqu.model.domain.User;
import com.dex.youqu.model.request.UserQueryRequest;
import com.dex.youqu.model.request.UserUpdatePassword;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author axin
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-02-07 16:55:39
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */

    long userRegister(String userAccount, String username, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 请求体
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param orignUser 传进来的用户
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User orignUser);

    /**
     * 用户注销
     *
     * @param request 请求
     */
    int userLogout(HttpServletRequest request);

    /**
     * 按标签搜索用户
     *
     * @param tagNameList 标记名称列表
     * @param currentPage 当前页码
     * @return {@link Page}<{@link User}>
     */
    Page<User> searchUsersByTags(List<String> tagNameList, long currentPage);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 当前是否登录
     *
     * @param request
     * @return
     */
    void isLogin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return 是否为管理员的布尔值
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return 是否为管理员的布尔值
     */
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);

    /**
     * 根据id获取好友列表
     *
     * @param currentUser
     * @return
     */
    List<User> getFriendsById(User currentUser);

    boolean deleteFriend(User currentUser, Long id);

    /**
     * 搜索用户
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    List<User> userQuery(UserQueryRequest userQueryRequest, HttpServletRequest request);

    /**
     * 搜索好友
     *
     * @param userQueryRequest
     * @param currentUser
     * @return
     */
    List<User> searchFriend(UserQueryRequest userQueryRequest, User currentUser);

    /**
     * 收到用户标签
     *
     * @param id id
     * @return {@link List}<{@link String}>
     */
    List<String> getUserTags(Long id);

    /**
     * 更新标记
     *
     * @param tags   标签
     * @param userId 用户id
     */
    void updateTags(List<String> tags, Long userId);

    /**
     * 修改密码
     *
     * @param updatePassword
     * @param currentUser
     * @return
     */
    int updatePasswordById(UserUpdatePassword updatePassword, User currentUser);

    /**
     * redisKey
     *
     * @param key
     * @return
     */
    String redisFormat(Long key);
}
