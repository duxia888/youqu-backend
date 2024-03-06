package com.dex.youqu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@MapperScan("com.dex.youqu.mapper")
@EnableScheduling
public class YuoQuApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuoQuApplication.class, args);
    }

}
