package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 港股现金流量表数据实体类（全字段版）
 */
@Data
public class StockFinancialHkCashFlow {
    // 报告期
    private String reportPeriod;
    // 股票代码
    private String stockCode;
    // 股票名称
    private String stockName;

    // ========== 经营活动现金流量 ==========
    // 除税前溢利(业务利润)
    private String profitBeforeTax;
    // 减:利息收入
    private String interestIncome;
    // 减:投资收益
    private String investmentIncome;
    // 减:应占附属公司溢利
    private String shareOfSubsidiaryProfit;
    // 加:减值及拨备
    private String impairmentAndProvisions;
    // 减:出售资产之溢利
    private String gainOnDisposalOfAssets;
    // 加:折旧及摊销
    private String depreciationAndAmortization;
    // 减:汇兑收益
    private String exchangeGain;
    // 加:经营调整其他项目
    private String otherOperatingAdjustments;
    // 营运资金变动前经营溢利
    private String operatingProfitBeforeWorkingCapital;
    
    // 存货(增加)减少
    private String inventoryChange;
    // 应收帐款减少
    private String accountsReceivableDecrease;
    // 应付帐款及应计费用增加(减少)
    private String accountsPayableAndAccrualsChange;
    // 营运资本变动其他项目
    private String otherWorkingCapitalChanges;
    // 预付款项、按金及其他应收款项减少(增加)
    private String prepaymentsAndOtherReceivablesChange;
    // 预收账款、按金及其他应付款增加(减少)
    private String advanceReceiptsAndOtherPayablesChange;
    // 递延收入(增加)减少
    private String deferredRevenueChange;
    // 存款(增加)减少
    private String depositsChange;
    // 经营产生现金
    private String operatingCashGenerated;
    // 已付税项
    private String taxPaid;
    // 经营业务现金净额
    private String netOperatingCashFlow;
    
    // ========== 投资活动现金流量 ==========
    // 已收利息(投资)
    private String interestReceived;
    // 已收股息(投资)
    private String dividendsReceived;
    // 存款减少(增加)
    private String depositsDecrease;
    // 处置固定资产
    private String disposalOfFixedAssets;
    // 购建固定资产
    private String purchaseOfFixedAssets;
    // 购建无形资产及其他资产
    private String purchaseOfIntangibleAssets;
    // 出售附属公司
    private String disposalOfSubsidiaries;
    // 收购附属公司
    private String acquisitionOfSubsidiaries;
    // 收回投资所得现金
    private String proceedsFromInvestment;
    // 投资支付现金
    private String investmentPayments;
    // 应收关联方款项(增加)减少(投资)
    private String relatedPartyReceivablesChange;
    // 投资业务其他项目
    private String otherInvestingActivities;
    // 投资业务现金净额
    private String netInvestingCashFlow;
    // 融资前现金净额
    private String netCashBeforeFinancing;
    
    // ========== 筹资活动现金流量 ==========
    // 新增借款
    private String newBorrowings;
    // 偿还借款
    private String repaymentOfBorrowings;
    // 已付股息(融资)
    private String dividendsPaid;
    // 吸收投资所得
    private String proceedsFromInvestmentReceived;
    // 发行股份
    private String proceedsFromIssueOfShares;
    // 发行相关费用
    private String shareIssueExpenses;
    // 回购股份
    private String repurchaseOfShares;
    // 发行债券
    private String proceedsFromIssueOfBonds;
    // 购买子公司少数股权而支付的现金
    private String purchaseOfMinorityInterests;
    // 融资业务其他项目
    private String otherFinancingActivities;
    // 融资业务现金净额
    private String netFinancingCashFlow;
    
    // ========== 现金净额及期末余额 ==========
    // 现金净额
    private String netCashFlow;
    // 期初现金
    private String cashAtBeginningOfPeriod;
    // 期间变动其他项目
    private String otherPeriodChanges;
    // 期末现金
    private String cashAtEndOfPeriod;
    
    // ========== 其他字段 ==========
    // 非运算项目
    private String nonOperatingItems;
    
    // ========== 兼容性字段（保持原有字段名） ==========
    // 调整项目
    private String adjustments;
    // 期初现金及现金等价物
    private String cashAndCashEquivalentsAtBeginningOfPeriod;
    // 期末现金及现金等价物
    private String cashAndCashEquivalentsAtEndOfPeriod;
    // 所得税支出
    private String incomeTaxPaid;
    // 利息支出
    private String interestExpense;
    // 筹资活动产生的现金流量净额
    private String netCashFromFinancingActivities;
    // 投资活动产生的现金流量净额
    private String netCashFromInvestingActivities;
    // 经营活动产生的现金流量净额
    private String netCashFromOperatingActivities;
    // 现金及现金等价物净增加额
    private String netIncreaseInCashAndCashEquivalents;
    // 非现金投资及筹资活动
    private String nonCashInvestingAndFinancingActivities;
    // 营运资金变动前的经营现金流量
    private String operatingCashFlowBeforeWorkingCapital;
    // 支付股息
    private String paymentOfDividends;
    // 取得借款
    private String proceedsFromBorrowings;
    // 出售无形资产
    private String proceedsFromSaleOfIntangibleAssets;
    // 出售投资物业
    private String proceedsFromSaleOfInvestmentProperty;
    // 出售其他投资
    private String proceedsFromSaleOfOtherInvestments;
    // 出售物业、厂房及设备
    private String proceedsFromSaleOfPropertyPlantAndEquipment;
    // 购买投资物业
    private String purchaseOfInvestmentProperty;
    // 购买其他投资
    private String purchaseOfOtherInvestments;
    // 购买物业、厂房及设备
    private String purchaseOfPropertyPlantAndEquipment;
    // 偿还债券
    private String repaymentOfBonds;
    // 营运资金变动
    private String workingCapitalChanges;
} 