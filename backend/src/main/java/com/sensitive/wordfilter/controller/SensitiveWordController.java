package com.sensitive.wordfilter.controller;

import com.sensitive.wordfilter.entity.Result;
import com.sensitive.wordfilter.entity.SensitiveWord;
import com.sensitive.wordfilter.service.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 敏感词管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/sensitive-words")
@CrossOrigin(origins = "*")
public class SensitiveWordController {
    
    @Autowired
    private SensitiveWordService sensitiveWordService;
    
    /**
     * 获取所有启用的敏感词
     */
    @GetMapping("/enabled")
    public Result<List<SensitiveWord>> getAllEnabledWords() {
        try {
            List<SensitiveWord> words = sensitiveWordService.getAllEnabledWords();
            return Result.success("获取敏感词列表成功", words);
        } catch (Exception e) {
            log.error("获取敏感词列表失败: {}", e.getMessage(), e);
            return Result.error("获取敏感词列表失败");
        }
    }
    
    /**
     * 分页查询敏感词
     */
    @GetMapping("/page")
    public Result<Object> getWordsByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<SensitiveWord> words = sensitiveWordService.getWordsByPage(page, size);
            Long total = sensitiveWordService.getWordCount();
            
            return Result.success("分页查询成功", Map.of(
                "list", words,
                "total", total,
                "page", page,
                "size", size
            ));
        } catch (Exception e) {
            log.error("分页查询敏感词失败: {}", e.getMessage(), e);
            return Result.error("分页查询失败");
        }
    }
    
    /**
     * 根据ID获取敏感词
     */
    @GetMapping("/{id}")
    public Result<SensitiveWord> getWordById(@PathVariable Long id) {
        try {
            SensitiveWord word = sensitiveWordService.getWordById(id);
            if (word == null) {
                return Result.error("敏感词不存在");
            }
            return Result.success("获取敏感词成功", word);
        } catch (Exception e) {
            log.error("获取敏感词失败: {}", e.getMessage(), e);
            return Result.error("获取敏感词失败");
        }
    }
    
    /**
     * 添加敏感词
     */
    @PostMapping
    public Result<Object> addWord(@RequestBody SensitiveWord word) {
        try {
            if (word.getWord() == null || word.getWord().trim().isEmpty()) {
                return Result.error("敏感词不能为空");
            }
            
            boolean success = sensitiveWordService.addWord(word);
            if (success) {
                return Result.success("添加敏感词成功");
            } else {
                return Result.error("添加敏感词失败，可能已存在");
            }
        } catch (Exception e) {
            log.error("添加敏感词失败: {}", e.getMessage(), e);
            return Result.error("添加敏感词失败");
        }
    }
    
    /**
     * 更新敏感词
     */
    @PutMapping("/{id}")
    public Result<Object> updateWord(@PathVariable Long id, @RequestBody SensitiveWord word) {
        try {
            word.setId(id);
            boolean success = sensitiveWordService.updateWord(word);
            if (success) {
                return Result.success("更新敏感词成功");
            } else {
                return Result.error("更新敏感词失败");
            }
        } catch (Exception e) {
            log.error("更新敏感词失败: {}", e.getMessage(), e);
            return Result.error("更新敏感词失败");
        }
    }
    
    /**
     * 删除敏感词
     */
    @DeleteMapping("/{id}")
    public Result<Object> deleteWord(@PathVariable Long id) {
        try {
            boolean success = sensitiveWordService.deleteWord(id);
            if (success) {
                return Result.success("删除敏感词成功");
            } else {
                return Result.error("删除敏感词失败");
            }
        } catch (Exception e) {
            log.error("删除敏感词失败: {}", e.getMessage(), e);
            return Result.error("删除敏感词失败");
        }
    }
    
    /**
     * 批量添加敏感词
     */
    @PostMapping("/batch")
    public Result<Object> batchAddWords(@RequestBody List<SensitiveWord> words) {
        try {
            if (words == null || words.isEmpty()) {
                return Result.error("敏感词列表不能为空");
            }
            
            boolean success = sensitiveWordService.batchAddWords(words);
            if (success) {
                return Result.success("批量添加敏感词成功");
            } else {
                return Result.error("批量添加敏感词失败");
            }
        } catch (Exception e) {
            log.error("批量添加敏感词失败: {}", e.getMessage(), e);
            return Result.error("批量添加敏感词失败");
        }
    }
    
    /**
     * 根据分类获取敏感词
     */
    @GetMapping("/category/{category}")
    public Result<List<SensitiveWord>> getWordsByCategory(@PathVariable String category) {
        try {
            List<SensitiveWord> words = sensitiveWordService.getWordsByCategory(category);
            return Result.success("获取分类敏感词成功", words);
        } catch (Exception e) {
            log.error("获取分类敏感词失败: {}", e.getMessage(), e);
            return Result.error("获取分类敏感词失败");
        }
    }
    
    /**
     * 根据级别获取敏感词
     */
    @GetMapping("/level/{level}")
    public Result<List<SensitiveWord>> getWordsByLevel(@PathVariable Integer level) {
        try {
            List<SensitiveWord> words = sensitiveWordService.getWordsByLevel(level);
            return Result.success("获取级别敏感词成功", words);
        } catch (Exception e) {
            log.error("获取级别敏感词失败: {}", e.getMessage(), e);
            return Result.error("获取级别敏感词失败");
        }
    }
    
    /**
     * 重新加载敏感词字典
     */
    @PostMapping("/reload")
    public Result<Object> reloadDictionary() {
        try {
            sensitiveWordService.reloadSensitiveWordDictionary();
            return Result.success("重新加载敏感词字典成功");
        } catch (Exception e) {
            log.error("重新加载敏感词字典失败: {}", e.getMessage(), e);
            return Result.error("重新加载敏感词字典失败");
        }
    }
} 