package com.sensitive.wordfilter.mapper;

import com.sensitive.wordfilter.entity.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 敏感词Mapper接口
 */
@Mapper
public interface SensitiveWordMapper {
    
    /**
     * 查询所有启用的敏感词
     */
    List<SensitiveWord> selectAllEnabled();
    
    /**
     * 根据分类查询敏感词
     */
    List<SensitiveWord> selectByCategory(@Param("category") String category);
    
    /**
     * 根据级别查询敏感词
     */
    List<SensitiveWord> selectByLevel(@Param("level") Integer level);
    
    /**
     * 分页查询敏感词
     */
    List<SensitiveWord> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 查询敏感词总数
     */
    Long selectCount();
    
    /**
     * 根据ID查询敏感词
     */
    SensitiveWord selectById(@Param("id") Long id);
    
    /**
     * 根据敏感词查询
     */
    SensitiveWord selectByWord(@Param("word") String word);
    
    /**
     * 插入敏感词
     */
    int insert(SensitiveWord sensitiveWord);
    
    /**
     * 更新敏感词
     */
    int update(SensitiveWord sensitiveWord);
    
    /**
     * 删除敏感词
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量插入敏感词
     */
    int batchInsert(@Param("words") List<SensitiveWord> words);
    
    /**
     * 根据分类删除敏感词
     */
    int deleteByCategory(@Param("category") String category);
} 