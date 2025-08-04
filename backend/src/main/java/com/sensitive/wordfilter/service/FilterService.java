package com.sensitive.wordfilter.service;

import com.sensitive.wordfilter.entity.FilterRecord;
import com.sensitive.wordfilter.entity.FilterResult;
import java.util.List;

/**
 * 过滤服务接口
 */
public interface FilterService {
    
    /**
     * 过滤文本
     */
    FilterResult filterText(String text, String ipAddress, String userAgent);
    
    /**
     * 检测文本是否包含敏感词
     */
    boolean containsSensitiveWord(String text);
    
    /**
     * 获取文本中的敏感词列表
     */
    java.util.Set<String> getSensitiveWords(String text);
    
    /**
     * 分页查询过滤记录
     */
    List<FilterRecord> getFilterRecords(Integer page, Integer size);
    
    /**
     * 获取过滤记录总数
     */
    Long getFilterRecordCount();
    
    /**
     * 根据IP地址查询过滤记录
     */
    List<FilterRecord> getFilterRecordsByIp(String ipAddress);
    
    /**
     * 根据时间范围查询过滤记录
     */
    List<FilterRecord> getFilterRecordsByTimeRange(String startTime, String endTime);
    
    /**
     * 清理历史记录
     */
    boolean cleanHistoryRecords(String beforeTime);
} 