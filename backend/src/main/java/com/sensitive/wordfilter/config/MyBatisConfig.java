package com.sensitive.wordfilter.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan("com.sensitive.wordfilter.mapper")
public class MyBatisConfig {
    // MyBatis配置已在application.yml中完成
} 