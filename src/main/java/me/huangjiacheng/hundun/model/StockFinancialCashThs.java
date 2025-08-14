package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 同花顺现金流量表数据实体
 */
@Data
public class StockFinancialCashThs {
    // 报告期
    private String reportPeriod;
    // 报表核心指标
    private String coreIndicator;
    // *现金及现金等价物净增加额
    private String netCashIncrease;
    // *经营活动产生的现金流量净额
    private String netCashFromOperating;
    // *投资活动产生的现金流量净额
    private String netCashFromInvesting;
    // *筹资活动产生的现金流量净额
    private String netCashFromFinancing;
    // *期末现金及现金等价物余额
    private String endingCash;
    // 报表全部指标
    private String allIndicator;
    // 一、经营活动产生的现金流量
    private String operatingCashFlow;
    // 销售商品、提供劳务收到的现金
    private String cashReceivedFromSales;
    // 收到的税费与返还
    private String taxRefund;
    // 收到其他与经营活动有关的现金
    private String otherOperatingCashIn;
    // 经营活动现金流入小计
    private String totalOperatingCashIn;
    // 购买商品、接受劳务支付的现金
    private String cashPaidForGoods;
    // 支付给职工以及为职工支付的现金
    private String cashPaidToEmployees;
    // 支付的各项税费
    private String taxesPaid;
    // 支付其他与经营活动有关的现金
    private String otherOperatingCashOut;
    // 经营活动现金流出小计
    private String totalOperatingCashOut;
    // 经营活动产生的现金流量净额
    private String netOperatingCash;
    // 二、投资活动产生的现金流量
    private String investingCashFlow;
    // 收回投资收到的现金
    private String cashReceivedFromInvestments;
    // 取得投资收益收到的现金
    private String investmentIncomeReceived;
    // 处置固定资产、无形资产和其他长期资产收回的现金净额
    private String cashFromAssetDisposal;
    // 处置子公司及其他营业单位收到的现金净额
    private String cashFromSubsidiaryDisposal;
    // 收到其他与投资活动有关的现金
    private String otherInvestingCashIn;
    // 投资活动现金流入小计
    private String totalInvestingCashIn;
    // 购建固定资产、无形资产和其他长期资产支付的现金
    private String cashPaidForAssets;
    // 投资支付的现金
    private String cashPaidForInvestments;
    // 取得子公司及其他营业单位支付的现金净额
    private String cashPaidForSubsidiary;
    // 支付其他与投资活动有关的现金
    private String otherInvestingCashOut;
    // 投资活动现金流出小计
    private String totalInvestingCashOut;
    // 投资活动产生的现金流量净额
    private String netInvestingCash;
    // 三、筹资活动产生的现金流量
    private String financingCashFlow;
    // 吸收投资收到的现金
    private String cashReceivedFromFinancing;
    // 其中：子公司吸收少数股东投资收到的现金
    private String cashReceivedFromMinorityFinancing;
    // 取得借款收到的现金
    private String cashReceivedFromBorrowings;
    // 收到其他与筹资活动有关的现金
    private String otherFinancingCashIn;
    // 筹资活动现金流入小计
    private String totalFinancingCashIn;
    // 偿还债务支付的现金
    private String cashPaidForDebt;
    // 分配股利、利润或偿付利息支付的现金
    private String cashPaidForDividends;
    // 其中：子公司支付给少数股东的股利、利润
    private String cashPaidToMinorityDividends;
    // 支付其他与筹资活动有关的现金
    private String otherFinancingCashOut;
    // 筹资活动现金流出小计
    private String totalFinancingCashOut;
    // 筹资活动产生的现金流量净额
    private String netFinancingCash;
    // 四、汇率变动对现金及现金等价物的影响
    private String fxEffectOnCash;
    // 五、现金及现金等价物净增加额
    private String netCashIncrease2;
    // 加：期初现金及现金等价物余额
    private String beginningCash;
    // 六、期末现金及现金等价物余额
    private String endingCash2;
    // 补充资料：
    private String supplement;
    // 1、将净利润调节为经营活动现金流量：
    private String profitToOperatingCash;
    // 净利润
    private String netProfit;
    // 加：资产减值准备
    private String assetImpairmentProvision;
    // 固定资产折旧、油气资产折耗、生产性生物资产折旧
    private String depreciation;
    // 无形资产摊销
    private String amortization;
    // 长期待摊费用摊销
    private String longTermDeferredExpenseAmortization;
    // 处置固定资产、无形资产和其他长期资产的损失
    private String assetDisposalLoss;
    // 固定资产报废损失
    private String fixedAssetScrapLoss;
    // 公允价值变动损失
    private String fairValueLoss;
    // 财务费用
    private String financialExpense2;
    // 投资损失
    private String investmentLoss;
    // 递延所得税资产减少
    private String deferredTaxAssetDecrease;
    // 递延所得税负债增加
    private String deferredTaxLiabilityIncrease;
    // 存货的减少
    private String inventoryDecrease;
    // 经营性应收项目的减少
    private String receivableDecrease;
    // 经营性应付项目的增加
    private String payableIncrease;
    // 其他
    private String other;
    // 间接法-经营活动产生的现金流量净额
    private String indirectNetOperatingCash;
    // 2、不涉及现金收支的重大投资和筹资活动：
    private String nonCashInvestingFinancing;
    // 3、现金及现金等价物净变动情况：
    private String cashChange;
    // 现金的期末余额
    private String endingCash3;
    // 减：现金的期初余额
    private String beginningCash2;
    // 加：现金等价物的期末余额
    private String endingCashEquivalent;
    // 间接法-现金及现金等价物净增加额
    private String indirectNetCashIncrease;
} 