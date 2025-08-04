package com.sensitive.wordfilter.entity;

import lombok.Data;
import java.util.List;

/**
 * 敏感词过滤结果实体类
 */
@Data
public class FilterResult {
    
    /**
     * 原始文本
     */
    private String originalText;
    
    /**
     * 过滤后文本
     */
    private String filteredText;
    
    /**
     * 是否包含敏感词
     */
    private Boolean hasSensitiveWord;
    
    /**
     * 检测到的敏感词列表
     */
    private List<String> sensitiveWords;
    
    /**
     * 敏感词数量
     */
    private Integer sensitiveWordCount;
    
    /**
     * 过滤时间（毫秒）
     */
    private Long filterTime;
    
    public FilterResult() {
        this.hasSensitiveWord = false;
        this.sensitiveWordCount = 0;
        this.filterTime = 0L;
    }
} 