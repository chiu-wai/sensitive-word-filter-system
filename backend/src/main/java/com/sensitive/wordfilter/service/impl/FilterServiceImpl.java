package com.sensitive.wordfilter.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sensitive.wordfilter.entity.FilterRecord;
import com.sensitive.wordfilter.entity.FilterResult;
import com.sensitive.wordfilter.mapper.FilterRecordMapper;
import com.sensitive.wordfilter.service.FilterService;
import com.sensitive.wordfilter.util.SensitiveWordFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 过滤服务实现类
 */
@Slf4j
@Service
public class FilterServiceImpl implements FilterService {
    
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    
    @Autowired
    private FilterRecordMapper filterRecordMapper;
    
    @Override
    public FilterResult filterText(String text, String ipAddress, String userAgent) {
        long startTime = System.currentTimeMillis();
        
        FilterResult result = new FilterResult();
        result.setOriginalText(text);
        
        // 过滤敏感词
        String filteredText = sensitiveWordFilter.filter(text);
        result.setFilteredText(filteredText);
        
        // 获取检测到的敏感词
        Set<String> sensitiveWords = sensitiveWordFilter.getSensitiveWords(text);
        result.setSensitiveWords(sensitiveWords.stream().toList());
        result.setSensitiveWordCount(sensitiveWords.size());
        result.setHasSensitiveWord(!sensitiveWords.isEmpty());
        
        // 计算过滤时间
        long endTime = System.currentTimeMillis();
        result.setFilterTime(endTime - startTime);
        
        // 记录过滤日志
        if (!sensitiveWords.isEmpty()) {
            saveFilterRecord(text, filteredText, sensitiveWords, ipAddress, userAgent);
        }
        
        log.info("文本过滤完成，原始长度: {}, 过滤后长度: {}, 敏感词数量: {}, 耗时: {}ms", 
                text.length(), filteredText.length(), sensitiveWords.size(), result.getFilterTime());
        
        return result;
    }
    
    @Override
    public boolean containsSensitiveWord(String text) {
        return sensitiveWordFilter.containsSensitiveWord(text);
    }
    
    @Override
    public Set<String> getSensitiveWords(String text) {
        return sensitiveWordFilter.getSensitiveWords(text);
    }
    
    @Override
    public List<FilterRecord> getFilterRecords(Integer page, Integer size) {
        int offset = (page - 1) * size;
        return filterRecordMapper.selectByPage(offset, size);
    }
    
    @Override
    public Long getFilterRecordCount() {
        return filterRecordMapper.selectCount();
    }
    
    @Override
    public List<FilterRecord> getFilterRecordsByIp(String ipAddress) {
        return filterRecordMapper.selectByIpAddress(ipAddress);
    }
    
    @Override
    public List<FilterRecord> getFilterRecordsByTimeRange(String startTime, String endTime) {
        return filterRecordMapper.selectByTimeRange(startTime, endTime);
    }
    
    @Override
    @Transactional
    public boolean cleanHistoryRecords(String beforeTime) {
        try {
            int result = filterRecordMapper.deleteByTimeBefore(beforeTime);
            log.info("清理历史记录成功，删除 {} 条记录", result);
            return true;
        } catch (Exception e) {
            log.error("清理历史记录失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 保存过滤记录
     */
    private void saveFilterRecord(String originalText, String filteredText, Set<String> sensitiveWords, 
                                String ipAddress, String userAgent) {
        try {
            FilterRecord record = new FilterRecord();
            record.setOriginalText(originalText);
            record.setFilteredText(filteredText);
            record.setSensitiveWords(JSON.toJSONString(sensitiveWords));
            record.setFilterCount(sensitiveWords.size());
            record.setIpAddress(ipAddress);
            record.setUserAgent(userAgent);
            
            filterRecordMapper.insert(record);
            log.debug("过滤记录保存成功，ID: {}", record.getId());
        } catch (Exception e) {
            log.error("保存过滤记录失败: {}", e.getMessage(), e);
        }
    }
} 