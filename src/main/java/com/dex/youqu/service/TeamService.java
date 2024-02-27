package com.dex.youqu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dex.youqu.model.domain.Team;
import com.dex.youqu.model.domain.User;
import com.dex.youqu.model.dto.TeamQuery;
import com.dex.youqu.model.request.TeamJoinRequest;
import com.dex.youqu.model.request.TeamQuitRequest;
import com.dex.youqu.model.request.TeamUpdateRequest;
import com.dex.youqu.model.vo.TeamUserVO;

import java.util.List;

/**
* @author axin
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-02-27 00:11:10
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team 传入的用户添加队伍信息
     * @param loginUser 当前登录用户信息
     * @return 队伍id
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery 传入的用户查询队伍信息封装
     * @return 搜索后队伍列表
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest 传入的用户更新队伍请求体
     * @param loginUser 当前登录用户信息
     * @return 是否更新成功的布尔值
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 传入的用户加入队伍请求体
     * @param loginUser 当前登录用户信息
     * @return 是否加入队伍的布尔值
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest 用户退出队伍请求体
     * @param loginUser 当前登录用户信息
     * @return 是否退出队伍的布尔值
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散）队伍
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);
}
