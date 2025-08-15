package me.huangjiacheng.hundun.service;

import me.huangjiacheng.hundun.model.StockWatchlist;

import java.util.List;

/**
 * 自选股服务接口
 */
public interface StockWatchlistService {
    
    /**
     * 添加自选股
     */
    boolean addStock(StockWatchlist stockWatchlist);
    
    /**
     * 删除自选股
     */
    boolean deleteStock(String stockCode);
    
    /**
     * 更新自选股信息
     */
    boolean updateStock(StockWatchlist stockWatchlist);
    
    /**
     * 根据股票代码查询自选股
     */
    StockWatchlist getStockByCode(String stockCode);
    
    /**
     * 查询所有自选股
     */
    List<StockWatchlist> getAllStocks();
    
    /**
     * 根据股票类型查询自选股
     */
    List<StockWatchlist> getStocksByType(Integer stockType);
}
