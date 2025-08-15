package me.huangjiacheng.hundun.service.impl;

import me.huangjiacheng.hundun.mapper.StockWatchlistMapper;
import me.huangjiacheng.hundun.model.StockWatchlist;
import me.huangjiacheng.hundun.service.StockWatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自选股服务实现类
 */
@Service
public class StockWatchlistServiceImpl implements StockWatchlistService {
    
    @Autowired
    private StockWatchlistMapper stockWatchlistMapper;
    
    @Override
    public boolean addStock(StockWatchlist stockWatchlist) {
        try {
            // 先检查股票是否已存在
            StockWatchlist existingStock = stockWatchlistMapper.selectByStockCode(stockWatchlist.getStockCode());
            if (existingStock != null) {
                // 如果已存在，则更新
                stockWatchlist.setId(existingStock.getId());
                return stockWatchlistMapper.update(stockWatchlist) > 0;
            } else {
                // 如果不存在，则插入
                return stockWatchlistMapper.insert(stockWatchlist) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteStock(String stockCode) {
        try {
            return stockWatchlistMapper.deleteByStockCode(stockCode) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateStock(StockWatchlist stockWatchlist) {
        try {
            return stockWatchlistMapper.update(stockWatchlist) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public StockWatchlist getStockByCode(String stockCode) {
        return stockWatchlistMapper.selectByStockCode(stockCode);
    }
    
    @Override
    public List<StockWatchlist> getAllStocks() {
        return stockWatchlistMapper.selectAll();
    }
    
    @Override
    public List<StockWatchlist> getStocksByType(Integer stockType) {
        return stockWatchlistMapper.selectByStockType(stockType);
    }
}
