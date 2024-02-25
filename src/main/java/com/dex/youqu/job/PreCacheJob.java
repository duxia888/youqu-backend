package com.dex.youqu.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dex.youqu.model.domain.User;
import com.dex.youqu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 * @Author axin
 * Date:2024/2/25 23:00
 * <p>
 * Version 1.0
 * Description:
 */

@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserService userService;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(3L);

    //每天执行
    @Scheduled(cron = "0 20 23 * * *")
    public void doCacheRecommendUser() {
        for (Long userId : mainUserList) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
            String redisKey = String.format("youqu:user:recommend:%s", userId);
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //写缓存
            try {
                valueOperations.set(redisKey, userPage, 60000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("reids set key error",e);
            }
        }

    }
}
