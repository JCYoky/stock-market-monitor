package me.huangjiacheng.hundun.service;

import me.huangjiacheng.hundun.mapper.StockWatchlistMapper;
import me.huangjiacheng.hundun.model.StockWatchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 持仓分析服务类
 */
@Service
public class HoldingsAnalysisService {

    @Autowired
    private StockWatchlistMapper stockWatchlistMapper;
    
    @Autowired
    private AKShareService akShareService;

    /**
     * 获取持仓分析数据
     */
    public Map<String, Object> getHoldingsAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取所有持仓股（type=2）
            List<StockWatchlist> holdings = stockWatchlistMapper.selectByStockType(2);
            
            if (holdings == null || holdings.isEmpty()) {
                result.put("success", true);
                result.put("totalHoldings", 0);
                result.put("totalMarketValue", BigDecimal.ZERO);
                result.put("holdings", new ArrayList<>());
                return result;
            }
            
            // 计算总市值
            BigDecimal totalMarketValue = BigDecimal.ZERO;
            List<Map<String, Object>> holdingsList = new ArrayList<>();
            
            for (StockWatchlist holding : holdings) {
                // 获取股票最新价格
                Double currentPrice = akShareService.getStockLatestPrice(holding.getStockCode());
                if (currentPrice == null || currentPrice <= 0) {
                    currentPrice = 0.0;
                }
                
                // 计算持仓市值
                BigDecimal marketValue = BigDecimal.ZERO;
                if (holding.getHoldingShares() != null && holding.getHoldingShares() > 0) {
                    BigDecimal shares = new BigDecimal(holding.getHoldingShares());
                    BigDecimal price = new BigDecimal(currentPrice.toString());
                    marketValue = shares.multiply(price);
                }
                
                totalMarketValue = totalMarketValue.add(marketValue);
                
                // 构建持仓数据（只保留四个字段）
                Map<String, Object> holdingData = new HashMap<>();
                holdingData.put("stockCode", holding.getStockCode());
                holdingData.put("stockName", holding.getStockName());
                holdingData.put("quantity", holding.getHoldingShares() != null ? holding.getHoldingShares() : 0);
                holdingData.put("currentPrice", currentPrice);
                holdingData.put("marketValue", marketValue);
                
                holdingsList.add(holdingData);
            }
            
            // 计算持仓比例
            for (Map<String, Object> holdingData : holdingsList) {
                BigDecimal marketValue = (BigDecimal) holdingData.get("marketValue");
                BigDecimal weight = BigDecimal.ZERO;
                if (totalMarketValue.compareTo(BigDecimal.ZERO) > 0) {
                    weight = marketValue.divide(totalMarketValue, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));
                }
                holdingData.put("weight", weight);
            }
            
            result.put("success", true);
            result.put("totalHoldings", holdings.size());
            result.put("totalMarketValue", totalMarketValue);
            result.put("holdings", holdingsList);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取持仓分析数据失败: " + e.getMessage());
        }
        
        return result;
    }
}
