package me.huangjiacheng.hundun.controller;

import me.huangjiacheng.hundun.service.BatchAnalysisService;
import me.huangjiacheng.hundun.service.PersistenceService;
import me.huangjiacheng.hundun.model.Evaluation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 * 批量分析API控制器
 * 用于触发批量评估方法和持久化数据
 */
@RestController
@RequestMapping("/api/batch")
public class BatchAnalysisApiController {
    
    @Autowired
    private BatchAnalysisService batchAnalysisService;
    
    @Autowired
    private PersistenceService persistenceService;
    
    /**
     * 触发批量评估并持久化数据
     * @return 处理结果
     */
    @PostMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> triggerBatchEvaluation() {
        try {
            // 执行批量评估
            Map<String, List<Evaluation>> yearlyEvaluations = batchAnalysisService.batchEvaluate();
            
            // 测试年份排序和数据对应关系
            persistenceService.testYearSorting(yearlyEvaluations);
            
            // 持久化数据
            boolean persistSuccess = persistenceService.persistentBatchEvaluation(yearlyEvaluations);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", persistSuccess);
            result.put("message", persistSuccess ? "批量评估完成" : "持久化失败");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}
