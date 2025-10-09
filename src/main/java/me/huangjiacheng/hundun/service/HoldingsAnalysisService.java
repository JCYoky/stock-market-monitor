package me.huangjiacheng.hundun.service;

import me.huangjiacheng.hundun.mapper.StockWatchlistMapper;
import me.huangjiacheng.hundun.model.StockWatchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 持仓分析服务类
 * 用于分析用户的持仓情况，包括持仓概览、风险评估、收益分析等
 */
@Service
public class HoldingsAnalysisService {

    @Autowired
    private StockWatchlistMapper stockWatchlistMapper;

    /**
     * 获取持仓分析数据
     * @return 持仓分析结果
     */
    public Map<String, Object> getHoldingsAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取所有持仓股（type=2）
            List<StockWatchlist> holdings = stockWatchlistMapper.selectByStockType(2);
            
            if (holdings == null || holdings.isEmpty()) {
                result.put("success", true);
                result.put("totalHoldings", 0);
                result.put("totalMarketValue", 0);
                result.put("avgRoe", 0);
                result.put("avgExpectedReturn", 0);
                result.put("holdings", new ArrayList<>());
                return result;
            }
            
            // 计算持仓概览统计
            int totalHoldings = holdings.size();
            BigDecimal totalMarketValue = calculateTotalMarketValue(holdings);
            BigDecimal avgRoe = calculateAverageRoe(holdings);
            BigDecimal avgExpectedReturn = calculateAverageExpectedReturn(holdings);
            
            // 构建持仓详情列表
            List<Map<String, Object>> holdingsList = buildHoldingsList(holdings, totalMarketValue);
            
            result.put("success", true);
            result.put("totalHoldings", totalHoldings);
            result.put("totalMarketValue", totalMarketValue);
            result.put("avgRoe", avgRoe);
            result.put("avgExpectedReturn", avgExpectedReturn);
            result.put("holdings", holdingsList);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取持仓分析数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 计算总市值
     */
    private BigDecimal calculateTotalMarketValue(List<StockWatchlist> holdings) {
        // 由于StockWatchlist模型中没有currentPrice字段，这里使用模拟价格
        // 实际应用中应该从其他数据源获取实时价格
        return holdings.stream()
                .filter(holding -> holding.getHoldingShares() != null)
                .map(holding -> {
                    BigDecimal shares = new BigDecimal(holding.getHoldingShares());
                    // 使用模拟价格，实际应用中应该从价格服务获取
                    BigDecimal mockPrice = new BigDecimal("10.00"); // 模拟价格
                    return shares.multiply(mockPrice);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算平均ROE
     */
    private BigDecimal calculateAverageRoe(List<StockWatchlist> holdings) {
        List<BigDecimal> roeList = holdings.stream()
                .filter(holding -> holding.getRoe() != null)
                .map(holding -> new BigDecimal(holding.getRoe()))
                .collect(Collectors.toList());
        
        if (roeList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = roeList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(roeList.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算平均预期收益率
     */
    private BigDecimal calculateAverageExpectedReturn(List<StockWatchlist> holdings) {
        List<BigDecimal> returnList = holdings.stream()
                .filter(holding -> holding.getExpectedReturn() != null)
                .map(holding -> new BigDecimal(holding.getExpectedReturn()))
                .collect(Collectors.toList());
        
        if (returnList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = returnList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(returnList.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 构建持仓详情列表
     */
    private List<Map<String, Object>> buildHoldingsList(List<StockWatchlist> holdings, BigDecimal totalMarketValue) {
        return holdings.stream().map(holding -> {
            Map<String, Object> holdingData = new HashMap<>();
            
            holdingData.put("stockCode", holding.getStockCode());
            holdingData.put("stockName", holding.getStockName());
            holdingData.put("quantity", holding.getHoldingShares() != null ? holding.getHoldingShares() : 0);
            holdingData.put("currentPrice", 10.00); // 模拟价格
            
            // 计算市值（使用模拟价格）
            BigDecimal marketValue = BigDecimal.ZERO;
            if (holding.getHoldingShares() != null) {
                BigDecimal shares = new BigDecimal(holding.getHoldingShares());
                BigDecimal mockPrice = new BigDecimal("10.00"); // 模拟价格
                marketValue = shares.multiply(mockPrice);
            }
            holdingData.put("marketValue", marketValue);
            
            // 计算占比
            BigDecimal weight = BigDecimal.ZERO;
            if (totalMarketValue.compareTo(BigDecimal.ZERO) > 0) {
                weight = marketValue.divide(totalMarketValue, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }
            holdingData.put("weight", weight);
            
            // 其他指标
            holdingData.put("peTtm", holding.getPeTtm() != null ? holding.getPeTtm() : 0);
            holdingData.put("roe", holding.getRoe() != null ? holding.getRoe() : 0);
            holdingData.put("expectedReturn", holding.getExpectedReturn() != null ? holding.getExpectedReturn() : 0);
            holdingData.put("profitQuality", holding.getProfitQuality() != null ? holding.getProfitQuality() : 0);
            holdingData.put("assetsQuality", holding.getAssetsQuality() != null ? holding.getAssetsQuality() : 0);
            holdingData.put("peScore", holding.getPeScore() != null ? holding.getPeScore() : 0);
            holdingData.put("expectedGrowthRate", holding.getExpectedGrowthRate() != null ? holding.getExpectedGrowthRate() : 0);
            
            return holdingData;
        }).collect(Collectors.toList());
    }

    /**
     * 获取持仓风险评估
     * @return 风险评估结果
     */
    public Map<String, Object> getHoldingsRiskAssessment() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<StockWatchlist> holdings = stockWatchlistMapper.selectByStockType(2);
            
            if (holdings == null || holdings.isEmpty()) {
                result.put("success", true);
                result.put("riskLevel", "无持仓");
                result.put("riskScore", 0);
                result.put("recommendations", new ArrayList<>());
                return result;
            }
            
            // 计算风险评分
            int riskScore = calculateRiskScore(holdings);
            String riskLevel = determineRiskLevel(riskScore);
            
            // 生成风险建议
            List<String> recommendations = generateRiskRecommendations(holdings, riskScore);
            
            result.put("success", true);
            result.put("riskLevel", riskLevel);
            result.put("riskScore", riskScore);
            result.put("recommendations", recommendations);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取风险评估失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 计算风险评分
     */
    private int calculateRiskScore(List<StockWatchlist> holdings) {
        int score = 0;
        
        for (StockWatchlist holding : holdings) {
            // ROE评分
            if (holding.getRoe() != null) {
                double roe = holding.getRoe();
                if (roe < 5) score += 3;
                else if (roe < 10) score += 2;
                else if (roe < 15) score += 1;
            }
            
            // 市盈率评分
            if (holding.getPeTtm() != null) {
                double pe = holding.getPeTtm();
                if (pe > 50) score += 3;
                else if (pe > 30) score += 2;
                else if (pe > 20) score += 1;
            }
            
            // 利润质量评分
            if (holding.getProfitQuality() != null) {
                double quality = holding.getProfitQuality();
                if (quality < 0.5) score += 2;
                else if (quality < 0.8) score += 1;
            }
            
            // 资产质量评分
            if (holding.getAssetsQuality() != null) {
                double quality = holding.getAssetsQuality();
                if (quality < 30) score += 2;
                else if (quality < 60) score += 1;
            }
        }
        
        return Math.min(score, 20); // 最高20分
    }

    /**
     * 确定风险等级
     */
    private String determineRiskLevel(int riskScore) {
        if (riskScore <= 5) return "低风险";
        else if (riskScore <= 10) return "中等风险";
        else if (riskScore <= 15) return "高风险";
        else return "极高风险";
    }

    /**
     * 生成风险建议
     */
    private List<String> generateRiskRecommendations(List<StockWatchlist> holdings, int riskScore) {
        List<String> recommendations = new ArrayList<>();
        
        if (riskScore > 10) {
            recommendations.add("建议减少高风险股票持仓");
        }
        
        // 检查低ROE股票
        long lowRoeCount = holdings.stream()
                .filter(h -> h.getRoe() != null && h.getRoe() < 5)
                .count();
        if (lowRoeCount > 0) {
            recommendations.add("发现" + lowRoeCount + "只ROE低于5%的股票，建议关注");
        }
        
        // 检查高市盈率股票
        long highPeCount = holdings.stream()
                .filter(h -> h.getPeTtm() != null && h.getPeTtm() > 30)
                .count();
        if (highPeCount > 0) {
            recommendations.add("发现" + highPeCount + "只市盈率超过30的股票，注意估值风险");
        }
        
        return recommendations;
    }
}
