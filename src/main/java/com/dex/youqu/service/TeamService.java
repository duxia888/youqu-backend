package com.dex.youqu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dex.youqu.model.domain.Team;
import com.dex.youqu.model.domain.User;

/**
* @author axin
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-02-27 00:11:10
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

}
