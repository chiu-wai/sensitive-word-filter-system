package com.sensitive.wordfilter.service.impl;

import com.sensitive.wordfilter.entity.SensitiveWord;
import com.sensitive.wordfilter.mapper.SensitiveWordMapper;
import com.sensitive.wordfilter.service.SensitiveWordService;
import com.sensitive.wordfilter.util.SensitiveWordFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 敏感词服务实现类
 */
@Slf4j
@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {
    
    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;
    
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    
    @Override
    @Cacheable(value = "sensitiveWords", key = "'allEnabled'")
    public List<SensitiveWord> getAllEnabledWords() {
        return sensitiveWordMapper.selectAllEnabled();
    }
    
    @Override
    public List<SensitiveWord> getWordsByPage(Integer page, Integer size) {
        int offset = (page - 1) * size;
        return sensitiveWordMapper.selectByPage(offset, size);
    }
    
    @Override
    public Long getWordCount() {
        return sensitiveWordMapper.selectCount();
    }
    
    @Override
    public SensitiveWord getWordById(Long id) {
        return sensitiveWordMapper.selectById(id);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "sensitiveWords", allEntries = true)
    public boolean addWord(SensitiveWord word) {
        try {
            // 检查敏感词是否已存在
            SensitiveWord existingWord = sensitiveWordMapper.selectByWord(word.getWord());
            if (existingWord != null) {
                log.warn("敏感词已存在: {}", word.getWord());
                return false;
            }
            
            // 设置默认值
            if (word.getCategory() == null) {
                word.setCategory("default");
            }
            if (word.getLevel() == null) {
                word.setLevel(1);
            }
            if (word.getStatus() == null) {
                word.setStatus(1);
            }
            
            int result = sensitiveWordMapper.insert(word);
            if (result > 0) {
                // 重新加载敏感词字典
                reloadSensitiveWordDictionary();
                log.info("添加敏感词成功: {}", word.getWord());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("添加敏感词失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "sensitiveWords", allEntries = true)
    public boolean updateWord(SensitiveWord word) {
        try {
            int result = sensitiveWordMapper.update(word);
            if (result > 0) {
                // 重新加载敏感词字典
                reloadSensitiveWordDictionary();
                log.info("更新敏感词成功: {}", word.getWord());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新敏感词失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "sensitiveWords", allEntries = true)
    public boolean deleteWord(Long id) {
        try {
            SensitiveWord word = sensitiveWordMapper.selectById(id);
            if (word == null) {
                log.warn("敏感词不存在，ID: {}", id);
                return false;
            }
            
            int result = sensitiveWordMapper.deleteById(id);
            if (result > 0) {
                // 重新加载敏感词字典
                reloadSensitiveWordDictionary();
                log.info("删除敏感词成功: {}", word.getWord());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除敏感词失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "sensitiveWords", allEntries = true)
    public boolean batchAddWords(List<SensitiveWord> words) {
        try {
            int result = sensitiveWordMapper.batchInsert(words);
            if (result > 0) {
                // 重新加载敏感词字典
                reloadSensitiveWordDictionary();
                log.info("批量添加敏感词成功，共 {} 个", words.size());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("批量添加敏感词失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<SensitiveWord> getWordsByCategory(String category) {
        return sensitiveWordMapper.selectByCategory(category);
    }
    
    @Override
    public List<SensitiveWord> getWordsByLevel(Integer level) {
        return sensitiveWordMapper.selectByLevel(level);
    }
    
    @Override
    public void reloadSensitiveWordDictionary() {
        try {
            List<SensitiveWord> words = sensitiveWordMapper.selectAllEnabled();
            Set<String> wordSet = new HashSet<>();
            for (SensitiveWord word : words) {
                wordSet.add(word.getWord());
            }
            sensitiveWordFilter.reloadSensitiveWords(wordSet);
            log.info("敏感词字典重新加载完成，共 {} 个敏感词", wordSet.size());
        } catch (Exception e) {
            log.error("重新加载敏感词字典失败: {}", e.getMessage(), e);
        }
    }
} 