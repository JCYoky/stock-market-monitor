package me.huangjiacheng.hundun.service;

import me.huangjiacheng.hundun.model.StockFinancialDebtThs;
import me.huangjiacheng.hundun.model.StockFinancialBenefitThs;
import me.huangjiacheng.hundun.model.StockFinancialCashThs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 股票财务数据分析服务
 */
@Service
public class StockFinancialDataService {

    @Autowired
    private AKShareService akShareService;

    /**
     * 获取指定股票的三张财务报表数据，并按报告期整合
     * @param symbol 股票代码
     * @return 按报告期整合的财务报表数据
     */
    public Map<String, Object> getStockFinancialDataByPeriod(String symbol) {
        Map<String, Object> result = new HashMap<>();
        result.put("symbol", symbol);
        
        try {
            // 获取资产负债表数据
            List<StockFinancialDebtThs> debtData = akShareService.getStockFinancialDebtThs(symbol);
            // 获取利润表数据
            List<StockFinancialBenefitThs> benefitData = akShareService.getStockFinancialBenefitThs(symbol);
            // 获取现金流量表数据
            List<StockFinancialCashThs> cashData = akShareService.getStockFinancialCashThs(symbol);
            
            // 按报告期整合数据
            Map<String, Map<String, Object>> periodData = new TreeMap<>(Collections.reverseOrder());
            
            // 处理资产负债表数据
            if (debtData != null) {
                for (StockFinancialDebtThs debt : debtData) {
                    String reportPeriod = debt.getReportPeriod();
                    if (reportPeriod != null && !reportPeriod.trim().isEmpty()) {
                        periodData.computeIfAbsent(reportPeriod, k -> new HashMap<>()).put("balanceSheet", debt);
                    }
                }
            }
            
            // 处理利润表数据
            if (benefitData != null) {
                for (StockFinancialBenefitThs benefit : benefitData) {
                    String reportPeriod = benefit.getReportPeriod();
                    if (reportPeriod != null && !reportPeriod.trim().isEmpty()) {
                        periodData.computeIfAbsent(reportPeriod, k -> new HashMap<>()).put("incomeStatement", benefit);
                    }
                }
            }
            
            // 处理现金流量表数据
            if (cashData != null) {
                for (StockFinancialCashThs cash : cashData) {
                    String reportPeriod = cash.getReportPeriod();
                    if (reportPeriod != null && !reportPeriod.trim().isEmpty()) {
                        periodData.computeIfAbsent(reportPeriod, k -> new HashMap<>()).put("cashFlowStatement", cash);
                    }
                }
            }
            
            result.put("periodData", periodData);
            result.put("success", true);
            result.put("message", "数据获取成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "数据获取失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
} 