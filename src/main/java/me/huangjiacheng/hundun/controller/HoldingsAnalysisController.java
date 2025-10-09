package me.huangjiacheng.hundun.controller;

import me.huangjiacheng.hundun.service.HoldingsAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 持仓分析控制器
 */
@RestController
@RequestMapping("/api/holdings")
public class HoldingsAnalysisController {

    @Autowired
    private HoldingsAnalysisService holdingsAnalysisService;

    /**
     * 获取持仓分析数据
     */
    @GetMapping("/analysis")
    public ResponseEntity<Map<String, Object>> getHoldingsAnalysis() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> analysisResult = holdingsAnalysisService.getHoldingsAnalysis();
            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取持仓分析失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
