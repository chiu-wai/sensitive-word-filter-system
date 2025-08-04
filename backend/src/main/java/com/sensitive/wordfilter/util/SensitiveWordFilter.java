package com.sensitive.wordfilter.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 敏感词过滤工具类
 * 使用DFA（确定有限自动机）算法实现高效的敏感词检测
 */
@Slf4j
@Component
public class SensitiveWordFilter {
    
    /**
     * 敏感词字典树根节点
     */
    private Map<Character, Object> sensitiveWordMap;
    
    /**
     * 替换字符
     */
    private static final String REPLACE_CHAR = "*";
    
    /**
     * 初始化敏感词字典树
     */
    public void initSensitiveWordMap(Set<String> sensitiveWords) {
        sensitiveWordMap = new HashMap<>();
        for (String word : sensitiveWords) {
            if (word == null || word.trim().isEmpty()) {
                continue;
            }
            addWord(word.trim());
        }
        log.info("敏感词字典树初始化完成，共加载 {} 个敏感词", sensitiveWords.size());
    }
    
    /**
     * 向字典树中添加敏感词
     */
    private void addWord(String word) {
        Map<Character, Object> currentMap = sensitiveWordMap;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Map<Character, Object> subMap = (Map<Character, Object>) currentMap.get(c);
            
            if (subMap == null) {
                subMap = new HashMap<>();
                currentMap.put(c, subMap);
            }
            
            currentMap = subMap;
            
            // 如果是最后一个字符，标记为敏感词结束
            if (i == word.length() - 1) {
                currentMap.put('\0', null);
            }
        }
    }
    
    /**
     * 过滤敏感词
     */
    public String filter(String text) {
        if (text == null || text.isEmpty() || sensitiveWordMap == null) {
            return text;
        }
        
        StringBuilder result = new StringBuilder(text);
        Set<String> foundWords = new HashSet<>();
        
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                String sensitiveWord = text.substring(i, i + length);
                foundWords.add(sensitiveWord);
                
                // 替换敏感词
                for (int j = 0; j < length; j++) {
                    result.setCharAt(i + j, REPLACE_CHAR.charAt(0));
                }
                i += length - 1; // 跳过已处理的字符
            }
        }
        
        if (!foundWords.isEmpty()) {
            log.info("检测到敏感词: {}", foundWords);
        }
        
        return result.toString();
    }
    
    /**
     * 检查从指定位置开始的敏感词
     */
    private int checkSensitiveWord(String text, int beginIndex) {
        Map<Character, Object> currentMap = sensitiveWordMap;
        int wordLength = 0;
        
        for (int i = beginIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            currentMap = (Map<Character, Object>) currentMap.get(c);
            
            if (currentMap == null) {
                break;
            }
            
            wordLength++;
            
            // 检查是否到达敏感词结尾
            if (currentMap.containsKey('\0')) {
                return wordLength;
            }
        }
        
        return 0;
    }
    
    /**
     * 检测文本中是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty() || sensitiveWordMap == null) {
            return false;
        }
        
        for (int i = 0; i < text.length(); i++) {
            if (checkSensitiveWord(text, i) > 0) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取文本中的所有敏感词
     */
    public Set<String> getSensitiveWords(String text) {
        Set<String> sensitiveWords = new HashSet<>();
        
        if (text == null || text.isEmpty() || sensitiveWordMap == null) {
            return sensitiveWords;
        }
        
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                sensitiveWords.add(text.substring(i, i + length));
                i += length - 1;
            }
        }
        
        return sensitiveWords;
    }
    
    /**
     * 获取敏感词数量
     */
    public int getSensitiveWordCount(String text) {
        return getSensitiveWords(text).size();
    }
    
    /**
     * 重新加载敏感词字典
     */
    public void reloadSensitiveWords(Set<String> sensitiveWords) {
        initSensitiveWordMap(sensitiveWords);
    }
    
    /**
     * 添加单个敏感词
     */
    public void addSensitiveWord(String word) {
        if (sensitiveWordMap == null) {
            sensitiveWordMap = new HashMap<>();
        }
        addWord(word);
    }
    
    /**
     * 删除敏感词（从字典树中移除）
     */
    public void removeSensitiveWord(String word) {
        if (sensitiveWordMap == null || word == null || word.isEmpty()) {
            return;
        }
        
        // 简化实现：重新构建字典树
        // 在实际应用中，可以实现更复杂的删除逻辑
        log.warn("删除敏感词功能暂未实现，请重新加载敏感词列表");
    }
} 