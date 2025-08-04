package com.sensitive.wordfilter.controller;

import com.sensitive.wordfilter.entity.FilterRecord;
import com.sensitive.wordfilter.entity.FilterResult;
import com.sensitive.wordfilter.entity.Result;
import com.sensitive.wordfilter.service.FilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词过滤控制器
 */
@Slf4j
@RestController
@RequestMapping("/filter")
@CrossOrigin(origins = "*")
public class FilterController {
    
    @Autowired
    private FilterService filterService;
    
    /**
     * 过滤文本
     */
    @PostMapping("/text")
    public Result<FilterResult> filterText(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            String text = request.get("text");
            if (text == null || text.trim().isEmpty()) {
                return Result.error("文本内容不能为空");
            }
            
            // 获取客户端IP地址
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            FilterResult result = filterService.filterText(text, ipAddress, userAgent);
            return Result.success("文本过滤完成", result);
        } catch (Exception e) {
            log.error("文本过滤失败: {}", e.getMessage(), e);
            return Result.error("文本过滤失败");
        }
    }
    
    /**
     * 检测文本是否包含敏感词
     */
    @PostMapping("/check")
    public Result<Map<String, Object>> checkText(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            if (text == null || text.trim().isEmpty()) {
                return Result.error("文本内容不能为空");
            }
            
            boolean containsSensitiveWord = filterService.containsSensitiveWord(text);
            Set<String> sensitiveWords = filterService.getSensitiveWords(text);
            
            Map<String, Object> result = Map.of(
                "containsSensitiveWord", containsSensitiveWord,
                "sensitiveWords", sensitiveWords,
                "sensitiveWordCount", sensitiveWords.size()
            );
            
            return Result.success("检测完成", result);
        } catch (Exception e) {
            log.error("文本检测失败: {}", e.getMessage(), e);
            return Result.error("文本检测失败");
        }
    }
    
    /**
     * 分页查询过滤记录
     */
    @GetMapping("/records")
    public Result<Object> getFilterRecords(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<FilterRecord> records = filterService.getFilterRecords(page, size);
            Long total = filterService.getFilterRecordCount();
            
            return Result.success("查询过滤记录成功", Map.of(
                "list", records,
                "total", total,
                "page", page,
                "size", size
            ));
        } catch (Exception e) {
            log.error("查询过滤记录失败: {}", e.getMessage(), e);
            return Result.error("查询过滤记录失败");
        }
    }
    
    /**
     * 根据IP地址查询过滤记录
     */
    @GetMapping("/records/ip/{ipAddress}")
    public Result<List<FilterRecord>> getFilterRecordsByIp(@PathVariable String ipAddress) {
        try {
            List<FilterRecord> records = filterService.getFilterRecordsByIp(ipAddress);
            return Result.success("查询IP过滤记录成功", records);
        } catch (Exception e) {
            log.error("查询IP过滤记录失败: {}", e.getMessage(), e);
            return Result.error("查询IP过滤记录失败");
        }
    }
    
    /**
     * 根据时间范围查询过滤记录
     */
    @GetMapping("/records/time")
    public Result<List<FilterRecord>> getFilterRecordsByTimeRange(@RequestParam String startTime,
                                                                @RequestParam String endTime) {
        try {
            List<FilterRecord> records = filterService.getFilterRecordsByTimeRange(startTime, endTime);
            return Result.success("查询时间范围过滤记录成功", records);
        } catch (Exception e) {
            log.error("查询时间范围过滤记录失败: {}", e.getMessage(), e);
            return Result.error("查询时间范围过滤记录失败");
        }
    }
    
    /**
     * 清理历史记录
     */
    @DeleteMapping("/records/clean")
    public Result<Object> cleanHistoryRecords(@RequestParam String beforeTime) {
        try {
            boolean success = filterService.cleanHistoryRecords(beforeTime);
            if (success) {
                return Result.success("清理历史记录成功");
            } else {
                return Result.error("清理历史记录失败");
            }
        } catch (Exception e) {
            log.error("清理历史记录失败: {}", e.getMessage(), e);
            return Result.error("清理历史记录失败");
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 