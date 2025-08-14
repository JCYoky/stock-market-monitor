package me.huangjiacheng.hundun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import me.huangjiacheng.hundun.model.Evaluation;
import me.huangjiacheng.hundun.model.StockInfo;

/**
 * 批处理分析服务
 * 用于处理批量财务数据分析任务
 */
@Service
public class BatchAnalysisService {
    @Autowired
    private FinancialAnalysisService financialAnalysisService;
    @Autowired
    private AKShareService akShareService;

    /**
     * 批量评估股票
     * @return 年份和Evaluation列表的映射关系
     */
    public Map<String, List<Evaluation>> batchEvaluate() {
        try {
            System.out.println("开始批量评估股票...");
            List<StockInfo> stockList = akShareService.getStockList();
            System.out.println("获取到 " + stockList.size() + " 只股票进行分析");
            
            Map<String, List<Evaluation>> result = new HashMap<>();
            
            for (StockInfo stock : stockList) {
                try {
                    System.out.println("正在分析股票: " + stock.getCode() + " - " + stock.getName());
                    Map<String, Object> financialAnalysisData = financialAnalysisService.analyzeFinancialStructure(stock.getCode());
                    if (financialAnalysisData != null) {
                        List<Evaluation> analysisResult = analysisSingle(financialAnalysisData, stock.getName());
                        System.out.println("股票 " + stock.getCode() + " 分析完成，生成 " + analysisResult.size() + " 个评估结果");
                        
                        for (Evaluation evaluation : analysisResult) {
                            String year = evaluation.getYear();
                            if (year != null) {
                                result.computeIfAbsent(year, k -> new ArrayList<>()).add(evaluation);
                            }
                        }
                    } else {
                        System.out.println("股票 " + stock.getCode() + " 财务分析数据为空");
                    }
                } catch (Exception e) {
                    // 记录单个股票分析失败，继续处理其他股票
                    System.err.println("分析股票 " + stock.getCode() + " 失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("批量评估完成，共生成 " + result.size() + " 个年份的评估结果");
            return result;
        } catch (Exception e) {
            System.err.println("批量评估失败: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

        /**
     * 分析单个财务结构数据
     * @param financialData analyzeFinancialStructure方法的返回值
     * @return 股票按年度评分列表
     */
    public List<Evaluation> analysisSingle(Map<String, Object> financialData, String stockName) {
        List<Evaluation> evaluations = new ArrayList<>();
        
        if (financialData == null || !(Boolean) financialData.get("success")) {
            System.out.println("股票 " + stockName + " 财务数据无效或分析失败");
            return evaluations;
        }
        
        String symbol = (String) financialData.get("symbol");
        System.out.println("开始分析股票 " + stockName + " (" + symbol + ") 的财务数据");
        
        // 获取各个分析模块
        @SuppressWarnings("unchecked")
        Map<String, Object> debtAnalysis = (Map<String, Object>) financialData.get("debtAnalysis");
        @SuppressWarnings("unchecked")
        Map<String, Object> assetAnalysis = (Map<String, Object>) financialData.get("assetAnalysis");
        @SuppressWarnings("unchecked")
        Map<String, Object> profitRatioAnalysis = (Map<String, Object>) financialData.get("profitRatioAnalysis");
        @SuppressWarnings("unchecked")
        Map<String, Object> financialRatioAnalysis = (Map<String, Object>) financialData.get("financialRatioAnalysis");
        @SuppressWarnings("unchecked")
        Map<String, Object> bloodRatioAnalysis = (Map<String, Object>) financialData.get("bloodRatioAnalysis");
        
        if (debtAnalysis == null || assetAnalysis == null || profitRatioAnalysis == null || 
            financialRatioAnalysis == null || bloodRatioAnalysis == null) {
            System.out.println("股票 " + stockName + " 缺少必要的分析模块数据");
            return evaluations;
        }
        
        // 获取年份数据
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> debtRatios = (Map<String, Map<String, Double>>) debtAnalysis.get("yearlyRatios");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> assetRatios = (Map<String, Map<String, Double>>) assetAnalysis.get("yearlyRatios");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> profitRatios = (Map<String, Map<String, Double>>) profitRatioAnalysis.get("yearlyRatios");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> financialRatios = (Map<String, Map<String, Double>>) financialRatioAnalysis.get("yearlyRatios");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> bloodRatios = (Map<String, Map<String, Double>>) bloodRatioAnalysis.get("yearlyRatios");
        
        if (debtRatios == null || debtRatios.isEmpty()) {
            System.out.println("股票 " + stockName + " 没有负债比率数据");
            return evaluations;
        }
        
        System.out.println("股票 " + stockName + " 共有 " + debtRatios.size() + " 个年份的数据");
        
        // 遍历每个年份进行评分
        for (String year : debtRatios.keySet()) {
            try {
                Map<String, Double> yearDebtRatios = debtRatios.get(year);
                Map<String, Double> yearAssetRatios = assetRatios.get(year);
                Map<String, Double> yearProfitRatios = profitRatios.get(year);
                Map<String, Double> yearFinancialRatios = financialRatios.get(year);
                Map<String, Double> yearBloodRatios = bloodRatios.get(year);
                
                if (yearDebtRatios == null || yearAssetRatios == null || yearProfitRatios == null) {
                    System.out.println("股票 " + stockName + " " + year + " 年数据不完整，跳过");
                    continue;
                }
                
                // 计算综合评分
                int score = calculateComprehensiveScore(
                    yearDebtRatios, yearAssetRatios, yearProfitRatios, 
                    yearFinancialRatios, yearBloodRatios
                );
                
                // 创建评估对象
                Evaluation evaluation = new Evaluation(year, symbol, stockName, score, 0.0, 0.0);
                evaluations.add(evaluation);
                System.out.println("股票 " + stockName + " " + year + " 年评分: " + score);
                
            } catch (Exception e) {
                System.err.println("处理股票 " + stockName + " " + year + " 年数据时出错: " + e.getMessage());
            }
        }
        
        System.out.println("股票 " + stockName + " 分析完成，共生成 " + evaluations.size() + " 个评估结果");
        return evaluations;
    }
    
    /**
     * 计算综合财务评分
     */
    private int calculateComprehensiveScore(
            Map<String, Double> debtRatios,
            Map<String, Double> assetRatios,
            Map<String, Double> profitRatios,
            Map<String, Double> financialRatios,
            Map<String, Double> bloodRatios) {
        
        int totalScore = 0;
        
        // 1. 负债结构评分 (25分)
        int debtScore = calculateDebtScore(debtRatios);
        totalScore += debtScore;
        
        // 2. 资产质量评分 (25分)
        int assetScore = calculateAssetScore(assetRatios);
        totalScore += assetScore;
        
        // 3. 盈利能力评分 (25分)
        int profitScore = calculateProfitScore(profitRatios);
        totalScore += profitScore;
        
        // 4. 财务健康度评分 (25分)
        int healthScore = calculateFinancialHealthScore(financialRatios, bloodRatios);
        totalScore += healthScore;
        
        return totalScore;
    }
    
    /**
     * 负债结构评分
     */
    private int calculateDebtScore(Map<String, Double> debtRatios) {
        if (debtRatios == null) return 0;
        
        int score = 0;
        
        // 经营性负债率评分
        Double operatingDebtRatio = debtRatios.get("operatingDebtRatio");     
        // 金融性负债率评分
        Double financialDebtRatio = debtRatios.get("financialDebtRatio");
        
        if (operatingDebtRatio != null && financialDebtRatio != null) {
            score += (int)((operatingDebtRatio - financialDebtRatio) * 100);
        }
        
        return score;
    }
    
    /**
     * 资产质量评分
     */
    private int calculateAssetScore(Map<String, Double> assetRatios) {
        if (assetRatios == null) return 0;
        
        int score = 0;
        // 净现金比率评分
        Double netCashRatio = assetRatios.get("净现金比率");
        // 商誉和无形资产占比评分
        Double goodwillRatio = assetRatios.get("商誉和无形资产占比");
        // 两应收一预付占比
        Double twoReceivableOnePrepaymentRatio = assetRatios.get("两应收一预付占比");
        
        if (netCashRatio != null && goodwillRatio != null && twoReceivableOnePrepaymentRatio != null) {
            score += (int)((netCashRatio - goodwillRatio - twoReceivableOnePrepaymentRatio) * 100);
        }
        
        return score;
    }
    
    /**
     * 盈利能力评分
     */
    private int calculateProfitScore(Map<String, Double> profitRatios) {
        if (profitRatios == null) return 0;
        
        int score = 0;
        
        // 毛利率评分
        Double grossProfitRatio = profitRatios.get("毛利率");
        // 核心利润率评分
        Double mainProfitRatio = profitRatios.get("核心利润率");
        // 财务费用占比评分
        Double financialExpenseRatio = profitRatios.get("财务费用占比");
        // 其他收益占比
        Double otherIncomeRatio = profitRatios.get("其他收益占比");
        
        if (grossProfitRatio != null && mainProfitRatio != null && 
            financialExpenseRatio != null && otherIncomeRatio != null) {
            score += (int)((grossProfitRatio + mainProfitRatio - financialExpenseRatio - otherIncomeRatio) * 100);
        }
        
        return score;
    }
    
    /**
     * 财务健康度评分
     */
    private int calculateFinancialHealthScore(Map<String, Double> financialRatios, Map<String, Double> bloodRatios) {
        if (financialRatios == null || bloodRatios == null) return 0;
        
        int score = 0;
        
        // 净资产收益率评分
        Double roe = financialRatios.get("净资产收益率");    
        // 造血能力占比评分
        Double earnedBloodRatio = bloodRatios.get("造血能力占比");
        // 输血能力占比评分
        Double bloodRatio = bloodRatios.get("输血能力占比");
        
        if (roe != null && earnedBloodRatio != null && bloodRatio != null) {
            score += (int)((roe + earnedBloodRatio - bloodRatio) * 100);
        }
        
        return score;
    }
    
}
