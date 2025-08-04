package com.sensitive.wordfilter.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 敏感词过滤记录实体类
 */
@Data
public class FilterRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 原始文本
     */
    private String originalText;
    
    /**
     * 过滤后文本
     */
    private String filteredText;
    
    /**
     * 检测到的敏感词（JSON格式）
     */
    private String sensitiveWords;
    
    /**
     * 过滤次数
     */
    private Integer filterCount;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 