package com.dex.youqu.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dex.youqu.model.domain.User;
import com.dex.youqu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(3L);

    //每天执行
    @Scheduled(cron = "0 07 21 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("youqu:precachejob:docache:lock");
        try {
            //只有一个线程可以获取到锁
            if (lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)){
                System.out.printf("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("youqu:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    //写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 60000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("reids set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }
}
