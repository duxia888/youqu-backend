package com.dex.youqu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dex.youqu.common.BaseResponse;
import com.dex.youqu.common.ErrorCode;
import com.dex.youqu.common.ResultUtils;
import com.dex.youqu.exception.BusinessException;
import com.dex.youqu.model.domain.Team;
import com.dex.youqu.model.domain.User;
import com.dex.youqu.model.dto.TeamQuery;
import com.dex.youqu.model.request.TeamAddRequest;
import com.dex.youqu.service.TeamService;
import com.dex.youqu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 队伍接口
 *
 * @Author axin
 * Date:2024/2/27 11:23
 * Version 1.0
 * Description:
 */

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = { "http://localhost:5173" })
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> delTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean result = teamService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }

        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean result = teamService.updateById(team);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败");
        }

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<Team>> listTeams(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team, teamQuery);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }
}
