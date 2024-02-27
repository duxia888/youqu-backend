package com.dex.youqu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dex.youqu.mapper.UserTeamMapper;
import com.dex.youqu.model.domain.UserTeam;
import com.dex.youqu.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author axin
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-02-27 00:12:29
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




