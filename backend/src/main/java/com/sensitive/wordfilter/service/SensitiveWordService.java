package com.sensitive.wordfilter.service;

import com.sensitive.wordfilter.entity.SensitiveWord;
import java.util.List;

/**
 * 敏感词服务接口
 */
public interface SensitiveWordService {
    
    /**
     * 获取所有启用的敏感词
     */
    List<SensitiveWord> getAllEnabledWords();
    
    /**
     * 分页查询敏感词
     */
    List<SensitiveWord> getWordsByPage(Integer page, Integer size);
    
    /**
     * 获取敏感词总数
     */
    Long getWordCount();
    
    /**
     * 根据ID获取敏感词
     */
    SensitiveWord getWordById(Long id);
    
    /**
     * 添加敏感词
     */
    boolean addWord(SensitiveWord word);
    
    /**
     * 更新敏感词
     */
    boolean updateWord(SensitiveWord word);
    
    /**
     * 删除敏感词
     */
    boolean deleteWord(Long id);
    
    /**
     * 批量添加敏感词
     */
    boolean batchAddWords(List<SensitiveWord> words);
    
    /**
     * 根据分类获取敏感词
     */
    List<SensitiveWord> getWordsByCategory(String category);
    
    /**
     * 根据级别获取敏感词
     */
    List<SensitiveWord> getWordsByLevel(Integer level);
    
    /**
     * 重新加载敏感词字典
     */
    void reloadSensitiveWordDictionary();
} 