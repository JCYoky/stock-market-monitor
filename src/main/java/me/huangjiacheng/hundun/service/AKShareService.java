package me.huangjiacheng.hundun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.huangjiacheng.hundun.model.StockFinancialDebtThs;
import me.huangjiacheng.hundun.model.StockInfo;
import me.huangjiacheng.hundun.model.StockFinancialBenefitThs;
import me.huangjiacheng.hundun.model.StockFinancialCashThs;
import me.huangjiacheng.hundun.model.StockValuationData;
import me.huangjiacheng.hundun.model.StockTurnoverData;
import me.huangjiacheng.hundun.model.StockFinancialAbstractThs;
import me.huangjiacheng.hundun.model.StockFinancialHkBalanceSheet;
import me.huangjiacheng.hundun.model.StockFinancialHkIncomeStatement;
import me.huangjiacheng.hundun.model.StockFinancialHkCashFlow;
import me.huangjiacheng.hundun.model.StockHkBasicInfo;
import me.huangjiacheng.hundun.model.StockShareholderData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AKShareService {
    @Autowired
    private AKShareClient akShareClient;
    

    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取A股股票代码和名称列表
     * @return 股票信息列表
     */
    public List<StockInfo> getStockList() {
        List<StockInfo> result = new ArrayList<>();
        String json = akShareClient.fetchData("stock_info_a_code_name", null);
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockInfo info = new StockInfo();
                    info.setCode(node.path("code").asText());
                    info.setName(node.path("name").asText());
                    result.add(info);
                }
            }
        } catch (Exception e) {
            System.err.println("解析股票列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取港股股票代码和名称列表
     * @return 股票信息列表
     */
    public List<StockInfo> getHKStockList() {
        List<StockInfo> result = new ArrayList<>();
        String json = akShareClient.fetchData("stock_hk_ggt_components_em", null);
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockInfo info = new StockInfo();
                    info.setCode(node.path("代码").asText());
                    info.setName(node.path("名称").asText());
                    result.add(info);
                }
            }
        } catch (Exception e) {
            System.err.println("解析港股列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取同花顺资产负债表数据
     * @param symbol 股票代码
     * @return 资产负债表数据列表
     */
    public List<StockFinancialDebtThs> getStockFinancialDebtThs(String symbol) {
        List<StockFinancialDebtThs> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        request.setIndicator("按报告期");
        String json = akShareClient.fetchData("stock_financial_debt_ths", request);
        
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockFinancialDebtThs item = new StockFinancialDebtThs();
                    item.setReportPeriod(parseFieldValue(node, "报告期"));
                    item.setCoreIndicator(parseFieldValue(node, "报表核心指标"));
                    item.setEquityTotal(parseFieldValue(node, "*所有者权益（或股东权益）合计"));
                    item.setAssetTotal(parseFieldValue(node, "*资产合计"));
                    item.setDebtTotal(parseFieldValue(node, "*负债合计"));
                    item.setParentEquityTotal(parseFieldValue(node, "*归属于母公司所有者权益合计"));
                    item.setAllIndicator(parseFieldValue(node, "报表全部指标"));
                    item.setCurrentAssets(parseFieldValue(node, "流动资产"));
                    item.setCash(parseFieldValue(node, "货币资金"));
                    item.setLendingFunds(parseFieldValue(node, "拆出资金"));
                    item.setTradingFinancialAssets(parseFieldValue(node, "交易性金融资产"));
                    item.setReceivables(parseFieldValue(node, "应收票据及应收账款"));
                    item.setReceivableNotes(parseFieldValue(node, "其中：应收票据"));
                    item.setReceivableAccounts(parseFieldValue(node, "应收账款"));
                    item.setPrepayments(parseFieldValue(node, "预付款项"));
                    item.setOtherReceivablesTotal(parseFieldValue(node, "其他应收款合计"));
                    item.setReceivableInterest(parseFieldValue(node, "其中：应收利息"));
                    item.setOtherReceivables(parseFieldValue(node, "其他应收款"));
                    item.setInventory(parseFieldValue(node, "存货"));
                    item.setNonCurrentAssetsDueWithinOneYear(parseFieldValue(node, "一年内到期的非流动资产"));
                    item.setOtherCurrentAssets(parseFieldValue(node, "其他流动资产"));
                    item.setTotalCash(parseFieldValue(node, "总现金"));
                    item.setCurrentAssetsTotal(parseFieldValue(node, "流动资产合计"));
                    item.setNonCurrentAssets(parseFieldValue(node, "非流动资产"));
                    item.setAvailableForSaleFinancialAssets(parseFieldValue(node, "可供出售金融资产"));
                    item.setHeldToMaturityInvestments(parseFieldValue(node, "持有至到期投资"));
                    item.setLongTermEquityInvestments(parseFieldValue(node, "长期股权投资"));
                    item.setOtherEquityInvestments(parseFieldValue(node, "其他权益工具投资"));
                    item.setOtherNonCurrentFinancialAssets(parseFieldValue(node, "其他非流动金融资产"));
                    item.setInvestmentProperty(parseFieldValue(node, "投资性房地产"));
                    item.setFixedAssetsTotal(parseFieldValue(node, "固定资产合计"));
                    item.setFixedAssets(parseFieldValue(node, "其中：固定资产"));
                    item.setFixedAssetsCleanup(parseFieldValue(node, "固定资产清理"));
                    item.setConstructionInProgressTotal(parseFieldValue(node, "在建工程合计"));
                    item.setConstructionInProgress(parseFieldValue(node, "其中：在建工程"));
                    item.setProjectMaterials(parseFieldValue(node, "工程物资"));
                    item.setIntangibleAssets(parseFieldValue(node, "无形资产"));
                    item.setGoodwill(parseFieldValue(node, "商誉"));
                    item.setLongTermDeferredExpenses(parseFieldValue(node, "长期待摊费用"));
                    item.setDeferredIncomeTaxAssets(parseFieldValue(node, "递延所得税资产"));
                    item.setOtherNonCurrentAssets(parseFieldValue(node, "其他非流动资产"));
                    item.setNonCurrentAssetsTotal(parseFieldValue(node, "非流动资产合计"));
                    item.setTotalAssets(parseFieldValue(node, "资产合计"));
                    item.setCurrentLiabilities(parseFieldValue(node, "流动负债"));
                    item.setShortTermLoans(parseFieldValue(node, "短期借款"));
                    item.setFairValueFinancialLiabilities(parseFieldValue(node, "以公允价值计量且其变动计入当期损益的金融负债"));
                    item.setDerivativeFinancialLiabilities(parseFieldValue(node, "衍生金融负债"));
                    item.setPayables(parseFieldValue(node, "应付票据及应付账款"));
                    item.setPayableNotes(parseFieldValue(node, "其中：应付票据"));
                    item.setPayableAccounts(parseFieldValue(node, "应付账款"));
                    item.setAdvanceReceipts(parseFieldValue(node, "预收款项"));
                    item.setContractLiabilities(parseFieldValue(node, "合同负债"));
                    item.setPayableStaffSalaries(parseFieldValue(node, "应付职工薪酬"));
                    item.setTaxesPayable(parseFieldValue(node, "应交税费"));
                    item.setOtherPayablesTotal(parseFieldValue(node, "其他应付款合计"));
                    item.setPayableInterest(parseFieldValue(node, "其中：应付利息"));
                    item.setPayableDividends(parseFieldValue(node, "应付股利"));
                    item.setOtherPayables(parseFieldValue(node, "其他应付款"));
                    item.setNonCurrentLiabilitiesDueWithinOneYear(parseFieldValue(node, "一年内到期的非流动负债"));
                    item.setOtherCurrentLiabilities(parseFieldValue(node, "其他流动负债"));
                    item.setCurrentLiabilitiesTotal(parseFieldValue(node, "流动负债合计"));
                    item.setNonCurrentLiabilities(parseFieldValue(node, "非流动负债"));
                    item.setLongTermLoans(parseFieldValue(node, "长期借款"));
                    item.setLongTermPayablesTotal(parseFieldValue(node, "长期应付款合计"));
                    item.setLongTermPayables(parseFieldValue(node, "其中：长期应付款"));
                    item.setSpecialPayables(parseFieldValue(node, "专项应付款"));
                    item.setEstimatedLiabilities(parseFieldValue(node, "预计负债"));
                    item.setDeferredIncomeTaxLiabilities(parseFieldValue(node, "递延所得税负债"));
                    item.setDeferredIncomeNonCurrentLiabilities(parseFieldValue(node, "递延收益-非流动负债"));
                    item.setOtherNonCurrentLiabilities(parseFieldValue(node, "其他非流动负债"));
                    item.setNonCurrentLiabilitiesTotal(parseFieldValue(node, "非流动负债合计"));
                    item.setTotalLiabilities(parseFieldValue(node, "负债合计"));
                    item.setEquity(parseFieldValue(node, "所有者权益（或股东权益）"));
                    item.setPaidInCapital(parseFieldValue(node, "实收资本（或股本）"));
                    item.setCapitalReserve(parseFieldValue(node, "资本公积"));
                    item.setTreasuryStock(parseFieldValue(node, "减：库存股"));
                    item.setOtherComprehensiveIncome(parseFieldValue(node, "其他综合收益"));
                    item.setSurplusReserve(parseFieldValue(node, "盈余公积"));
                    item.setUndistributedProfits(parseFieldValue(node, "未分配利润"));
                    item.setParentEquity(parseFieldValue(node, "归属于母公司所有者权益合计"));
                    item.setMinorityEquity(parseFieldValue(node, "少数股东权益"));
                    item.setEquityTotal2(parseFieldValue(node, "所有者权益（或股东权益）合计"));
                    item.setTotalLiabilitiesAndEquity(parseFieldValue(node, "负债和所有者权益（或股东权益）合计"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("解析stock_financial_debt_ths数据失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取同花顺利润表数据（结构化实体类）
     */
    public List<StockFinancialBenefitThs> getStockFinancialBenefitThs(String symbol) {
        List<StockFinancialBenefitThs> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        request.setIndicator("按报告期");
        String json = akShareClient.fetchData("stock_financial_benefit_ths", request);
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockFinancialBenefitThs item = new StockFinancialBenefitThs();
                    item.setReportPeriod(parseFieldValue(node, "报告期"));
                    item.setCoreIndicator(parseFieldValue(node, "报表核心指标"));
                    item.setNetProfit(parseFieldValue(node, "*净利润"));
                    item.setTotalRevenue(parseFieldValue(node, "*营业总收入"));
                    item.setTotalCost(parseFieldValue(node, "*营业总成本"));
                    item.setParentNetProfit(parseFieldValue(node, "*归属于母公司所有者的净利润"));
                    item.setNetProfitExclExtra(parseFieldValue(node, "*扣除非经常性损益后的净利润"));
                    item.setAllIndicator(parseFieldValue(node, "报表全部指标"));
                    item.setRevenue1(parseFieldValue(node, "一、营业总收入"));
                    item.setRevenue(parseFieldValue(node, "其中：营业收入"));
                    item.setCost2(parseFieldValue(node, "二、营业总成本"));
                    item.setCost(parseFieldValue(node, "其中：营业成本"));
                    item.setTaxAndSurcharge(parseFieldValue(node, "营业税金及附加"));
                    item.setSellingExpense(parseFieldValue(node, "销售费用"));
                    item.setAdminExpense(parseFieldValue(node, "管理费用"));
                    item.setRAndDExpense(parseFieldValue(node, "研发费用"));
                    item.setFinancialExpense(parseFieldValue(node, "财务费用"));
                    item.setInterestExpense(parseFieldValue(node, "其中：利息费用"));
                    item.setInterestIncome(parseFieldValue(node, "利息收入"));
                    item.setAssetImpairmentLoss(parseFieldValue(node, "资产减值损失"));
                    item.setCreditImpairmentLoss(parseFieldValue(node, "信用减值损失"));
                    item.setFairValueIncome(parseFieldValue(node, "加：公允价值变动收益"));
                    item.setInvestmentIncome(parseFieldValue(node, "投资收益"));
                    item.setJointInvestmentIncome(parseFieldValue(node, "其中：联营企业和合营企业的投资收益"));
                    item.setAssetDisposalIncome(parseFieldValue(node, "资产处置收益"));
                    item.setOtherIncome(parseFieldValue(node, "其他收益"));
                    item.setOperatingProfit(parseFieldValue(node, "三、营业利润"));
                    item.setNonOperatingIncome(parseFieldValue(node, "加：营业外收入"));
                    item.setNonCurrentAssetDisposalGain(parseFieldValue(node, "其中：非流动资产处置利得"));
                    item.setNonOperatingExpense(parseFieldValue(node, "减：营业外支出"));
                    item.setNonCurrentAssetDisposalLoss(parseFieldValue(node, "其中：非流动资产处置损失"));
                    item.setTotalProfit(parseFieldValue(node, "四、利润总额"));
                    item.setIncomeTaxExpense(parseFieldValue(node, "减：所得税费用"));
                    item.setNetProfit2(parseFieldValue(node, "五、净利润"));
                    item.setContinuedNetProfit(parseFieldValue(node, "（一）持续经营净利润"));
                    item.setParentNetProfit2(parseFieldValue(node, "归属于母公司所有者的净利润"));
                    item.setMinorityProfit(parseFieldValue(node, "少数股东损益"));
                    item.setNetProfitExclExtra2(parseFieldValue(node, "扣除非经常性损益后的净利润"));
                    item.setEps6(parseFieldValue(node, "六、每股收益"));
                    item.setBasicEps(parseFieldValue(node, "（一）基本每股收益"));
                    item.setDilutedEps(parseFieldValue(node, "（二）稀释每股收益"));
                    item.setOtherComprehensiveIncome(parseFieldValue(node, "七、其他综合收益"));
                    item.setParentOtherComprehensiveIncome(parseFieldValue(node, "归属母公司所有者的其他综合收益"));
                    item.setTotalComprehensiveIncome(parseFieldValue(node, "八、综合收益总额"));
                    item.setParentTotalComprehensiveIncome(parseFieldValue(node, "归属于母公司股东的综合收益总额"));
                    item.setMinorityTotalComprehensiveIncome(parseFieldValue(node, "归属于少数股东的综合收益总额"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("解析stock_financial_benefit_ths数据失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取同花顺现金流量表数据（结构化实体类）
     */
    public List<StockFinancialCashThs> getStockFinancialCashThs(String symbol) {
        List<StockFinancialCashThs> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        request.setIndicator("按报告期");
        String json = akShareClient.fetchData("stock_financial_cash_ths", request);
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockFinancialCashThs item = new StockFinancialCashThs();
                    item.setReportPeriod(parseFieldValue(node, "报告期"));
                    item.setCoreIndicator(parseFieldValue(node, "报表核心指标"));
                    item.setNetCashIncrease(parseFieldValue(node, "*现金及现金等价物净增加额"));
                    item.setNetCashFromOperating(parseFieldValue(node, "*经营活动产生的现金流量净额"));
                    item.setNetCashFromInvesting(parseFieldValue(node, "*投资活动产生的现金流量净额"));
                    item.setNetCashFromFinancing(parseFieldValue(node, "*筹资活动产生的现金流量净额"));
                    item.setEndingCash(parseFieldValue(node, "*期末现金及现金等价物余额"));
                    item.setAllIndicator(parseFieldValue(node, "报表全部指标"));
                    item.setOperatingCashFlow(parseFieldValue(node, "一、经营活动产生的现金流量"));
                    item.setCashReceivedFromSales(parseFieldValue(node, "销售商品、提供劳务收到的现金"));
                    item.setTaxRefund(parseFieldValue(node, "收到的税费与返还"));
                    item.setOtherOperatingCashIn(parseFieldValue(node, "收到其他与经营活动有关的现金"));
                    item.setTotalOperatingCashIn(parseFieldValue(node, "经营活动现金流入小计"));
                    item.setCashPaidForGoods(parseFieldValue(node, "购买商品、接受劳务支付的现金"));
                    item.setCashPaidToEmployees(parseFieldValue(node, "支付给职工以及为职工支付的现金"));
                    item.setTaxesPaid(parseFieldValue(node, "支付的各项税费"));
                    item.setOtherOperatingCashOut(parseFieldValue(node, "支付其他与经营活动有关的现金"));
                    item.setTotalOperatingCashOut(parseFieldValue(node, "经营活动现金流出小计"));
                    item.setNetOperatingCash(parseFieldValue(node, "经营活动产生的现金流量净额"));
                    item.setInvestingCashFlow(parseFieldValue(node, "二、投资活动产生的现金流量"));
                    item.setCashReceivedFromInvestments(parseFieldValue(node, "收回投资收到的现金"));
                    item.setInvestmentIncomeReceived(parseFieldValue(node, "取得投资收益收到的现金"));
                    item.setCashFromAssetDisposal(parseFieldValue(node, "处置固定资产、无形资产和其他长期资产收回的现金净额"));
                    item.setCashFromSubsidiaryDisposal(parseFieldValue(node, "处置子公司及其他营业单位收到的现金净额"));
                    item.setOtherInvestingCashIn(parseFieldValue(node, "收到其他与投资活动有关的现金"));
                    item.setTotalInvestingCashIn(parseFieldValue(node, "投资活动现金流入小计"));
                    item.setCashPaidForAssets(parseFieldValue(node, "购建固定资产、无形资产和其他长期资产支付的现金"));
                    item.setCashPaidForInvestments(parseFieldValue(node, "投资支付的现金"));
                    item.setCashPaidForSubsidiary(parseFieldValue(node, "取得子公司及其他营业单位支付的现金净额"));
                    item.setOtherInvestingCashOut(parseFieldValue(node, "支付其他与投资活动有关的现金"));
                    item.setTotalInvestingCashOut(parseFieldValue(node, "投资活动现金流出小计"));
                    item.setNetInvestingCash(parseFieldValue(node, "投资活动产生的现金流量净额"));
                    item.setFinancingCashFlow(parseFieldValue(node, "三、筹资活动产生的现金流量"));
                    item.setCashReceivedFromFinancing(parseFieldValue(node, "吸收投资收到的现金"));
                    item.setCashReceivedFromMinorityFinancing(parseFieldValue(node, "其中：子公司吸收少数股东投资收到的现金"));
                    item.setCashReceivedFromBorrowings(parseFieldValue(node, "取得借款收到的现金"));
                    item.setOtherFinancingCashIn(parseFieldValue(node, "收到其他与筹资活动有关的现金"));
                    item.setTotalFinancingCashIn(parseFieldValue(node, "筹资活动现金流入小计"));
                    item.setCashPaidForDebt(parseFieldValue(node, "偿还债务支付的现金"));
                    item.setCashPaidForDividends(parseFieldValue(node, "分配股利、利润或偿付利息支付的现金"));
                    item.setCashPaidToMinorityDividends(parseFieldValue(node, "其中：子公司支付给少数股东的股利、利润"));
                    item.setOtherFinancingCashOut(parseFieldValue(node, "支付其他与筹资活动有关的现金"));
                    item.setTotalFinancingCashOut(parseFieldValue(node, "筹资活动现金流出小计"));
                    item.setNetFinancingCash(parseFieldValue(node, "筹资活动产生的现金流量净额"));
                    item.setFxEffectOnCash(parseFieldValue(node, "四、汇率变动对现金及现金等价物的影响"));
                    item.setNetCashIncrease2(parseFieldValue(node, "五、现金及现金等价物净增加额"));
                    item.setBeginningCash(parseFieldValue(node, "加：期初现金及现金等价物余额"));
                    item.setEndingCash2(parseFieldValue(node, "六、期末现金及现金等价物余额"));
                    item.setSupplement(parseFieldValue(node, "补充资料："));
                    item.setProfitToOperatingCash(parseFieldValue(node, "1、将净利润调节为经营活动现金流量："));
                    item.setNetProfit(parseFieldValue(node, "净利润"));
                    item.setAssetImpairmentProvision(parseFieldValue(node, "加：资产减值准备"));
                    item.setDepreciation(parseFieldValue(node, "固定资产折旧、油气资产折耗、生产性生物资产折旧"));
                    item.setAmortization(parseFieldValue(node, "无形资产摊销"));
                    item.setLongTermDeferredExpenseAmortization(parseFieldValue(node, "长期待摊费用摊销"));
                    item.setAssetDisposalLoss(parseFieldValue(node, "处置固定资产、无形资产和其他长期资产的损失"));
                    item.setFixedAssetScrapLoss(parseFieldValue(node, "固定资产报废损失"));
                    item.setFairValueLoss(parseFieldValue(node, "公允价值变动损失"));
                    item.setFinancialExpense2(parseFieldValue(node, "财务费用"));
                    item.setInvestmentLoss(parseFieldValue(node, "投资损失"));
                    item.setDeferredTaxAssetDecrease(parseFieldValue(node, "递延所得税资产减少"));
                    item.setDeferredTaxLiabilityIncrease(parseFieldValue(node, "递延所得税负债增加"));
                    item.setInventoryDecrease(parseFieldValue(node, "存货的减少"));
                    item.setReceivableDecrease(parseFieldValue(node, "经营性应收项目的减少"));
                    item.setPayableIncrease(parseFieldValue(node, "经营性应付项目的增加"));
                    item.setOther(parseFieldValue(node, "其他"));
                    item.setIndirectNetOperatingCash(parseFieldValue(node, "间接法-经营活动产生的现金流量净额"));
                    item.setNonCashInvestingFinancing(parseFieldValue(node, "2、不涉及现金收支的重大投资和筹资活动："));
                    item.setCashChange(parseFieldValue(node, "3、现金及现金等价物净变动情况："));
                    item.setEndingCash3(parseFieldValue(node, "现金的期末余额"));
                    item.setBeginningCash2(parseFieldValue(node, "减：现金的期初余额"));
                    item.setEndingCashEquivalent(parseFieldValue(node, "加：现金等价物的期末余额"));
                    item.setIndirectNetCashIncrease(parseFieldValue(node, "间接法-现金及现金等价物净增加额"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("解析stock_financial_cash_ths数据失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取股票基本信息
     * @param symbol 股票代码
     * @return 股票基本信息
     */
    public StockInfo getStockIndividualInfo(String symbol) {
        StockInfo stockInfo = new StockInfo();
        stockInfo.setCode(symbol);
        
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        
        String json = akShareClient.fetchData("stock_individual_info_em", request);
        if (json == null) {
            return stockInfo;
        }
        
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String item = node.path("item").asText();
                    String value = node.path("value").asText();
                    
                    switch (item) {
                        case "股票简称":
                            stockInfo.setName(value);
                            break;
                        case "最新":
                            stockInfo.setLatestPrice(value);
                            break;
                        case "总股本":
                            stockInfo.setTotalShares(value);
                            break;
                        case "流通股":
                            stockInfo.setCirculatingShares(value);
                            break;
                        case "总市值":
                            stockInfo.setTotalMarketValue(value);
                            break;
                        case "流通市值":
                            stockInfo.setCirculatingMarketValue(value);
                            break;
                        case "行业":
                            stockInfo.setIndustry(value);
                            break;
                        case "上市时间":
                            stockInfo.setListingDate(value);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("解析stock_individual_info_em数据失败: " + e.getMessage());
        }
        
        // 计算ROE和市盈率
        try {
            calculateROEAndPE(stockInfo);
        } catch (Exception e) {
            System.err.println("计算ROE和市盈率失败: " + e.getMessage());
        }
        
        return stockInfo;
    }

    /**
     * 获取股票历史估值数据
     * @param symbol 股票代码
     * @return 历史估值数据列表
     */
    public List<StockValuationData> getStockValuationData(String symbol) {
        List<StockValuationData> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        request.setIndicator("市盈率(TTM)");
        request.setPeriod("全部");
        
        String json = akShareClient.fetchData("stock_zh_valuation_baidu", request);
        if (json == null) {
            return result;
        }
        
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockValuationData item = new StockValuationData();
                    item.setDate(parseFieldValue(node, "date"));
                    item.setSymbol(symbol);
                    item.setValue(parseFieldValue(node, "value"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("解析stock_zh_valuation_baidu数据失败: " + e.getMessage());
        }
        
        // 按日期排序，确保最新的数据在最后
        result.sort((a, b) -> {
            if (a.getDate() == null && b.getDate() == null) return 0;
            if (a.getDate() == null) return -1;
            if (b.getDate() == null) return 1;
            return a.getDate().compareTo(b.getDate());
        });
        
        return result;
    }

    /**
     * 获取股票历史换手率数据
     * @param symbol 股票代码
     * @return 历史换手率数据列表
     */
    public List<StockTurnoverData> getStockTurnoverData(String symbol) {
        List<StockTurnoverData> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        request.setPeriod("daily");
        
        String json = akShareClient.fetchData("stock_zh_a_hist", request);
        if (json == null) {
            return result;
        }
        
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockTurnoverData item = new StockTurnoverData();
                    item.setDate(parseFieldValue(node, "日期"));
                    item.setSymbol(symbol);
                    item.setTurnover(parseFieldValue(node, "换手率"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("解析stock_zh_a_hist换手率数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取股票财务摘要数据
     * @param symbol 股票代码
     * @return 财务摘要数据列表
     */
    public List<StockFinancialAbstractThs> getStockFinancialAbstractThs(String symbol) {
        List<StockFinancialAbstractThs> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        request.setIndicator("按年度");
        String json = akShareClient.fetchData("stock_financial_abstract_ths", request);
        
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockFinancialAbstractThs item = new StockFinancialAbstractThs();
                    item.setReportPeriod(parseFieldValue(node, "报告期"));
                    item.setRoe(parseFieldValue(node, "净资产收益率"));
                    item.setDilutedRoe(parseFieldValue(node, "净资产收益率-摊薄"));
                    item.setNetProfitMargin(parseFieldValue(node, "销售净利率"));
                    item.setCurrentRatio(parseFieldValue(node, "流动比率"));
                    item.setQuickRatio(parseFieldValue(node, "速动比率"));
                    item.setDebtToAssetRatio(parseFieldValue(node, "资产负债率"));
                    item.setGrossProfitMargin(parseFieldValue(node, "销售毛利率"));
                    item.setAssetTurnover(parseFieldValue(node, "总资产周转率"));
                    item.setInventoryTurnover(parseFieldValue(node, "存货周转率"));
                    item.setReceivableTurnover(parseFieldValue(node, "应收账款周转率"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("解析stock_financial_abstract_ths数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 计算ROE和市盈率
     * @param stockInfo 股票信息对象
     */
    private void calculateROEAndPE(StockInfo stockInfo) {
        try {
            // 使用财务摘要接口获取ROE数据，更准确
            List<StockFinancialAbstractThs> abstractList = getStockFinancialAbstractThs(stockInfo.getCode());
            
            if (!abstractList.isEmpty()) {
                // 获取最新的财务摘要数据（数据按时间正序排列，最新的在最后）
                StockFinancialAbstractThs latestAbstract = abstractList.get(abstractList.size() - 1);
                
                // 优先使用净资产收益率-摊薄，如果没有则使用净资产收益率
                String roeValue = latestAbstract.getDilutedRoe();
                if (roeValue == null || roeValue.trim().isEmpty() || "0".equals(roeValue.trim())) {
                    roeValue = latestAbstract.getRoe();
                }
                
                if (roeValue != null && !roeValue.trim().isEmpty() && !"0".equals(roeValue.trim())) {
                    stockInfo.setRoe(roeValue);
                }
            }
            
            // 获取最新的市盈率数据
            List<StockValuationData> valuationData = getStockValuationData(stockInfo.getCode());
            if (valuationData != null && !valuationData.isEmpty()) {
                // 获取最新的市盈率数据（数据按时间正序排列，最新的在最后）
                StockValuationData latestValuation = valuationData.get(valuationData.size() - 1);
                if (latestValuation.getValue() != null) {
                    stockInfo.setPe(latestValuation.getValue());
                }
            }
        } catch (Exception e) {
            System.err.println("计算ROE和市盈率时发生错误: " + e.getMessage());
        }
    }

    /**
     * 统一解析字段值，处理Boolean和String类型的不一致问题
     * Boolean false -> "0", Boolean true -> "1", String -> 原值
     */
    private String parseFieldValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return null;
        } else if (fieldNode.isBoolean()) {
            return fieldNode.asBoolean() ? "1" : "0";
        } else {
            return fieldNode.asText(null);
        }
    }

    /**
     * 获取港股资产负债表数据
     * @param stock 股票代码（5位数字）
     * @return 港股资产负债表数据列表
     */
    public List<StockFinancialHkBalanceSheet> getStockFinancialHkBalanceSheet(String stock) {
        System.out.println("开始获取港股资产负债表数据，股票代码: " + stock);
        List<StockFinancialHkBalanceSheet> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setStock(stock);
        request.setExtraParam("symbol", "资产负债表");
        request.setIndicator("报告期");
        System.out.println("请求参数: stock=" + stock + ", symbol=资产负债表, indicator=报告期");
        String json = akShareClient.fetchData("stock_financial_hk_report_em", request);
        
        System.out.println("港股资产负债表原始数据: " + json);
        
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                // 按报告期分组处理数据
                Map<String, StockFinancialHkBalanceSheet> periodMap = new HashMap<>();
                
                for (JsonNode node : root) {
                    String reportDate = parseFieldValue(node, "REPORT_DATE");
                    String itemName = parseFieldValue(node, "STD_ITEM_NAME");
                    String amount = parseFieldValue(node, "AMOUNT");
                    
                    if (reportDate == null || itemName == null) {
                        continue;
                    }
                    
                    // 获取或创建该报告期的资产负债表对象
                    StockFinancialHkBalanceSheet balanceSheet = periodMap.computeIfAbsent(reportDate, k -> {
                        StockFinancialHkBalanceSheet bs = new StockFinancialHkBalanceSheet();
                        bs.setReportPeriod(reportDate);
                        bs.setStockCode(parseFieldValue(node, "SECURITY_CODE"));
                        bs.setStockName(parseFieldValue(node, "SECURITY_NAME_ABBR"));
                        return bs;
                    });
                    
                    // 根据项目名称映射到对应字段
                    mapBalanceSheetItem(balanceSheet, itemName, amount);
                }
                
                result.addAll(periodMap.values());
            }
        } catch (Exception e) {
            System.err.println("解析港股资产负债表数据失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 映射资产负债表项目到实体字段
     */
    private void mapBalanceSheetItem(StockFinancialHkBalanceSheet balanceSheet, String itemName, String amount) {
        if (itemName == null || amount == null) {
            return;
        }
        
        // 根据项目名称进行智能映射
        switch (itemName) {
            // 流动资产
            case "流动资产合计":
                balanceSheet.setLiuDongZiChanHeJi(amount);
                break;
            case "现金及等价物":
                balanceSheet.setXianJinJiDengJiaWu(amount);
                break;
            case "短期存款":
                balanceSheet.setDuanQiCunKuan(amount);
                break;
            case "受限制存款及现金":
                balanceSheet.setShouXianZhiCunKuanJiXianJin(amount);
                break;
            case "中长期存款":
                balanceSheet.setZhongChangQiCunKuan(amount);
                break;
            case "指定以公允价值记账之金融资产(流动)":
                balanceSheet.setZhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChanLiuDong(amount);
                break;
            case "其他金融资产(流动)":
                balanceSheet.setQiTaJinRongZiChanLiuDong(amount);
                break;
            case "应收帐款":
                balanceSheet.setYingShouZhangKuan(amount);
                break;
            case "应收关联方款项":
                balanceSheet.setYingShouGuanLianFangKuanXiang(amount);
                break;
            case "预付款按金及其他应收款":
                balanceSheet.setYuFuKuanAnJinJiQiTaYingShouKuan(amount);
                break;
            case "预付款项":
                balanceSheet.setYuFuKuanXiang(amount);
                break;
            case "存货":
                balanceSheet.setCunHuo(amount);
                break;
            case "持作出售的资产(流动)":
                balanceSheet.setChiZuoChuShouDeZiChanLiuDong(amount);
                break;
            case "净流动资产":
                balanceSheet.setJingLiuDongZiChan(amount);
                break;
                
            // 非流动资产
            case "非流动资产合计":
                balanceSheet.setFeiLiuDongZiChanHeJi(amount);
                break;
            case "物业厂房及设备":
                balanceSheet.setWuYeChangFangJiSheBei(amount);
                break;
            case "在建工程":
                balanceSheet.setZaiJianGongCheng(amount);
                break;
            case "投资物业":
                balanceSheet.setTouZiWuYe(amount);
                break;
            case "土地使用权":
                balanceSheet.setTuDiShiYongQuan(amount);
                break;
            case "无形资产":
                balanceSheet.setWuXingZiChan(amount);
                break;
            case "商誉":
                balanceSheet.setShangYu(amount);
                break;
            case "递延税项资产":
                balanceSheet.setDiYanShuiXiangZiChan(amount);
                break;
            case "可供出售投资":
                balanceSheet.setKeGongChuShouTouZi(amount);
                break;
            case "指定以公允价值记账之金融资产":
                balanceSheet.setZhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChan(amount);
                break;
            case "其他金融资产(非流动)":
                balanceSheet.setQiTaJinRongZiChanFeiLiuDong(amount);
                break;
            case "联营公司权益":
                balanceSheet.setLianYingGongSiQuanYi(amount);
                break;
            case "合营公司权益":
                balanceSheet.setHeYingGongSiQuanYi(amount);
                break;
            case "于联营公司可赎回工具的投资":
                balanceSheet.setYuLianYingGongSiKeShuHuiGongJuDeTouZi(amount);
                break;
            case "非流动资产其他项目":
                balanceSheet.setFeiLiuDongZiChanQiTaXiangMu(amount);
                break;
                
            // 资产总计
            case "总资产":
                balanceSheet.setZongZiChan(amount);
                break;
            case "总资产减流动负债":
                balanceSheet.setZongZiChanJianLiuDongFuZhai(amount);
                break;
            case "总资产减总负债合计":
                balanceSheet.setZongZiChanJianZongFuZhaiHeJi(amount);
                break;
                
            // 流动负债
            case "流动负债合计":
                balanceSheet.setLiuDongFuZhaiHeJi(amount);
                break;
            case "短期贷款":
                balanceSheet.setDuanQiDaiKuan(amount);
                break;
            case "应付帐款":
                balanceSheet.setYingFuZhangKuan(amount);
                break;
            case "应付票据":
                balanceSheet.setYingFuPiaoJu(amount);
                break;
            case "应付税项":
                balanceSheet.setYingFuShuiXiang(amount);
                break;
            case "应付股利":
                balanceSheet.setYingFuGuLi(amount);
                break;
            case "应付关联方款项(流动)":
                balanceSheet.setYingFuGuanLianFangKuanXiangLiuDong(amount);
                break;
            case "其他应付款及应计费用":
                balanceSheet.setQiTaYingFuKuanJiYingJiFeiYong(amount);
                break;
            case "其他金融负债(流动)":
                balanceSheet.setQiTaJinRongFuZhaiLiuDong(amount);
                break;
            case "融资租赁负债(流动)":
                balanceSheet.setRongZiZuLinFuZhaiLiuDong(amount);
                break;
            case "递延收入(流动)":
                balanceSheet.setDiYanShouRuLiuDong(amount);
                break;
                
            // 非流动负债
            case "非流动负债合计":
                balanceSheet.setFeiLiuDongFuZhaiHeJi(amount);
                break;
            case "长期贷款":
                balanceSheet.setChangQiDaiKuan(amount);
                break;
            case "长期应付款":
                balanceSheet.setChangQiYingFuKuan(amount);
                break;
            case "递延税项负债":
                balanceSheet.setDiYanShuiXiangFuZhai(amount);
                break;
            case "其他金融负债(非流动)":
                balanceSheet.setQiTaJinRongFuZhaiFeiLiuDong(amount);
                break;
            case "融资租赁负债(非流动)":
                balanceSheet.setRongZiZuLinFuZhaiFeiLiuDong(amount);
                break;
            case "递延收入(非流动)":
                balanceSheet.setDiYanShouRuFeiLiuDong(amount);
                break;
                
            // 负债总计
            case "总负债":
                balanceSheet.setZongFuZhai(amount);
                break;
                
            // 所有者权益
            case "股东权益":
                balanceSheet.setGuDongQuanYi(amount);
                break;
            case "股本":
                balanceSheet.setGuBen(amount);
                break;
            case "股本溢价":
                balanceSheet.setGuBenYiJia(amount);
                break;
            case "储备":
                balanceSheet.setChuBei(amount);
                break;
            case "其他储备":
                balanceSheet.setQiTaChuBei(amount);
                break;
            case "保留溢利(累计亏损)":
                balanceSheet.setBaoLiuYiLiLeiJiKuiSun(amount);
                break;
            case "库存股":
                balanceSheet.setKuCunGu(amount);
                break;
            case "少数股东权益":
                balanceSheet.setShaoShuGuDongQuanYi(amount);
                break;
            case "总权益":
                balanceSheet.setZongQuanYi(amount);
                break;
            case "总权益及总负债":
                balanceSheet.setZongQuanYiJiZongFuZhai(amount);
                break;
            case "总权益及非流动负债":
                balanceSheet.setZongQuanYiJiFeiLiuDongFuZhai(amount);
                break;
            case "净资产":
                balanceSheet.setJingZiChan(amount);
                break;
                
            // 其他可能出现的字段
            case "可转换票据及债券":
                balanceSheet.setKeZhuanHuanPiaoJuJiZhaiQuan(amount);
                break;
            case "合同负债":
                balanceSheet.setHeTongFuZhai(amount);
                break;
            case "应付债券":
                balanceSheet.setYingFuZhaiQuan(amount);
                break;
            case "流动资产其他项目":
                balanceSheet.setLiuDongZiChanQiTaXiangMu(amount);
                break;
            case "贷款及垫款":
                balanceSheet.setDaiKuanJiDianKuan(amount);
                break;
            case "长期应收款":
                balanceSheet.setChangQiYingShouKuan(amount);
                break;
            case "拨备(流动)":
                balanceSheet.setBoBeiLiuDong(amount);
                break;
            case "拨备(非流动)":
                balanceSheet.setBoBeiFeiLiuDong(amount);
                break;
            case "持作出售的负债(流动)":
                balanceSheet.setChiZuoChuShouDeFuZhaiLiuDong(amount);
                break;
            case "衍生金融工具-负债(流动)":
                balanceSheet.setYanShengJinRongGongJuFuZhaiLiuDong(amount);
                break;
            case "衍生金融工具-资产(流动)":
                balanceSheet.setYanShengJinRongGongJuZiChanLiuDong(amount);
                break;
            case "固定资产":
                balanceSheet.setGuDingZiChan(amount);
                break;
            case "交易性金融资产(流动)":
                balanceSheet.setJiaoYiXingJinRongZiChanLiuDong(amount);
                break;
            case "公积金":
                balanceSheet.setGongJiJin(amount);
                break;
            case "其他投资":
                balanceSheet.setQiTaTouZi(amount);
                break;
            case "其他综合性收益":
                balanceSheet.setQiTaZongHeXingShouYi(amount);
                break;
            case "其他非流动负债":
                balanceSheet.setQiTaFeiLiuDongFuZhai(amount);
                break;
            case "外币报表折算差额":
                balanceSheet.setWaiBiBaoBiaoZheSuanChaE(amount);
                break;
            case "流动负债其他项目":
                balanceSheet.setLiuDongFuZhaiQiTaXiangMu(amount);
                break;
            case "短期投资":
                balanceSheet.setDuanQiTouZi(amount);
                break;
            case "股东权益其他项目":
                balanceSheet.setGuDongQuanYiQiTaXiangMu(amount);
                break;
            case "证券投资":
                balanceSheet.setZhengQuanTouZi(amount);
                break;
            case "重估储备":
                balanceSheet.setZhongGuChuBei(amount);
                break;
            case "非流动负债其他项目":
                balanceSheet.setFeiLiuDongFuZhaiQiTaXiangMu(amount);
                break;
            case "非运算项目":
                balanceSheet.setFeiYunSuanXiangMu(amount);
                break;
                
            default:
                // 对于未映射的项目，可以记录日志或忽略
                System.out.println("未映射的资产负债表项目: " + itemName + " = " + amount);
                break;
        }
    }

    /**
     * 获取港股利润表数据
     * @param stock 股票代码（5位数字）
     * @return 港股利润表数据列表
     */
    public List<StockFinancialHkIncomeStatement> getStockFinancialHkIncomeStatement(String stock) {
        List<StockFinancialHkIncomeStatement> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setStock(stock);
        request.setExtraParam("symbol", "利润表");
        request.setIndicator("报告期");
        String json = akShareClient.fetchData("stock_financial_hk_report_em", request);
        
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                // 按报告期分组处理数据
                Map<String, StockFinancialHkIncomeStatement> periodMap = new HashMap<>();
                
                for (JsonNode node : root) {
                    String reportDate = parseFieldValue(node, "REPORT_DATE");
                    String itemName = parseFieldValue(node, "STD_ITEM_NAME");
                    String amount = parseFieldValue(node, "AMOUNT");
                    
                    if (reportDate == null || itemName == null) {
                        continue;
                    }
                    
                    // 获取或创建该报告期的利润表对象
                    StockFinancialHkIncomeStatement incomeStatement = periodMap.computeIfAbsent(reportDate, k -> {
                        StockFinancialHkIncomeStatement is = new StockFinancialHkIncomeStatement();
                        is.setReportPeriod(reportDate);
                        is.setStockCode(parseFieldValue(node, "SECURITY_CODE"));
                        is.setStockName(parseFieldValue(node, "SECURITY_NAME_ABBR"));
                        return is;
                    });
                    
                    // 根据项目名称映射到对应字段
                    System.out.println("映射利润表项目: " + itemName + " = " + amount);
                    mapIncomeStatementItem(incomeStatement, itemName, amount);
                }
                
                result.addAll(periodMap.values());
            }
        } catch (Exception e) {
            System.err.println("解析港股利润表数据失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 映射利润表项目到实体字段
     */
    private void mapIncomeStatementItem(StockFinancialHkIncomeStatement incomeStatement, String itemName, String amount) {
        if (itemName == null || amount == null) {
            return;
        }
        
        // 根据项目名称进行智能映射
        switch (itemName) {
            case "营业额":
                incomeStatement.setRevenue(amount);
                break;
            case "营运收入":
                incomeStatement.setTotalRevenue(amount);
                break;
            case "其他收益":
                incomeStatement.setOtherRevenue(amount);
                break;
            case "利息收入":
                incomeStatement.setInterestIncome(amount);
                break;
            case "毛利":
                incomeStatement.setGrossProfit(amount);
                break;
            case "营运支出":
                incomeStatement.setTotalOperatingExpenses(amount);
                break;
            case "行政开支":
                incomeStatement.setAdministrativeExpenses(amount);
                break;
            case "减值及拨备":
                incomeStatement.setImpairmentAndProvisions(amount);
                break;
            case "折旧与摊销":
                incomeStatement.setDepreciationAndAmortization(amount);
                break;
            case "薪金福利支出":
                // 可以映射到销售及分销费用或行政费用
                break;
            case "经营溢利":
                incomeStatement.setOperatingProfit(amount);
                break;
            case "融资成本":
                incomeStatement.setFinancialCosts(amount);
                break;
            case "其他收入":
                incomeStatement.setOtherIncome(amount);
                break;
            case "其他支出":
                incomeStatement.setOtherExpenses(amount);
                break;
            case "溢利其他项目":
                incomeStatement.setProfitOtherItems(amount);
                break;
            case "重估盈余":
                incomeStatement.setRevaluationSurplus(amount);
                break;
            case "非运算项目":
                incomeStatement.setNonOperatingItems(amount);
                break;
            case "除税前溢利":
                incomeStatement.setProfitBeforeTax(amount);
                break;
            case "应占合营公司溢利":
                incomeStatement.setShareOfJointVentureProfit(amount);
                break;
            case "应占联营公司溢利":
                incomeStatement.setShareOfAssociateProfit(amount);
                break;
            case "税项":
                incomeStatement.setIncomeTaxExpense(amount);
                break;
            case "除税后溢利":
                incomeStatement.setProfitForTheYear(amount);
                break;
            case "持续经营业务税后利润":
                incomeStatement.setProfitForTheYear(amount);
                break;
            case "每股基本盈利":
                incomeStatement.setBasicEarningsPerShare(amount);
                break;
            case "每股摊薄盈利":
                incomeStatement.setDilutedEarningsPerShare(amount);
                break;
            case "每股股息":
                incomeStatement.setDividendPerShare(amount);
                break;
            case "股东应占溢利":
                incomeStatement.setProfitAttributableToParent(amount);
                break;
            case "少数股东损益":
                incomeStatement.setMinorityInterests(amount);
                break;
            case "终止或非持续业务溢利":
                incomeStatement.setDiscontinuedOperationsProfit(amount);
                break;
            case "全面收益总额":
                incomeStatement.setTotalComprehensiveIncome(amount);
                break;
            case "本公司拥有人应占全面收益总额":
                incomeStatement.setComprehensiveIncomeAttributableToParent(amount);
                break;
            default:
                // 对于未映射的项目，记录日志以便调试
                System.out.println("未映射的利润表项目: " + itemName + " = " + amount);
                break;
        }
    }

    /**
     * 获取港股现金流量表数据
     * @param stock 股票代码（5位数字）
     * @return 港股现金流量表数据列表
     */
    public List<StockFinancialHkCashFlow> getStockFinancialHkCashFlow(String stock) {
        List<StockFinancialHkCashFlow> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setStock(stock);
        request.setExtraParam("symbol", "现金流量表");
        request.setIndicator("报告期");
        String json = akShareClient.fetchData("stock_financial_hk_report_em", request);
        
        if (json == null) {
            return result;
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                // 按报告期分组处理数据
                Map<String, StockFinancialHkCashFlow> periodMap = new HashMap<>();
                
                for (JsonNode node : root) {
                    String reportDate = parseFieldValue(node, "REPORT_DATE");
                    String itemName = parseFieldValue(node, "STD_ITEM_NAME");
                    String amount = parseFieldValue(node, "AMOUNT");
                    
                    if (reportDate == null || itemName == null) {
                        continue;
                    }
                    
                    // 获取或创建该报告期的现金流量表对象
                    StockFinancialHkCashFlow cashFlow = periodMap.computeIfAbsent(reportDate, k -> {
                        StockFinancialHkCashFlow cf = new StockFinancialHkCashFlow();
                        cf.setReportPeriod(reportDate);
                        cf.setStockCode(parseFieldValue(node, "SECURITY_CODE"));
                        cf.setStockName(parseFieldValue(node, "SECURITY_NAME_ABBR"));
                        return cf;
                    });
                    
                    // 根据项目名称映射到对应字段
                    mapCashFlowItem(cashFlow, itemName, amount);
                }
                
                result.addAll(periodMap.values());
            }
        } catch (Exception e) {
            System.err.println("解析港股现金流量表数据失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 映射现金流量表项目到实体字段
     */
    private void mapCashFlowItem(StockFinancialHkCashFlow cashFlow, String itemName, String amount) {
        if (itemName == null || amount == null) {
            return;
        }
        
        // 根据项目名称进行智能映射
        switch (itemName) {
            // ========== 经营活动现金流量 ==========
            case "除税前溢利(业务利润)":
                cashFlow.setProfitBeforeTax(amount);
                break;
            case "减:利息收入":
                cashFlow.setInterestIncome(amount);
                break;
            case "减:投资收益":
                cashFlow.setInvestmentIncome(amount);
                break;
            case "减:应占附属公司溢利":
                cashFlow.setShareOfSubsidiaryProfit(amount);
                break;
            case "加:减值及拨备":
                cashFlow.setImpairmentAndProvisions(amount);
                break;
            case "减:出售资产之溢利":
                cashFlow.setGainOnDisposalOfAssets(amount);
                break;
            case "加:折旧及摊销":
                cashFlow.setDepreciationAndAmortization(amount);
                break;
            case "减:汇兑收益":
                cashFlow.setExchangeGain(amount);
                break;
            case "加:经营调整其他项目":
                cashFlow.setOtherOperatingAdjustments(amount);
                break;
            case "营运资金变动前经营溢利":
                cashFlow.setOperatingProfitBeforeWorkingCapital(amount);
                break;
            case "存货(增加)减少":
                cashFlow.setInventoryChange(amount);
                break;
            case "应收帐款减少":
                cashFlow.setAccountsReceivableDecrease(amount);
                break;
            case "应付帐款及应计费用增加(减少)":
                cashFlow.setAccountsPayableAndAccrualsChange(amount);
                break;
            case "营运资本变动其他项目":
                cashFlow.setOtherWorkingCapitalChanges(amount);
                break;
            case "预付款项、按金及其他应收款项减少(增加)":
                cashFlow.setPrepaymentsAndOtherReceivablesChange(amount);
                break;
            case "预收账款、按金及其他应付款增加(减少)":
                cashFlow.setAdvanceReceiptsAndOtherPayablesChange(amount);
                break;
            case "递延收入(增加)减少":
                cashFlow.setDeferredRevenueChange(amount);
                break;
            case "存款(增加)减少":
                cashFlow.setDepositsChange(amount);
                break;
            case "经营产生现金":
                cashFlow.setOperatingCashGenerated(amount);
                break;
            case "已付税项":
                cashFlow.setTaxPaid(amount);
                break;
            case "经营业务现金净额":
                cashFlow.setNetOperatingCashFlow(amount);
                break;
                
            // ========== 投资活动现金流量 ==========
            case "已收利息(投资)":
                cashFlow.setInterestReceived(amount);
                break;
            case "已收股息(投资)":
                cashFlow.setDividendsReceived(amount);
                break;
            case "存款减少(增加)":
                cashFlow.setDepositsDecrease(amount);
                break;
            case "处置固定资产":
                cashFlow.setDisposalOfFixedAssets(amount);
                break;
            case "购建固定资产":
                cashFlow.setPurchaseOfFixedAssets(amount);
                break;
            case "购建无形资产及其他资产":
                cashFlow.setPurchaseOfIntangibleAssets(amount);
                break;
            case "出售附属公司":
                cashFlow.setDisposalOfSubsidiaries(amount);
                break;
            case "收购附属公司":
                cashFlow.setAcquisitionOfSubsidiaries(amount);
                break;
            case "收回投资所得现金":
                cashFlow.setProceedsFromInvestment(amount);
                break;
            case "投资支付现金":
                cashFlow.setInvestmentPayments(amount);
                break;
            case "应收关联方款项(增加)减少(投资)":
                cashFlow.setRelatedPartyReceivablesChange(amount);
                break;
            case "投资业务其他项目":
                cashFlow.setOtherInvestingActivities(amount);
                break;
            case "投资业务现金净额":
                cashFlow.setNetInvestingCashFlow(amount);
                break;
            case "融资前现金净额":
                cashFlow.setNetCashBeforeFinancing(amount);
                break;
                
            // ========== 筹资活动现金流量 ==========
            case "新增借款":
                cashFlow.setNewBorrowings(amount);
                break;
            case "偿还借款":
                cashFlow.setRepaymentOfBorrowings(amount);
                break;
            case "已付股息(融资)":
                cashFlow.setDividendsPaid(amount);
                break;
            case "吸收投资所得":
                cashFlow.setProceedsFromInvestmentReceived(amount);
                break;
            case "发行股份":
                cashFlow.setProceedsFromIssueOfShares(amount);
                break;
            case "发行相关费用":
                cashFlow.setShareIssueExpenses(amount);
                break;
            case "回购股份":
                cashFlow.setRepurchaseOfShares(amount);
                break;
            case "发行债券":
                cashFlow.setProceedsFromIssueOfBonds(amount);
                break;
            case "购买子公司少数股权而支付的现金":
                cashFlow.setPurchaseOfMinorityInterests(amount);
                break;
            case "融资业务其他项目":
                cashFlow.setOtherFinancingActivities(amount);
                break;
            case "融资业务现金净额":
                cashFlow.setNetFinancingCashFlow(amount);
                break;
                
            // ========== 现金净额及期末余额 ==========
            case "现金净额":
                cashFlow.setNetCashFlow(amount);
                break;
            case "期初现金":
                cashFlow.setCashAtBeginningOfPeriod(amount);
                break;
            case "期间变动其他项目":
                cashFlow.setOtherPeriodChanges(amount);
                break;
            case "期末现金":
                cashFlow.setCashAtEndOfPeriod(amount);
                break;
                
            // ========== 其他字段 ==========
            case "非运算项目":
                cashFlow.setNonOperatingItems(amount);
                break;
                
            // ========== 兼容性字段（保持原有字段名） ==========
            case "调整项目":
                cashFlow.setAdjustments(amount);
                break;
            case "期初现金及现金等价物":
                cashFlow.setCashAndCashEquivalentsAtBeginningOfPeriod(amount);
                break;
            case "期末现金及现金等价物":
                cashFlow.setCashAndCashEquivalentsAtEndOfPeriod(amount);
                break;
            case "所得税支出":
                cashFlow.setIncomeTaxPaid(amount);
                break;
            case "利息支出":
                cashFlow.setInterestExpense(amount);
                break;
            case "筹资活动产生的现金流量净额":
                cashFlow.setNetCashFromFinancingActivities(amount);
                break;
            case "投资活动产生的现金流量净额":
                cashFlow.setNetCashFromInvestingActivities(amount);
                break;
            case "经营活动产生的现金流量净额":
                cashFlow.setNetCashFromOperatingActivities(amount);
                break;
            case "现金及现金等价物净增加额":
                cashFlow.setNetIncreaseInCashAndCashEquivalents(amount);
                break;
            case "非现金投资及筹资活动":
                cashFlow.setNonCashInvestingAndFinancingActivities(amount);
                break;
            case "营运资金变动前的经营现金流量":
                cashFlow.setOperatingCashFlowBeforeWorkingCapital(amount);
                break;
            case "支付股息":
                cashFlow.setPaymentOfDividends(amount);
                break;
            case "取得借款":
                cashFlow.setProceedsFromBorrowings(amount);
                break;
            case "出售无形资产":
                cashFlow.setProceedsFromSaleOfIntangibleAssets(amount);
                break;
            case "出售投资物业":
                cashFlow.setProceedsFromSaleOfInvestmentProperty(amount);
                break;
            case "出售其他投资":
                cashFlow.setProceedsFromSaleOfOtherInvestments(amount);
                break;
            case "出售物业、厂房及设备":
                cashFlow.setProceedsFromSaleOfPropertyPlantAndEquipment(amount);
                break;
            case "购买投资物业":
                cashFlow.setPurchaseOfInvestmentProperty(amount);
                break;
            case "购买其他投资":
                cashFlow.setPurchaseOfOtherInvestments(amount);
                break;
            case "购买物业、厂房及设备":
                cashFlow.setPurchaseOfPropertyPlantAndEquipment(amount);
                break;
            case "偿还债券":
                cashFlow.setRepaymentOfBonds(amount);
                break;
            case "营运资金变动":
                cashFlow.setWorkingCapitalChanges(amount);
                break;
            default:
                // 对于未映射的项目，可以记录日志或忽略
                break;
        }
    }

    /**
     * 获取港股基本信息
     * @param stock 港股代码（5位数字）
     * @return StockHkBasicInfo对象，若无数据返回空对象
     */
    public StockHkBasicInfo getStockHkBasicInfo(String stock) {
        System.out.println("开始获取港股基本信息，股票代码: " + stock);
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(stock);
        System.out.println("请求参数: symbol=" + stock);
        String json = akShareClient.fetchData("stock_hk_company_profile_em", request);
        if (json == null) return null;
        try {
            JsonNode root = objectMapper.readTree(json);
            StockHkBasicInfo info = new StockHkBasicInfo();
            if (root.isArray() && root.size() > 0) {
                // 新接口返回的是数组，取第一个元素
                JsonNode data = root.get(0);
                if (data != null) {
                    // 根据新接口的字段映射
                    info.setComunic(getJsonValue(data, "注册地")); // 使用注册地作为公司注册号
                    info.setComcnname(getJsonValue(data, "公司名称"));
                    info.setComenname(getJsonValue(data, "英文名称"));
                    info.setRgiofc(getJsonValue(data, "注册地址"));
                    info.setHofclctmbu(getJsonValue(data, "办公地址"));
                    info.setChairman(getJsonValue(data, "董事长"));
                    info.setComintr(getJsonValue(data, "公司介绍")); // 公司简介
                    info.setIncdate(parseDateToTimestamp(getJsonValue(data, "公司成立日期")));
                    info.setIndustry(getJsonValue(data, "所属行业"));
                    info.setLegalRepresentative(getJsonValue(data, "董事长")); // 港股通常董事长就是法定代表人
                    info.setRegisteredCapital(getJsonValue(data, "员工人数")); // 使用员工人数作为注册资本（临时）
                    info.setWebsite(getJsonValue(data, "公司网址"));
                    info.setEmail(getJsonValue(data, "E-MAIL"));
                    info.setPhone(getJsonValue(data, "联系电话"));
                }
            }
            return info;
        } catch (Exception e) {
            System.err.println("解析港股基本信息失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 从JsonNode中安全获取字符串值
     */
    private String getJsonValue(JsonNode node, String fieldName) {
        if (node.has(fieldName)) {
            JsonNode valueNode = node.get(fieldName);
            return valueNode.isNull() ? null : valueNode.asText();
        }
        return null;
    }

    /**
     * 解析日期字符串为时间戳
     */
    private Long parseDateToTimestamp(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            // 解析 "1999-11-23" 格式的日期
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            // 使用UTC时区避免时区问题
            return date.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取股东户数数据
     * @param symbol 股票代码
     * @return 股东户数数据列表
     */
    public List<StockShareholderData> getStockShareholderData(String symbol) {
        List<StockShareholderData> result = new ArrayList<>();
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        String json = akShareClient.fetchData("stock_zh_a_gdhs_detail_em", request);
        
        if (json == null) {
            return result;
        }
        
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockShareholderData item = new StockShareholderData();
                    item.setSymbol(parseFieldValue(node, "代码"));
                    item.setName(parseFieldValue(node, "名称"));
                    item.setStatisticalDate(parseFieldValue(node, "股东户数统计截止日"));
                    item.setShareholderCount(parseFieldValue(node, "股东户数-本次"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("解析股东户数数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取港股历史换手率数据
     * @param symbol 港股代码（5位数字）
     * @return 换手率数据列表
     */
    public List<StockTurnoverData> getStockHkHistTurnover(String symbol) {
        System.out.println("开始获取港股历史换手率数据，股票代码: " + symbol);
        List<StockTurnoverData> result = new ArrayList<>();
        
        AKShareRequest request = new AKShareRequest();
        request.setSymbol(symbol);
        request.setPeriod("daily");
        
        System.out.println("请求参数: symbol=" + symbol + ", period=daily");
        String json = akShareClient.fetchData("stock_hk_hist", request);
        
        if (json == null) {
            System.err.println("获取港股历史换手率数据失败: 返回数据为空");
            return result;
        }
        
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    StockTurnoverData item = new StockTurnoverData();
                    item.setSymbol(symbol);
                    
                    // 解析日期
                    String dateStr = getJsonValue(node, "日期");
                    if (dateStr != null) {
                        item.setDate(dateStr);
                    }
                    
                    // 解析换手率
                    String turnoverStr = getJsonValue(node, "换手率");
                    if (turnoverStr != null && !turnoverStr.trim().isEmpty()) {
                        item.setTurnover(turnoverStr);
                    }
                    
                    // 只添加有换手率数据的记录
                    if (item.getTurnover() != null) {
                        result.add(item);
                    }
                }
            }
            
            System.out.println("成功获取港股历史换手率数据，共 " + result.size() + " 条记录");
            return result;
            
        } catch (Exception e) {
            System.err.println("解析港股历史换手率数据失败: " + e.getMessage());
            e.printStackTrace();
            return result;
        }
    }

} 