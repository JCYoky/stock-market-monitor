package me.huangjiacheng.hundun.model;

import lombok.Data;

@Data
public class StockFinancialHkIncomeStatement {
    // 报告期
    private String reportPeriod;
    // 股票代码
    private String stockCode;
    // 股票名称
    private String stockName;
    
    // 营业额/营业收入
    private String revenue;
    // 其他营业收入
    private String otherRevenue;
    // 营业总收入
    private String totalRevenue;
    // 利息收入
    private String interestIncome;
    // 销售成本
    private String costOfSales;
    // 毛利
    private String grossProfit;
    // 销售及分销费用
    private String sellingAndDistributionExpenses;
    // 行政费用
    private String administrativeExpenses;
    // 研发费用
    private String researchAndDevelopmentExpenses;
    // 其他营业费用
    private String otherOperatingExpenses;
    // 营业总成本
    private String totalOperatingExpenses;
    // 减值及拨备
    private String impairmentAndProvisions;
    // 折旧与摊销
    private String depreciationAndAmortization;
    // 营业利润
    private String operatingProfit;
    // 财务收入
    private String financialIncome;
    // 财务费用
    private String financialExpenses;
    // 财务成本
    private String financialCosts;
    // 其他收入
    private String otherIncome;
    // 其他支出
    private String otherExpenses;
    // 溢利其他项目
    private String profitOtherItems;
    // 重估盈余
    private String revaluationSurplus;
    // 非运算项目
    private String nonOperatingItems;
    // 除税前溢利
    private String profitBeforeTax;
    // 应占合营公司溢利
    private String shareOfJointVentureProfit;
    // 应占联营公司溢利
    private String shareOfAssociateProfit;
    // 所得税费用
    private String incomeTaxExpense;
    // 年内溢利/净利润
    private String profitForTheYear;
    // 归属于母公司股东的溢利
    private String profitAttributableToParent;
    // 少数股东损益
    private String minorityInterests;
    // 终止或非持续业务溢利
    private String discontinuedOperationsProfit;
    // 基本每股收益
    private String basicEarningsPerShare;
    // 稀释每股收益
    private String dilutedEarningsPerShare;
    // 每股股息
    private String dividendPerShare;
    // 综合收益总额
    private String totalComprehensiveIncome;
    // 归属于母公司股东的综合收益
    private String comprehensiveIncomeAttributableToParent;
} 