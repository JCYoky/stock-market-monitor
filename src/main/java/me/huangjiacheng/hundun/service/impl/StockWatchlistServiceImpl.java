package me.huangjiacheng.hundun.service.impl;

import me.huangjiacheng.hundun.mapper.StockWatchlistMapper;
import me.huangjiacheng.hundun.model.StockWatchlist;
import me.huangjiacheng.hundun.service.StockWatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            // 设置时间
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stockWatchlist.setCreatedTime(currentTime);
            stockWatchlist.setUpdatedTime(currentTime);
            
            // 设置默认值
            if (stockWatchlist.getPeTtm() == null) stockWatchlist.setPeTtm(0.0);
            if (stockWatchlist.getRoe() == null) stockWatchlist.setRoe(0.0);
            if (stockWatchlist.getProfitQuality() == null) stockWatchlist.setProfitQuality(0.0);
            if (stockWatchlist.getAssetsQuality() == null) stockWatchlist.setAssetsQuality(0.0);
            if (stockWatchlist.getPeScore() == null) stockWatchlist.setPeScore(0.0);
            
            // 先检查股票是否已存在
            StockWatchlist existingStock = stockWatchlistMapper.selectByStockCode(stockWatchlist.getStockCode());
            if (existingStock != null) {
                // 如果已存在，则更新
                stockWatchlist.setId(existingStock.getId());
                stockWatchlist.setCreatedTime(existingStock.getCreatedTime()); // 保持原创建时间
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
            // 设置更新时间
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stockWatchlist.setUpdatedTime(currentTime);
            
            // 设置默认值
            if (stockWatchlist.getPeTtm() == null) stockWatchlist.setPeTtm(0.0);
            if (stockWatchlist.getRoe() == null) stockWatchlist.setRoe(0.0);
            if (stockWatchlist.getProfitQuality() == null) stockWatchlist.setProfitQuality(0.0);
            if (stockWatchlist.getAssetsQuality() == null) stockWatchlist.setAssetsQuality(0.0);
            if (stockWatchlist.getPeScore() == null) stockWatchlist.setPeScore(0.0);
            
            return stockWatchlistMapper.update(stockWatchlist) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public StockWatchlist getStockByCode(String stockCode) {
        try {
            return stockWatchlistMapper.selectByStockCode(stockCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<StockWatchlist> getAllStocks() {
        try {
            List<StockWatchlist> stocks = stockWatchlistMapper.selectAll();
            if (stocks != null && !stocks.isEmpty()) {
                            // 排序：按市盈率得分从大到小排序
            stocks.sort((s1, s2) -> {
                Double score1 = s1.getPeScore() != null ? s1.getPeScore() : 0.0;
                Double score2 = s2.getPeScore() != null ? s2.getPeScore() : 0.0;
                return score2.compareTo(score1);
            });
            }
            return stocks;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<StockWatchlist> getStocksByType(Integer stockType) {
        try {
            return stockWatchlistMapper.selectByStockType(stockType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean removeFromWatchlist(String stockCode) {
        try {
            // 获取现有股票信息
            StockWatchlist existingStock = stockWatchlistMapper.selectByStockCode(stockCode);
            if (existingStock != null) {
                // 将股票类型改为0（系统股票）
                existingStock.setStockType(0);
                // 设置更新时间
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                existingStock.setUpdatedTime(currentTime);
                return stockWatchlistMapper.update(existingStock) > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
