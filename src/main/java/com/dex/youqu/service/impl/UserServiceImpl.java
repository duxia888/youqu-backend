package com.dex.youqu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dex.youqu.common.ErrorCode;
import com.dex.youqu.exception.BusinessException;
import com.dex.youqu.mapper.UserMapper;
import com.dex.youqu.model.domain.User;
import com.dex.youqu.model.request.UserQueryRequest;
import com.dex.youqu.model.request.UserUpdatePassword;
import com.dex.youqu.service.UserService;
import com.dex.youqu.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dex.youqu.contant.SystemConstant.PAGE_SIZE;
import static com.dex.youqu.contant.UserConstant.ADMIN_ROLE;
import static com.dex.youqu.contant.UserConstant.USER_LOGIN_STATE;
import static com.dex.youqu.utils.StringUtils.stringJsonListToLongSet;

/**
 * 用户服务实现类
 *
 * @author axin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-02-07 16:55:39
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "axin";

    /**
     * 注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String userAccount, String username, String userPassword, String checkPassword) {
        //1.校验
        //StringUtils.isAllBlank():可以放多个参数，判断参数是否为空，是否为null
        if (StringUtils.isAllBlank(userAccount, username, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8位");
        }

        //账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }

        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        //账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUsername(username);
        user.setUserPassword(encryptPassword);
        user.setTags("[]");
        user.setUserIds("[]");
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存错误");
        }
        return user.getId();
    }

    /**
     * 登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        // StringUtils.isAllBlank():可以放多个参数，判断参数是否为空，是否为null
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号或密码为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8位");
        }

        // 账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号含有特殊字符");
        }

        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);

        // 4.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    @Override
    public User getSafetyUser(User orignUser) {
        if (orignUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(orignUser.getId());
        safetyUser.setUsername(orignUser.getUsername());
        safetyUser.setUserAccount(orignUser.getUserAccount());
        safetyUser.setAvatarUrl(orignUser.getAvatarUrl());
        safetyUser.setGender(orignUser.getGender());
        safetyUser.setPhone(orignUser.getPhone());
        safetyUser.setEmail(orignUser.getEmail());
        safetyUser.setUserRole(orignUser.getUserRole());
        safetyUser.setTags(orignUser.getTags());
        safetyUser.setUserIds(orignUser.getUserIds());
        safetyUser.setUserStatus(orignUser.getUserStatus());
        safetyUser.setCreateTime(orignUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        throw new BusinessException(ErrorCode.SUCCESS, "用户注销");
    }

    /**
     * 按标签搜索用户
     * 根据标签搜索用户（内存过滤）
     *
     * @param tagNameList 用户要拥有的标签
     * @param currentPage 当前页码
     * @return {@link Page}<{@link User}>
     */
    @Override
    public Page<User> searchUsersByTags(List<String> tagNameList, long currentPage) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        for (String tagName : tagNameList) {
            userLambdaQueryWrapper = userLambdaQueryWrapper
                    .or().like(Strings.isNotEmpty(tagName), User::getTags, tagName);
        }
        return page(new Page<>(currentPage, PAGE_SIZE), userLambdaQueryWrapper);
    }

    /**
     * 根据标签搜索用户 (内存过滤)
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Deprecated
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 在内存中判断是否包含要求的标签(语法糖：返回false就跳出循环)
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            //所有从数据库取出来的值都要进行校验
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            //集合都要判空
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行任何 update 语句
        // 如果是管理员，运行更新任意用户
        // 如果不是管理员，只允许更新当前（自己）的信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    @Override
    public void isLogin(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录");
        }
        Object objUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) objUser;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录");
        }
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        //仅管理员可查询
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    @Override
    public List<User> getFriendsById(User currentUser) {
        User loginUser = this.getById(currentUser.getId());
        Set<Long> friendsId = stringJsonListToLongSet(loginUser.getUserIds());
        return friendsId.stream().map(user -> this.getSafetyUser(this.getById(user))).collect(Collectors.toList());
    }

    @Override
    public boolean deleteFriend(User currentUser, Long id) {
        User loginUser = this.getById(currentUser.getId());
        User friendUser = this.getById(id);
        Set<Long> friendsId = stringJsonListToLongSet(loginUser.getUserIds());
        Set<Long> fid = stringJsonListToLongSet(friendUser.getUserIds());
        friendsId.remove(id);
        fid.remove(loginUser.getId());
        String friends = new Gson().toJson(friendsId);
        String fids = new Gson().toJson(fid);
        loginUser.setUserIds(friends);
        friendUser.setUserIds(fids);
        return this.updateById(loginUser) && this.updateById(friendUser);
    }

    @Override
    public List<User> userQuery(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        isLogin(request);
        String searchText = userQueryRequest.getSearchText();
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(User::getUsername, searchText).or().like(User::getProfile, searchText);
        return this.list(userLambdaQueryWrapper);
    }

    @Override
    public List<User> searchFriend(UserQueryRequest userQueryRequest, User currentUser) {
        String searchText = userQueryRequest.getSearchText();
        User user = this.getById(currentUser.getId());
        Set<Long> friendsId = stringJsonListToLongSet(user.getUserIds());
        List<User> users = new ArrayList<>();
        Collections.shuffle(users);
        friendsId.forEach(id -> {
            User u = this.getById(id);
            if (u.getUsername().contains(searchText)) {
                users.add(u);
            }
        });
        return users;
    }

    /**
     * 收到用户标签
     *
     * @param id id
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> getUserTags(Long id) {
        User user = this.getById(id);
        String userTags = user.getTags();
        Gson gson = new Gson();
        return gson.fromJson(userTags, new TypeToken<List<String>>() {
        }.getType());
    }

    /**
     * 更新标记
     *
     * @param tags   标签
     * @param userId 用户id
     */
    @Override
    public void updateTags(List<String> tags, Long userId) {
        User user = new User();
        Gson gson = new Gson();
        String tagsJson = gson.toJson(tags);
        user.setId(userId);
        user.setTags(tagsJson);
        this.updateById(user);
    }

    @Override
    public int updatePasswordById(UserUpdatePassword updatePassword, User currentUser) {
        long id = updatePassword.getId();
        String oldPassword = updatePassword.getOldPassword();
        String newPassword = updatePassword.getNewPassword();
        String checkPassword = updatePassword.getCheckPassword();
        if (StringUtils.isAnyBlank(oldPassword, newPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入有误");
        }
        // 密码就不小于8位吧
        if (oldPassword.length() < 8 || checkPassword.length() < 8 || newPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于8位");
        }
        // 密码和校验密码相同
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入密码不一致");
        }
        if (!isAdmin(currentUser) && currentUser.getId() != id) {
            throw new BusinessException(ErrorCode.NO_AUTH, "权限不足");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", currentUser.getUserAccount());
        userQueryWrapper.eq("userPassword", encryptPassword);
        User user = this.getOne(userQueryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        String newEncryptPassword = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes(StandardCharsets.UTF_8));

        currentUser.setUserPassword(newEncryptPassword);

        return userMapper.updateById(currentUser);
    }

    @Override
    public String redisFormat(Long key) {
        return String.format("youqu:user:search:%s", key);
    }

    /**
     * 根据标签搜索用户 (SQL 查询版)
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接 and 查询
        // like '%Java%' and like '%Python%'
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

    }
}




