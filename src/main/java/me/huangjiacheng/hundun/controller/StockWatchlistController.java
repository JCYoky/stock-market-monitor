package me.huangjiacheng.hundun.controller;

import me.huangjiacheng.hundun.model.StockWatchlist;
import me.huangjiacheng.hundun.service.StockWatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自选股管理控制器
 */
@RestController
@RequestMapping("/api/watchlist")
public class StockWatchlistController {

    @Autowired
    private StockWatchlistService stockWatchlistService;

    /**
     * 添加或更新自选股
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStock(@RequestBody StockWatchlist stockWatchlist) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证必填字段
            if (stockWatchlist.getStockCode() == null || stockWatchlist.getStockCode().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "股票代码不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (stockWatchlist.getStockName() == null || stockWatchlist.getStockName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "股票名称不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (stockWatchlist.getStockType() == null || (stockWatchlist.getStockType() != 1 && stockWatchlist.getStockType() != 2)) {
                response.put("success", false);
                response.put("message", "股票类型必须为1(观察股)或2(持仓股)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查是否已存在
            StockWatchlist existingStock = stockWatchlistService.getStockByCode(stockWatchlist.getStockCode());
            if (existingStock != null) {
                // 更新现有记录：保留原有财务数据，只更新类型
                stockWatchlist.setId(existingStock.getId());
                stockWatchlist.setPeTtm(existingStock.getPeTtm());
                stockWatchlist.setRoe(existingStock.getRoe());
                stockWatchlist.setProfitQuality(existingStock.getProfitQuality());
                stockWatchlist.setAssetsQuality(existingStock.getAssetsQuality());
                stockWatchlist.setPeScore(existingStock.getPeScore());
                stockWatchlist.setCreatedTime(existingStock.getCreatedTime());
                stockWatchlist.setUpdatedTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                
                boolean updated = stockWatchlistService.updateStock(stockWatchlist);
                if (updated) {
                    response.put("success", true);
                    response.put("message", "自选股更新成功");
                    response.put("action", "updated");
                } else {
                    response.put("success", false);
                    response.put("message", "自选股更新失败");
                }
            } else {
                // 添加新记录
                boolean added = stockWatchlistService.addStock(stockWatchlist);
                if (added) {
                    response.put("success", true);
                    response.put("message", "自选股添加成功");
                    response.put("action", "added");
                } else {
                    response.put("success", false);
                    response.put("message", "自选股添加失败");
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 删除自选股
     */
    @DeleteMapping("/delete/{stockCode}")
    public ResponseEntity<Map<String, Object>> deleteStock(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = stockWatchlistService.deleteStock(stockCode);
            if (deleted) {
                response.put("success", true);
                response.put("message", "自选股删除成功");
            } else {
                response.put("success", false);
                response.put("message", "自选股删除失败，可能该股票不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 将自选股改为系统股票（type改为0）
     */
    @PutMapping("/remove-from-watchlist/{stockCode}")
    public ResponseEntity<Map<String, Object>> removeFromWatchlist(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean updated = stockWatchlistService.removeFromWatchlist(stockCode);
            if (updated) {
                response.put("success", true);
                response.put("message", "已从自选股中移除");
            } else {
                response.put("success", false);
                response.put("message", "移除失败，可能该股票不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "移除失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 更新自选股信息
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateStock(@RequestBody StockWatchlist stockWatchlist) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (stockWatchlist.getId() == null) {
                response.put("success", false);
                response.put("message", "ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean updated = stockWatchlistService.updateStock(stockWatchlist);
            if (updated) {
                response.put("success", true);
                response.put("message", "自选股更新成功");
            } else {
                response.put("success", false);
                response.put("message", "自选股更新失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 根据股票代码查询自选股
     */
    @GetMapping("/stock/{stockCode}")
    public ResponseEntity<Map<String, Object>> getStockByCode(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            StockWatchlist stock = stockWatchlistService.getStockByCode(stockCode);
            if (stock != null) {
                response.put("success", true);
                response.put("data", stock);
                response.put("message", "获取自选股成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "股票不在自选股中");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取所有自选股
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStocks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockWatchlist> stocks = stockWatchlistService.getAllStocks();
            response.put("success", true);
            response.put("data", stocks);
            response.put("message", "获取自选股成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取自选股失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 根据类型获取自选股
     */
    @GetMapping("/type/{stockType}")
    public ResponseEntity<Map<String, Object>> getStocksByType(@PathVariable Integer stockType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (stockType != 1 && stockType != 2) {
                response.put("success", false);
                response.put("message", "股票类型必须为1(观察股)或2(持仓股)");
                return ResponseEntity.badRequest().body(response);
            }
            List<StockWatchlist> stocks = stockWatchlistService.getStocksByType(stockType);
            response.put("success", true);
            response.put("data", stocks);
            response.put("message", "获取自选股成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取自选股失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
