package com.dex.usercenterbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * 配置类，配置分组，接口详情等
 *
 * @author axin
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev", "test"})
public class SwaggerConfiguration {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //扫描路径，获取controller层的接口
                .apis(RequestHandlerSelectors.basePackage("com.dex.usercenterbackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    /**
     * api信息
     *
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                //标题
                .title("游趣用户中心")
                //简介
                .description("游趣用户中心接口文档")
                .termsOfServiceUrl("https://github/staryellow.com/")
                //作者、网址http:localhost:8088/doc.html(这里注意端口号要与项目一致，如果你的端口号后面还加了前缀，就需要把前缀加上)、邮箱
                .contact(new Contact("axin","https://github.com/staryellow","xxx@qq.com"))
                //版本
                .version("1.0")
                .build();
    }

}

