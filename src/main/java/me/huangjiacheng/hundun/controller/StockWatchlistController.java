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
 * 自选股控制器
 */
@RestController
@RequestMapping("/api/watchlist")
public class StockWatchlistController {
    
    @Autowired
    private StockWatchlistService stockWatchlistService;
    
    /**
     * 添加自选股
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStock(@RequestBody StockWatchlist stockWatchlist) {
        System.out.println("接收到的数据: " + stockWatchlist);
        
        // 验证数据
        if (stockWatchlist.getStockCode() == null || stockWatchlist.getStockCode().trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "股票代码不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (stockWatchlist.getStockName() == null || stockWatchlist.getStockName().trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "股票名称不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (stockWatchlist.getStockType() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "股票类型不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean success = stockWatchlistService.addStock(stockWatchlist);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            // 检查是新增还是更新
            StockWatchlist existingStock = stockWatchlistService.getStockByCode(stockWatchlist.getStockCode());
            if (existingStock != null && existingStock.getStockType().equals(stockWatchlist.getStockType())) {
                response.put("success", true);
                response.put("message", "已存在，无需重复添加");
            } else {
                response.put("success", true);
                response.put("message", existingStock != null ? "更新成功" : "添加成功");
            }
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "操作失败");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除自选股
     */
    @DeleteMapping("/delete/{stockCode}")
    public ResponseEntity<Map<String, Object>> deleteStock(@PathVariable String stockCode) {
        boolean success = stockWatchlistService.deleteStock(stockCode);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "删除失败");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新自选股信息
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateStock(@RequestBody StockWatchlist stockWatchlist) {
        boolean success = stockWatchlistService.updateStock(stockWatchlist);
        if (success) {
            return ResponseEntity.ok("更新成功");
        } else {
            return ResponseEntity.badRequest().body("更新失败");
        }
    }
    
    /**
     * 根据股票代码查询自选股
     */
    @GetMapping("/stock/{stockCode}")
    public ResponseEntity<StockWatchlist> getStockByCode(@PathVariable String stockCode) {
        StockWatchlist stock = stockWatchlistService.getStockByCode(stockCode);
        if (stock != null) {
            return ResponseEntity.ok(stock);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 查询所有自选股
     */
    @GetMapping("/all")
    public ResponseEntity<List<StockWatchlist>> getAllStocks() {
        List<StockWatchlist> stocks = stockWatchlistService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }
    
    /**
     * 根据股票类型查询自选股
     */
    @GetMapping("/type/{stockType}")
    public ResponseEntity<List<StockWatchlist>> getStocksByType(@PathVariable Integer stockType) {
        List<StockWatchlist> stocks = stockWatchlistService.getStocksByType(stockType);
        return ResponseEntity.ok(stocks);
    }
}
