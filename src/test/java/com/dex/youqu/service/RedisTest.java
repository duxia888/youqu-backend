package com.dex.youqu.service;

import com.dex.youqu.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @Author axin
 * Date:2024/2/25 21:00
 * <p>
 * Version 1.0
 * Description:
 */

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("duxiaStr", "duxia");
        valueOperations.set("duxiaInt", 1);
        valueOperations.set("duxiaDouble", 2.0);
        User user = new User();
        user.setId(0L);
        user.setUsername("duxia");
        valueOperations.set("duxiaUser", user);
        // 查
        Object duxia = valueOperations.get("duxiaStr");
        Assertions.assertTrue("duxia".equals((String) duxia));
        duxia = valueOperations.get("duxiaInt");
        Assertions.assertTrue( 1 == ((Integer) duxia));
        duxia = valueOperations.get("duxiaDouble");
        Assertions.assertTrue( 2.0 ==  ((Double) duxia));
        duxia = valueOperations.get("duxiaUser");
        System.out.println(duxia);
        redisTemplate.delete("duxiaStr");
    }
}
