package com.sensitive.wordfilter;

import com.sensitive.wordfilter.service.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 敏感词过滤系统启动类
 */
@Slf4j
@SpringBootApplication
@EnableCaching
public class SensitiveWordFilterApplication implements CommandLineRunner {
    
    @Autowired
    private SensitiveWordService sensitiveWordService;
    
    public static void main(String[] args) {
        SpringApplication.run(SensitiveWordFilterApplication.class, args);
        log.info("敏感词过滤系统启动成功！");
    }
    
    @Override
    public void run(String... args) throws Exception {
        // 应用启动时初始化敏感词字典
        log.info("正在初始化敏感词字典...");
        sensitiveWordService.reloadSensitiveWordDictionary();
        log.info("敏感词字典初始化完成！");
    }
} 