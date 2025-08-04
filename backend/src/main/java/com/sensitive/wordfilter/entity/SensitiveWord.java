package com.sensitive.wordfilter.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 敏感词实体类
 */
@Data
public class SensitiveWord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 敏感词
     */
    private String word;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 敏感级别：1-低，2-中，3-高
     */
    private Integer level;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 