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
                // 更新现有记录
                stockWatchlist.setId(existingStock.getId());
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
    public ResponseEntity<StockWatchlist> getStockByCode(@PathVariable String stockCode) {
        try {
            StockWatchlist stock = stockWatchlistService.getStockByCode(stockCode);
            if (stock != null) {
                return ResponseEntity.ok(stock);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取所有自选股
     */
    @GetMapping("/all")
    public ResponseEntity<List<StockWatchlist>> getAllStocks() {
        try {
            List<StockWatchlist> stocks = stockWatchlistService.getAllStocks();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据类型获取自选股
     */
    @GetMapping("/type/{stockType}")
    public ResponseEntity<List<StockWatchlist>> getStocksByType(@PathVariable Integer stockType) {
        try {
            if (stockType != 1 && stockType != 2) {
                return ResponseEntity.badRequest().build();
            }
            List<StockWatchlist> stocks = stockWatchlistService.getStocksByType(stockType);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
