package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 同花顺利润表数据实体
 */
@Data
public class StockFinancialBenefitThs {
    // 报告期
    private String reportPeriod;
    // 报表核心指标
    private String coreIndicator;
    // *净利润
    private String netProfit;
    // *营业总收入
    private String totalRevenue;
    // *营业总成本
    private String totalCost;
    // *归属于母公司所有者的净利润
    private String parentNetProfit;
    // *扣除非经常性损益后的净利润
    private String netProfitExclExtra;
    // 报表全部指标
    private String allIndicator;
    // 一、营业总收入
    private String revenue1;
    // 其中：营业收入
    private String revenue;
    // 二、营业总成本
    private String cost2;
    // 其中：营业成本
    private String cost;
    // 营业税金及附加
    private String taxAndSurcharge;
    // 销售费用
    private String sellingExpense;
    // 管理费用
    private String adminExpense;
    // 研发费用
    private String rAndDExpense;
    // 财务费用
    private String financialExpense;
    // 其中：利息费用
    private String interestExpense;
    // 利息收入
    private String interestIncome;
    // 资产减值损失
    private String assetImpairmentLoss;
    // 信用减值损失
    private String creditImpairmentLoss;
    // 加：公允价值变动收益
    private String fairValueIncome;
    // 投资收益
    private String investmentIncome;
    // 其中：联营企业和合营企业的投资收益
    private String jointInvestmentIncome;
    // 资产处置收益
    private String assetDisposalIncome;
    // 其他收益
    private String otherIncome;
    // 三、营业利润
    private String operatingProfit;
    // 加：营业外收入
    private String nonOperatingIncome;
    // 其中：非流动资产处置利得
    private String nonCurrentAssetDisposalGain;
    // 减：营业外支出
    private String nonOperatingExpense;
    // 其中：非流动资产处置损失
    private String nonCurrentAssetDisposalLoss;
    // 四、利润总额
    private String totalProfit;
    // 减：所得税费用
    private String incomeTaxExpense;
    // 五、净利润
    private String netProfit2;
    // （一）持续经营净利润
    private String continuedNetProfit;
    // 归属于母公司所有者的净利润
    private String parentNetProfit2;
    // 少数股东损益
    private String minorityProfit;
    // 扣除非经常性损益后的净利润
    private String netProfitExclExtra2;
    // 六、每股收益
    private String eps6;
    // （一）基本每股收益
    private String basicEps;
    // （二）稀释每股收益
    private String dilutedEps;
    // 七、其他综合收益
    private String otherComprehensiveIncome;
    // 归属母公司所有者的其他综合收益
    private String parentOtherComprehensiveIncome;
    // 八、综合收益总额
    private String totalComprehensiveIncome;
    // 归属于母公司股东的综合收益总额
    private String parentTotalComprehensiveIncome;
    // 归属于少数股东的综合收益总额
    private String minorityTotalComprehensiveIncome;
} 