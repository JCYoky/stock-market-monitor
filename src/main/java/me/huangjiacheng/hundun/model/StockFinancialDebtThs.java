package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 同花顺资产负债表数据实体
 */
@Data
public class StockFinancialDebtThs {
    // 报告期
    private String reportPeriod;
    // 报表核心指标
    private String coreIndicator;
    // *所有者权益（或股东权益）合计
    private String equityTotal;
    // *资产合计
    private String assetTotal;
    // *负债合计
    private String debtTotal;
    // *归属于母公司所有者权益合计
    private String parentEquityTotal;
    // 报表全部指标
    private String allIndicator;

    //=================================================资产=================================================
    // 流动资产
    private String currentAssets;
    // 货币资金
    private String cash;
    // 拆出资金
    private String lendingFunds;
    // 交易性金融资产
    private String tradingFinancialAssets;
    // 应收票据及应收账款
    private String receivables;
    // 其中：应收票据
    private String receivableNotes;
    // 应收账款
    private String receivableAccounts;
    // 预付款项
    private String prepayments;
    // 其他应收款合计
    private String otherReceivablesTotal;
    // 其中：应收利息
    private String receivableInterest;
    // 其他应收款
    private String otherReceivables;
    // 存货
    private String inventory;
    // 一年内到期的非流动资产
    private String nonCurrentAssetsDueWithinOneYear;
    // 其他流动资产
    private String otherCurrentAssets;
    // 总现金
    private String totalCash;
    // 流动资产合计
    private String currentAssetsTotal;

    //=================================================非流动资产=================================================
    // 非流动资产
    private String nonCurrentAssets;
    // 可供出售金融资产
    private String availableForSaleFinancialAssets;
    // 持有至到期投资
    private String heldToMaturityInvestments;
    // 长期股权投资
    private String longTermEquityInvestments;
    // 其他权益工具投资
    private String otherEquityInvestments;
    // 其他非流动金融资产
    private String otherNonCurrentFinancialAssets;
    // 投资性房地产
    private String investmentProperty;
    // 固定资产合计
    private String fixedAssetsTotal;
    // 其中：固定资产
    private String fixedAssets;
    // 固定资产清理
    private String fixedAssetsCleanup;
    // 在建工程合计
    private String constructionInProgressTotal;
    // 其中：在建工程
    private String constructionInProgress;
    // 工程物资
    private String projectMaterials;
    // 无形资产
    private String intangibleAssets;
    // 商誉
    private String goodwill;
    // 长期待摊费用
    private String longTermDeferredExpenses;
    // 递延所得税资产
    private String deferredIncomeTaxAssets;
    // 其他非流动资产
    private String otherNonCurrentAssets;
    // 非流动资产合计
    private String nonCurrentAssetsTotal;
    // 资产合计
    private String totalAssets;

    //=================================================负债=================================================
    // 流动负债
    private String currentLiabilities;
    // 短期借款-金融性负债
    private String shortTermLoans;
    // 以公允价值计量且其变动计入当期损益的金融负债
    private String fairValueFinancialLiabilities;
    // 衍生金融负债
    private String derivativeFinancialLiabilities;
    // 应付票据及应付账款-经营性负债
    private String payables;
    // 其中：应付票据
    private String payableNotes;
    // 应付账款
    private String payableAccounts;
    // 预收款项-经营性负债
    private String advanceReceipts;
    // 合同负债-经营性负债
    private String contractLiabilities;
    // 应付职工薪酬-经营性负债
    private String payableStaffSalaries;
    // 应交税费-经营性负债
    private String taxesPayable;
    // 其他应付款合计
    private String otherPayablesTotal;
    // 其中：应付利息
    private String payableInterest;
    // 应付股利
    private String payableDividends;
    // 其他应付款
    private String otherPayables;
    // 一年内到期的非流动负债-金融性负债
    private String nonCurrentLiabilitiesDueWithinOneYear;
    // 其他流动负债
    private String otherCurrentLiabilities;
    // 流动负债合计
    private String currentLiabilitiesTotal;

    //=================================================非流动负债=================================================
    // 非流动负债
    private String nonCurrentLiabilities;
    // 长期借款-金融性负债
    private String longTermLoans;
    // 长期应付款合计-金融性负债
    private String longTermPayablesTotal;
    // 其中：长期应付款
    private String longTermPayables;
    // 专项应付款
    private String specialPayables;
    // 预计负债
    private String estimatedLiabilities;
    // 递延所得税负债
    private String deferredIncomeTaxLiabilities;
    // 递延收益-非流动负债
    private String deferredIncomeNonCurrentLiabilities;
    // 其他非流动负债
    private String otherNonCurrentLiabilities;
    // 非流动负债合计
    private String nonCurrentLiabilitiesTotal;
    // 负债合计
    private String totalLiabilities;

    //=================================================所有者权益=================================================
    // 所有者权益（或股东权益）
    private String equity;
    // 实收资本（或股本）
    private String paidInCapital;
    // 资本公积
    private String capitalReserve;
    // 减：库存股
    private String treasuryStock;
    // 其他综合收益
    private String otherComprehensiveIncome;
    // 盈余公积
    private String surplusReserve;
    // 未分配利润
    private String undistributedProfits;
    // 归属于母公司所有者权益合计
    private String parentEquity;
    // 少数股东权益
    private String minorityEquity;
    // 所有者权益（或股东权益）合计
    private String equityTotal2;
    // 负债和所有者权益（或股东权益）合计
    private String totalLiabilitiesAndEquity;
} 