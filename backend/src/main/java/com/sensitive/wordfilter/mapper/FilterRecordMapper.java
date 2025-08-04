package com.sensitive.wordfilter.mapper;

import com.sensitive.wordfilter.entity.FilterRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 过滤记录Mapper接口
 */
@Mapper
public interface FilterRecordMapper {
    
    /**
     * 插入过滤记录
     */
    int insert(FilterRecord record);
    
    /**
     * 分页查询过滤记录
     */
    List<FilterRecord> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 查询过滤记录总数
     */
    Long selectCount();
    
    /**
     * 根据IP地址查询过滤记录
     */
    List<FilterRecord> selectByIpAddress(@Param("ipAddress") String ipAddress);
    
    /**
     * 根据时间范围查询过滤记录
     */
    List<FilterRecord> selectByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    /**
     * 删除指定时间之前的记录
     */
    int deleteByTimeBefore(@Param("time") String time);
} 