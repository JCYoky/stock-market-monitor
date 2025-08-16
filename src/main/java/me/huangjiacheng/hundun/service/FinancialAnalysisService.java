package me.huangjiacheng.hundun.service;

import me.huangjiacheng.hundun.model.StockFinancialDebtThs;
import me.huangjiacheng.hundun.model.StockFinancialBenefitThs;
import me.huangjiacheng.hundun.model.StockFinancialCashThs;
import me.huangjiacheng.hundun.model.StockValuationData;
import me.huangjiacheng.hundun.model.StockTurnoverData;
import me.huangjiacheng.hundun.model.StockFinancialHkBalanceSheet;
import me.huangjiacheng.hundun.model.StockFinancialHkIncomeStatement;
import me.huangjiacheng.hundun.model.StockFinancialHkCashFlow;
import me.huangjiacheng.hundun.model.StockWatchlist;
import me.huangjiacheng.hundun.model.StockInfo;
import me.huangjiacheng.hundun.model.MarketRiskRatio;
import me.huangjiacheng.hundun.mapper.MarketRiskRatioMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 张新民结构性财务分析服务
 */
@Service
public class FinancialAnalysisService {
    @Autowired
    private StockFinancialDataService stockFinancialDataService;

    @Autowired
    private AKShareService akShareService;

    @Autowired
    private StockWatchlistService stockWatchlistService;
    
    @Autowired
    private MarketRiskRatioMapper marketRiskRatioMapper;

    /**
     * 综合分析上市公司财务状况，包括负债结构和资产结构
     * 
     * @param symbol 股票代码
     * @param stockName 股票名称
     * @return Map: { debtAnalysis, assetAnalysis }
     */
    public Map<String, Object> analyzeFinancialStructure(String symbol, String stockName) {
        Map<String, Object> result = new HashMap<>();
        result.put("symbol", symbol);

        // 只获取一次财务数据
        Map<String, Object> financialData = stockFinancialDataService.getStockFinancialDataByPeriod(symbol);
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> periodData = (Map<String, Map<String, Object>>) financialData
                .get("periodData");

        if (periodData == null || periodData.isEmpty()) {
            result.put("success", false);
            result.put("message", "未找到该股票的财务数据");
            return result;
        }

        // 分析资产负债结构
        Map<String, Map<String, Double>> debtRatios = new TreeMap<>(Collections.reverseOrder());
        Map<String, Map<String, Double>> assetRatios = new TreeMap<>(Collections.reverseOrder());
        Map<String, Map<String, Double>> bloodRatios = new TreeMap<>(Collections.reverseOrder()); // 造血能力和输血能力占比
        // 利润表分析
        Map<String, Map<String, Double>> profitData = new TreeMap<>(Collections.reverseOrder()); // 利润表数据
        Map<String, Map<String, Double>> profitRatios = new TreeMap<>(Collections.reverseOrder()); // 利润比率
        Map<String, Map<String, Double>> financialRatios = new TreeMap<>(Collections.reverseOrder()); // 财务比率

        for (Map.Entry<String, Map<String, Object>> periodEntry : periodData.entrySet()) {
            String period = periodEntry.getKey();
            Map<String, Object> data = periodEntry.getValue();
            StockFinancialDebtThs debt = (StockFinancialDebtThs) data.get("balanceSheet");
            StockFinancialBenefitThs benefit = (StockFinancialBenefitThs) data.get("incomeStatement");
            StockFinancialCashThs cashFlow = (StockFinancialCashThs) data.get("cashFlowStatement");
            if (debt == null)
                continue;

            // 解析基础数据
            double totalAssets = parseFinancialValue(debt.getAssetTotal());// 总资产
            double totalDebt = parseFinancialValue(debt.getDebtTotal());// 总负债
            // 负债相关数据
            double currentLiabilities = parseFinancialValue(debt.getCurrentLiabilitiesTotal());// 流动负债合计
            double nonCurrentLiabilities = parseFinancialValue(debt.getNonCurrentLiabilitiesTotal());// 非流动负债合计
            // 负债相关数据-经营性负债项目
            double payables = parseFinancialValue(debt.getPayables());// 应付账款+应付票据
            double advanceReceipts = parseFinancialValue(debt.getAdvanceReceipts());// 预收账款
            double contractLiabilities = parseFinancialValue(debt.getContractLiabilities());// 合同负债
            double payableStaffSalaries = parseFinancialValue(debt.getPayableStaffSalaries());// 应付职工薪酬
            double taxesPayable = parseFinancialValue(debt.getTaxesPayable());// 应交税费
            // 资产相关数据
            double currentAssets = parseFinancialValue(debt.getCurrentAssetsTotal()); // 流动资产合计
            double cash = parseFinancialValue(debt.getCash());// 货币资金
            double lendingFunds = parseFinancialValue(debt.getLendingFunds());// 拆出资金
            double tradingFinancialAssets = parseFinancialValue(debt.getTradingFinancialAssets());// 交易性金融资产
            double receivables = parseFinancialValue(debt.getReceivables());// 应收账款+应收票据
            double prepayments = parseFinancialValue(debt.getPrepayments());// 预付款项
            double inventory = parseFinancialValue(debt.getInventory());// 存货
            double fixedAssets = parseFinancialValue(debt.getFixedAssetsTotal()); // 固定资产合计
            double constructionInProgress = parseFinancialValue(debt.getConstructionInProgressTotal());// 在建工程合计
            double projectMaterials = parseFinancialValue(debt.getProjectMaterials());// 工程物资
            double otherEquityInvestments = parseFinancialValue(debt.getOtherEquityInvestments());// 其他权益工具投资
            double otherNonCurrentFinancialAssets = parseFinancialValue(debt.getOtherNonCurrentFinancialAssets());// 其他非流动金融资产
            double investmentProperty = parseFinancialValue(debt.getInvestmentProperty());// 投资性房地产
            double goodwill = parseFinancialValue(debt.getGoodwill()); // 商誉
            double intangibleAssets = parseFinancialValue(debt.getIntangibleAssets());// 无形资产
            double longTermEquityInvestments = parseFinancialValue(debt.getLongTermEquityInvestments());// 长期股权投资
            double availableForSaleFinancialAssets = parseFinancialValue(debt.getAvailableForSaleFinancialAssets());// 可供出售金融资产
            double heldToMaturityInvestments = parseFinancialValue(debt.getHeldToMaturityInvestments());// 持有至到期投资
            double totalCash = parseFinancialValue(debt.getTotalCash());// 总现金
            // 所有者权益相关数据
            double equity = parseFinancialValue(debt.getEquityTotal());// 所有者权益合计
            double retainedEarnings = parseFinancialValue(debt.getSurplusReserve());// 盈余公积
            double undistributedProfit = parseFinancialValue(debt.getUndistributedProfits());// 未分配利润
            double capitalReserve = parseFinancialValue(debt.getCapitalReserve());// 资本公积
            double paidInCapital = parseFinancialValue(debt.getPaidInCapital());// 股本

            // 计算负债比率
            Map<String, Double> debtRatioMap = new HashMap<>();
            // 1. 总负债率 = 总负债 / 总资产
            Double totalDebtRatio = (totalAssets > 0) ? totalDebt / totalAssets : null;
            // 2. 经营性负债率 = (应付账款+应付票据+预收账款+合同负债+应付职工薪酬+应交税费) / 总资产
            Double operatingDebt = payables
                    + advanceReceipts
                    + contractLiabilities
                    + payableStaffSalaries
                    + taxesPayable;
            Double operatingDebtRatio = (totalAssets > 0) ? operatingDebt / totalAssets : null;
            // 3. 金融性负债率 = (总负债 - 经营性负债) / 总资产
            Double financialDebt = totalDebt - operatingDebt;
            Double financialDebtRatio = (totalAssets > 0) ? financialDebt / totalAssets : null;
            // 4. 流动负债率 = 流动负债 / 总资产
            Double currentDebtRatio = (totalAssets > 0) ? currentLiabilities / totalAssets : null;
            // 5. 非流动负债率 = 非流动负债 / 总资产
            Double nonCurrentDebtRatio = (totalAssets > 0) ? nonCurrentLiabilities / totalAssets : null;

            debtRatioMap.put("totalDebtRatio", totalDebtRatio);
            debtRatioMap.put("operatingDebtRatio", operatingDebtRatio);
            debtRatioMap.put("financialDebtRatio", financialDebtRatio);
            debtRatioMap.put("currentDebtRatio", currentDebtRatio);
            debtRatioMap.put("nonCurrentDebtRatio", nonCurrentDebtRatio);

            // 资产结构分析 - 动态分析所有资产项目
            Map<String, Double> assetRatioMap = new HashMap<>();
            assetRatioMap.put("货币资金（含拆出资金）占比", (cash + lendingFunds) / totalAssets);
            assetRatioMap.put("存货占比", inventory / totalAssets);
            assetRatioMap.put("固定资产和在建工程占比", (fixedAssets + constructionInProgress + projectMaterials) / totalAssets);
            assetRatioMap.put("两应收一预付占比", (receivables + prepayments) / totalAssets);
            assetRatioMap.put("商誉和无形资产占比", (goodwill + intangibleAssets) / totalAssets);
            assetRatioMap.put("流动资产占比", currentAssets / totalAssets);
            // 净现金比率 = 净现金 / 总资产
            Double netCashRatio = (totalCash + lendingFunds - totalDebt) / totalAssets;
            assetRatioMap.put("净现金比率", netCashRatio);
            double touZiXingZiChan = tradingFinancialAssets
                    + availableForSaleFinancialAssets
                    + heldToMaturityInvestments
                    + longTermEquityInvestments
                    + otherEquityInvestments
                    + otherNonCurrentFinancialAssets
                    + investmentProperty;
            assetRatioMap.put("投资性资产占比", touZiXingZiChan / totalAssets);

            if (benefit == null)
                continue;

            // 解析利润表数据
            double totalRevenue = parseFinancialValue(benefit.getTotalRevenue());// 营业总收入
            double revenue = parseFinancialValue(benefit.getRevenue());// 营业收入
            double cost = parseFinancialValue(benefit.getCost());// 营业成本
            double taxAndSurcharge = parseFinancialValue(benefit.getTaxAndSurcharge());// 营业税金及附加
            double sellingExpense = parseFinancialValue(benefit.getSellingExpense());// 销售费用
            double adminExpense = parseFinancialValue(benefit.getAdminExpense());// 管理费用
            double rAndDExpense = parseFinancialValue(benefit.getRAndDExpense());// 研发费用
            double financialExpense = parseFinancialValue(benefit.getFinancialExpense());// 财务费用
            double interestIncome = parseFinancialValue(benefit.getInterestIncome());// 利息收入
            double assetImpairmentLoss = parseFinancialValue(benefit.getAssetImpairmentLoss());// 资产减值损失
            double creditImpairmentLoss = parseFinancialValue(benefit.getCreditImpairmentLoss());// 信用减值损失
            double fairValueIncome = parseFinancialValue(benefit.getFairValueIncome());// 公允价值变动收益
            double investmentIncome = parseFinancialValue(benefit.getInvestmentIncome());// 投资收益
            double assetDisposalIncome = parseFinancialValue(benefit.getAssetDisposalIncome());// 资产处置收益
            double otherIncome = parseFinancialValue(benefit.getOtherIncome());// 其他收益
            double nonOperatingIncome = parseFinancialValue(benefit.getNonOperatingIncome());// 营业外收入
            double nonCurrentAssetDisposalGain = parseFinancialValue(benefit.getNonCurrentAssetDisposalGain());// 非流动资产处置利得
            double nonOperatingExpense = parseFinancialValue(benefit.getNonOperatingExpense());// 营业外支出
            double nonCurrentAssetDisposalLoss = parseFinancialValue(benefit.getNonCurrentAssetDisposalLoss());// 非流动资产处置损失
            double netProfit = parseFinancialValue(benefit.getNetProfit());// 净利润

            // 解析现金流量表数据
            double netCashflowOperating = 0.0;

            if (cashFlow != null) {
                netCashflowOperating = parseFinancialValue(cashFlow.getNetOperatingCash());// 经营活动产生的现金流量净额
            }

            // 第一部分：利润表数据
            Map<String, Double> profitDataMap = new HashMap<>();
            // 第二部分：利润表分析
            Map<String, Double> profitRatioMap = new HashMap<>();
            // 第三部分：比率分析
            Map<String, Double> financialRatioMap = new HashMap<>();
            // 第四部分：造血能力和输血能力
            Map<String, Double> bloodRatioMap = new HashMap<>();

            // 收入项目
            profitDataMap.put("营业收入", revenue);
            profitDataMap.put("营业总收入", totalRevenue);
            // 毛利
            double grossProfit = revenue - cost;
            profitDataMap.put("毛利", grossProfit);
            // 毛利率
            double grossProfitRatio = grossProfit / revenue;
            profitRatioMap.put("毛利率", grossProfitRatio);
            // 主营业务利润（核心利润）=营业收入-营业成本-营业税金及附加-销售费用-管理费用-研发费用-财务费用
            double mainBusinessProfit = revenue - cost - taxAndSurcharge - sellingExpense - adminExpense - rAndDExpense
                    - financialExpense;
            profitDataMap.put("主营业务利润", mainBusinessProfit);
            // 净利润
            profitDataMap.put("净利润", netProfit);
            // 净利润占比
            double netProfitRatio = netProfit / revenue;
            profitRatioMap.put("净利率", netProfitRatio);
            // 核心利润率
            double mainProfitRatio = mainBusinessProfit / revenue;
            profitRatioMap.put("核心利润率", mainProfitRatio);
            // 投资性收益合计（jointInvestmentIncome 已包含在 investmentIncome 中）
            double investmentProfit = investmentIncome + interestIncome;
            profitDataMap.put("投资性收益", investmentProfit);
            // 投资性收益占比
            double investmentProfitRatio = investmentProfit / revenue;
            profitRatioMap.put("投资性收益占比", investmentProfitRatio);
            // 一次性损益合计（各项直接相加即可，含正负）
            double nonRecurringProfit = otherIncome
                    + assetDisposalIncome
                    + nonOperatingIncome
                    + nonCurrentAssetDisposalGain
                    + fairValueIncome
                    + nonOperatingExpense
                    + nonCurrentAssetDisposalLoss
                    + assetImpairmentLoss
                    + creditImpairmentLoss;
            profitDataMap.put("一次性损益", nonRecurringProfit);
            // 一次性损益占比
            double nonRecurringProfitRatio = nonRecurringProfit / revenue;
            profitRatioMap.put("一次性损益占比", nonRecurringProfitRatio);
            // 其他收益
            profitDataMap.put("其他收益", otherIncome);
            // 其他收益占比
            double otherIncomeRatio = otherIncome / revenue;
            profitRatioMap.put("其他收益占比", otherIncomeRatio);
            // 信用减值损失
            profitDataMap.put("信用减值损失", creditImpairmentLoss);
            // 信用减值损失占比
            double creditImpairmentLossRatio = creditImpairmentLoss / revenue;
            profitRatioMap.put("信用减值损失占比", creditImpairmentLossRatio);
            // 资产减值损失
            profitDataMap.put("资产减值损失", assetImpairmentLoss);
            // 资产减值损失占比
            double assetImpairmentLossRatio = assetImpairmentLoss / revenue;
            profitRatioMap.put("资产减值损失占比", assetImpairmentLossRatio);

            double sellingExpenseRatio = sellingExpense / revenue;
            profitRatioMap.put("销售费用率", sellingExpenseRatio);
            double adminExpenseRatio = adminExpense / revenue;
            profitRatioMap.put("管理费用率", adminExpenseRatio);
            double rAndDExpenseRatio = rAndDExpense / revenue;
            profitRatioMap.put("研发费用率", rAndDExpenseRatio);
            double financialExpenseRatio = financialExpense / revenue;
            profitRatioMap.put("财务费用率", financialExpenseRatio);

            // 核心利润获现率
            double mainProfitCashRatio = netCashflowOperating / mainBusinessProfit;
            financialRatioMap.put("核心利润获现率", mainProfitCashRatio);
            double liuDongBiLv = currentAssets / currentLiabilities;
            financialRatioMap.put("流动比率", liuDongBiLv);
            double xianJinBiLv = totalCash / currentLiabilities;
            financialRatioMap.put("现金比率", xianJinBiLv);
            double roe = equity != 0 ? netProfit / equity : 0.0;
            financialRatioMap.put("净资产收益率", roe);

            // 造血能力=经营性负债+盈余公积+未分配利润
            double earnedBlood = operatingDebt + retainedEarnings + undistributedProfit;
            // 造血能力占比
            double earnedBloodRatio = earnedBlood / totalAssets;
            bloodRatioMap.put("造血能力占比", earnedBloodRatio);
            // 输血能力 = 金融性负债+资本公积+股本
            double givedBlood = financialDebt + capitalReserve + paidInCapital;
            // 输血能力占比
            double givedBloodRatio = givedBlood / totalAssets;
            bloodRatioMap.put("输血能力占比", givedBloodRatio);
            // 报告期维度数据，每一个是一个折线图
            debtRatios.put(period, debtRatioMap);
            assetRatios.put(period, assetRatioMap);
            profitData.put(period, profitDataMap);
            profitRatios.put(period, profitRatioMap);
            financialRatios.put(period, financialRatioMap);
            bloodRatios.put(period, bloodRatioMap);
        }

        // 构建返回结果
        Map<String, Object> debtAnalysis = new HashMap<>();
        debtAnalysis.put("symbol", symbol);
        debtAnalysis.put("yearlyRatios", debtRatios);
        debtAnalysis.put("success", true);

        Map<String, Object> assetAnalysis = new HashMap<>();
        assetAnalysis.put("symbol", symbol);
        assetAnalysis.put("yearlyRatios", assetRatios);
        assetAnalysis.put("success", true);

        Map<String, Object> profitDataAnalysis = new HashMap<>();
        profitDataAnalysis.put("symbol", symbol);
        profitDataAnalysis.put("yearlyRatios", profitData);
        profitDataAnalysis.put("success", true);

        Map<String, Object> profitRatioAnalysis = new HashMap<>();
        profitRatioAnalysis.put("symbol", symbol);
        profitRatioAnalysis.put("yearlyRatios", profitRatios);
        profitRatioAnalysis.put("success", true);

        Map<String, Object> financialRatioAnalysis = new HashMap<>();
        financialRatioAnalysis.put("symbol", symbol);
        financialRatioAnalysis.put("yearlyRatios", financialRatios);
        financialRatioAnalysis.put("success", true);

        Map<String, Object> bloodRatioAnalysis = new HashMap<>();
        bloodRatioAnalysis.put("symbol", symbol);
        bloodRatioAnalysis.put("yearlyRatios", bloodRatios);
        bloodRatioAnalysis.put("success", true);

        result.put("debtAnalysis", debtAnalysis);
        result.put("assetAnalysis", assetAnalysis);
        result.put("profitDataAnalysis", profitDataAnalysis);
        result.put("profitRatioAnalysis", profitRatioAnalysis);
        result.put("financialRatioAnalysis", financialRatioAnalysis);
        result.put("bloodRatioAnalysis", bloodRatioAnalysis);

        // 获取市盈率数据
        List<StockValuationData> valuationDataList = akShareService.getStockValuationData(symbol);
        System.out.println("valuationDataList个数: " + valuationDataList.size());
        Map<String, Object> valuationAnalysis = new HashMap<>();
        valuationAnalysis.put("symbol", symbol);
        valuationAnalysis.put("valuationData", valuationDataList);
        valuationAnalysis.put("success", true);
        result.put("valuationAnalysis", valuationAnalysis);

        result.put("success", true);

        // 构建StockWatchlist对象，使用最新报告期的数据
        try {
            // 获取最新报告期的数据
            String latestPeriod = periodData.keySet().iterator().next(); // 由于使用TreeMap倒序，第一个就是最新的
            Map<String, Object> latestData = periodData.get(latestPeriod);
            
            if (latestData != null) {
                StockFinancialDebtThs latestBalanceSheet = (StockFinancialDebtThs) latestData.get("balanceSheet");
                StockFinancialBenefitThs latestIncomeStatement = (StockFinancialBenefitThs) latestData.get("incomeStatement");
                
                if (latestBalanceSheet != null && latestIncomeStatement != null) {
                    // 构建StockWatchlist对象
                    StockWatchlist stockWatchlist = new StockWatchlist();
                    stockWatchlist.setStockCode(symbol);
                    stockWatchlist.setStockName(stockName);
                    stockWatchlist.setStockType(0); // 默认类型为0，后续业务逻辑会设置
                    
                    // 设置财务指标（使用最新报告期数据）
                    setASharePeTtm(stockWatchlist, valuationDataList);
                    setAShareRoe(stockWatchlist, symbol);
                    setAShareProfitQuality(stockWatchlist, periodData);
                    setAShareAssetsQuality(stockWatchlist, assetRatios, latestPeriod);
                    setASharePeScore(stockWatchlist, valuationDataList);
                    
                    System.out.println("构建StockWatchlist对象: " + symbol + " - " + stockName + ", peTtm: " + stockWatchlist.getPeTtm() + ", roe: " + stockWatchlist.getRoe() + ", profitQuality: " + stockWatchlist.getProfitQuality() + ", assetsQuality: " + stockWatchlist.getAssetsQuality() + ", peScore: " + stockWatchlist.getPeScore());
                    
                    // 保存StockWatchlist对象到数据库
                    try {
                        // 检查是否已存在
                        StockWatchlist existingStock = stockWatchlistService.getStockByCode(symbol);
                        if (existingStock != null) {
                            // 更新：保持原有StockType不变
                            stockWatchlist.setStockType(existingStock.getStockType());
                            stockWatchlist.setId(existingStock.getId());
                            stockWatchlist.setCreatedTime(existingStock.getCreatedTime());
                            stockWatchlist.setUpdatedTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                            
                            stockWatchlistService.updateStock(stockWatchlist);
                            System.out.println("更新StockWatchlist成功: " + symbol + ", 保持原有类型: " + existingStock.getStockType());
                        } else {
                            // 新增：StockType设为0
                            stockWatchlist.setStockType(0);
                            stockWatchlist.setCreatedTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                            stockWatchlist.setUpdatedTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                            
                            stockWatchlistService.addStock(stockWatchlist);
                            System.out.println("新增StockWatchlist成功: " + symbol + ", 类型设为: 0");
                        }
                    } catch (Exception e) {
                        System.err.println("保存StockWatchlist失败: " + symbol + " - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            // 构建StockWatchlist失败不影响主要分析结果
            System.err.println("构建StockWatchlist对象失败: " + e.getMessage());
        }

        return result;
    }

    // 解析财务数值，支持单位和异常处理
    private double parseFinancialValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        String cleanValue = value.trim();
        if ("false".equalsIgnoreCase(cleanValue) || "true".equalsIgnoreCase(cleanValue)) {
            return 0.0;
        }
        cleanValue = cleanValue.replaceAll("[^0-9.亿万千]", "");
        if (cleanValue.isEmpty()) {
            return 0.0;
        }
        try {
            if (cleanValue.contains("万亿")) {
                String numStr = cleanValue.replace("万亿", "");
                return Double.parseDouble(numStr) * 1000000000000L;
            } else if (cleanValue.contains("亿")) {
                String numStr = cleanValue.replace("亿", "");
                return Double.parseDouble(numStr) * 100000000;
            } else if (cleanValue.contains("万")) {
                String numStr = cleanValue.replace("万", "");
                return Double.parseDouble(numStr) * 10000;
            } else if (cleanValue.contains("千")) {
                String numStr = cleanValue.replace("千", "");
                return Double.parseDouble(numStr) * 1000;
            } else {
                return Double.parseDouble(cleanValue);
            }
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * 分析股票换手率分布
     * 
     * @param symbol 股票代码
     * @return 换手率分布分析结果
     */
    public Map<String, Object> analyzeTurnoverDistribution(String symbol) {
        Map<String, Object> result = new HashMap<>();
        result.put("symbol", symbol);

        try {
            // 1. 获取股票的换手率历史数据
            List<StockTurnoverData> turnoverDataList = akShareService.getStockTurnoverData(symbol);

            if (turnoverDataList == null || turnoverDataList.isEmpty()) {
                result.put("success", false);
                result.put("message", "未找到该股票的换手率数据");
                return result;
            }

            // 2. 将换手率从大到小进行排序
            List<Double> turnoverValues = new ArrayList<>();
            String latestTurnover = null;
            String latestDate = null;

            for (StockTurnoverData data : turnoverDataList) {
                String turnoverStr = data.getTurnover();
                if (turnoverStr != null && !turnoverStr.trim().isEmpty()) {
                    try {
                        double turnover = Double.parseDouble(turnoverStr);
                        turnoverValues.add(turnover);

                        // 记录最新的换手率（数据按时间正序，最新的在最后）
                        if (data.getDate() != null) {
                            latestTurnover = turnoverStr;
                            latestDate = data.getDate();
                        }
                    } catch (NumberFormatException e) {
                        // 忽略无法解析的数据
                        continue;
                    }
                }
            }

            if (turnoverValues.isEmpty()) {
                result.put("success", false);
                result.put("message", "换手率数据解析失败");
                return result;
            }

            // 从大到小排序
            Collections.sort(turnoverValues, Collections.reverseOrder());

            // 3. 将全部换手率分割成1000个区间，统计落入每个区间换手率的日期的个数
            double minTurnover = turnoverValues.get(turnoverValues.size() - 1); // 最小值
            double maxTurnover = turnoverValues.get(0); // 最大值
            double rangeSize = (maxTurnover - minTurnover) / 1000.0; // 每个区间的大小

            int[] intervalCounts = new int[1000]; // 每个区间的计数
            int latestIntervalIndex = -1; // 最新换手率所在的区间索引

            // 统计每个区间的数据个数
            for (double turnover : turnoverValues) {
                int intervalIndex = (int) Math.floor((turnover - minTurnover) / rangeSize);
                if (intervalIndex >= 1000) {
                    intervalIndex = 999; // 防止越界
                }
                intervalCounts[intervalIndex]++;
            }

            // 找到最新换手率所在的区间
            if (latestTurnover != null) {
                try {
                    double latestTurnoverValue = Double.parseDouble(latestTurnover);
                    latestIntervalIndex = (int) Math.floor((latestTurnoverValue - minTurnover) / rangeSize);
                    if (latestIntervalIndex >= 1000) {
                        latestIntervalIndex = 999;
                    }
                } catch (NumberFormatException e) {
                    latestIntervalIndex = -1;
                }
            }

            // 构建区间标签
            List<String> intervalLabels = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                double startValue = minTurnover + i * rangeSize;
                double endValue = minTurnover + (i + 1) * rangeSize;
                String label = String.format("%.2f-%.2f", startValue, endValue);
                intervalLabels.add(label);
            }

            // 构建返回结果
            result.put("success", true);
            result.put("intervalCounts", intervalCounts);
            result.put("intervalLabels", intervalLabels);
            result.put("latestTurnover", latestTurnover);
            result.put("latestDate", latestDate);
            result.put("latestIntervalIndex", latestIntervalIndex);
            result.put("minTurnover", minTurnover);
            result.put("maxTurnover", maxTurnover);
            result.put("totalDays", turnoverValues.size());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "分析换手率分布时发生错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 港股综合分析
     * 
     * @param stock 股票代码（5位数字）
     * @return 综合分析结果
     */
    public Map<String, Object> analyzeHkFinancialStructure(String stock, String stockName) {
        Map<String, Object> result = new HashMap<>();
        result.put("stock", stock);

        try {
            // 获取三张报表数据
            List<StockFinancialHkBalanceSheet> balanceSheets = akShareService.getStockFinancialHkBalanceSheet(stock);
            List<StockFinancialHkIncomeStatement> incomeStatements = akShareService
                    .getStockFinancialHkIncomeStatement(stock);
            List<StockFinancialHkCashFlow> cashFlows = akShareService.getStockFinancialHkCashFlow(stock);

            if (balanceSheets.isEmpty()) {
                result.put("success", false);
                result.put("message", "未找到该港股的财务数据");
                return result;
            }

            // 按报告期组织数据
            Map<String, Map<String, Object>> periodData = new TreeMap<>(Collections.reverseOrder());

            // 处理资产负债表数据
            for (StockFinancialHkBalanceSheet balanceSheet : balanceSheets) {
                String reportPeriod = balanceSheet.getReportPeriod();
                if (reportPeriod != null && !reportPeriod.trim().isEmpty()) {
                    if (!periodData.containsKey(reportPeriod)) {
                        periodData.put(reportPeriod, new HashMap<>());
                    }
                    periodData.get(reportPeriod).put("balanceSheet", balanceSheet);
                }
            }

            // 处理利润表数据
            for (StockFinancialHkIncomeStatement incomeStatement : incomeStatements) {
                String reportPeriod = incomeStatement.getReportPeriod();
                if (reportPeriod != null && !reportPeriod.trim().isEmpty()) {
                    if (periodData.containsKey(reportPeriod)) {
                        periodData.get(reportPeriod).put("incomeStatement", incomeStatement);
                    }
                }
            }

            // 处理现金流量表数据
            for (StockFinancialHkCashFlow cashFlow : cashFlows) {
                String reportPeriod = cashFlow.getReportPeriod();
                if (reportPeriod != null && !reportPeriod.trim().isEmpty()) {
                    if (periodData.containsKey(reportPeriod)) {
                        periodData.get(reportPeriod).put("cashFlow", cashFlow);
                    }
                }
            }

            // 分析负债结构
            Map<String, Map<String, Double>> debtRatios = new TreeMap<>(Collections.reverseOrder());
            // to-do 分析资产结构
            Map<String, Map<String, Double>> assetRatios = new TreeMap<>(Collections.reverseOrder());
            Map<String, Map<String, Double>> profitData = new TreeMap<>(Collections.reverseOrder());
            Map<String, Map<String, Double>> profitRatios = new TreeMap<>(Collections.reverseOrder());
            Map<String, Map<String, Double>> financialRatios = new TreeMap<>(Collections.reverseOrder());
            Map<String, Map<String, Double>> cashFlowData = new TreeMap<>(Collections.reverseOrder());

            for (Map.Entry<String, Map<String, Object>> periodEntry : periodData.entrySet()) {
                String period = periodEntry.getKey();
                Map<String, Object> data = periodEntry.getValue();

                StockFinancialHkBalanceSheet balanceSheet = (StockFinancialHkBalanceSheet) data.get("balanceSheet");
                StockFinancialHkIncomeStatement incomeStatement = (StockFinancialHkIncomeStatement) data
                        .get("incomeStatement");
                StockFinancialHkCashFlow cashFlow = (StockFinancialHkCashFlow) data.get("cashFlow");

                if (balanceSheet == null)
                    continue;

                // 解析资产负债表数据
                double totalAssets = parseDoubleValue(balanceSheet.getZongZiChan());
                double totalLiabilities = parseDoubleValue(balanceSheet.getZongFuZhai());
                double cashAndEquivalents = parseDoubleValue(balanceSheet.getXianJinJiDengJiaWu());
                double shouXianZhiCunKuanJiXianJin = parseDoubleValue(balanceSheet.getShouXianZhiCunKuanJiXianJin());
                double inventory = parseDoubleValue(balanceSheet.getCunHuo());
                double receivables = parseDoubleValue(balanceSheet.getYingShouZhangKuan());
                double payables = parseDoubleValue(balanceSheet.getYingFuZhangKuan());
                double currentLiabilities = parseDoubleValue(balanceSheet.getLiuDongFuZhaiHeJi());
                double nonCurrentLiabilities = parseDoubleValue(balanceSheet.getFeiLiuDongFuZhaiHeJi());
                double yingFuZhangKuan = parseDoubleValue(balanceSheet.getYingFuZhangKuan());
                double yingFuPiaoJu = parseDoubleValue(balanceSheet.getYingFuPiaoJu());
                double qiTaYingFuKuanJiYingJiFeiYong = parseDoubleValue(
                        balanceSheet.getQiTaYingFuKuanJiYingJiFeiYong());
                double yingFuGuanLianFangKuanXiangLiuDong = parseDoubleValue(
                        balanceSheet.getYingFuGuanLianFangKuanXiangLiuDong());
                double diYanShouRuLiuDong = parseDoubleValue(balanceSheet.getDiYanShouRuLiuDong());
                double diYanShouRuFeiLiuDong = parseDoubleValue(balanceSheet.getDiYanShouRuFeiLiuDong());
                double yingFuShuiXiang = parseDoubleValue(balanceSheet.getYingFuShuiXiang());
                double rongZiZuLinFuZhaiFeiLiuDong = parseDoubleValue(balanceSheet.getRongZiZuLinFuZhaiFeiLiuDong());
                double rongZiZuLinFuZhaiLiuDong = parseDoubleValue(balanceSheet.getRongZiZuLinFuZhaiLiuDong());
                double heTongFuZhai = parseDoubleValue(balanceSheet.getHeTongFuZhai());
                double shangyu = parseDoubleValue(balanceSheet.getShangYu());
                double wuXingZiChan = parseDoubleValue(balanceSheet.getWuXingZiChan());
                double guDingZiChan = parseDoubleValue(balanceSheet.getGuDingZiChan());
                double zaiJianGongCheng = parseDoubleValue(balanceSheet.getZaiJianGongCheng());
                double duanQiCunKuan = parseDoubleValue(balanceSheet.getDuanQiCunKuan());
                double jiaoYiXingJinRongZiChanLiuDong = parseDoubleValue(
                        balanceSheet.getJiaoYiXingJinRongZiChanLiuDong());
                double qiTaJinRongZiChanLiuDong = parseDoubleValue(balanceSheet.getQiTaJinRongZiChanLiuDong());
                double yanShengJinRongGongJuZiChanLiuDong = parseDoubleValue(
                        balanceSheet.getYanShengJinRongGongJuZiChanLiuDong());
                double chiZuoChuShouDeZiChanLiuDong = parseDoubleValue(balanceSheet.getChiZuoChuShouDeZiChanLiuDong());
                double chiYouZhiDaoQiTouZiLiuDong = parseDoubleValue(balanceSheet.getChiYouZhiDaoQiTouZiLiuDong());
                double zhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChanLiuDong = parseDoubleValue(
                        balanceSheet.getZhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChanLiuDong());
                double touZiWuYe = parseDoubleValue(balanceSheet.getTouZiWuYe());
                double keGongChuShouTouZi = parseDoubleValue(balanceSheet.getKeGongChuShouTouZi());
                double chiYouZhiDaoQiTouZi = parseDoubleValue(balanceSheet.getChiYouZhiDaoQiTouZi());
                double zhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChan = parseDoubleValue(
                        balanceSheet.getZhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChan());
                double qiTaJinRongZiChanFeiLiuDong = parseDoubleValue(balanceSheet.getQiTaJinRongZiChanFeiLiuDong());
                double lianYingGongSiQuanYi = parseDoubleValue(balanceSheet.getLianYingGongSiQuanYi());
                double heYingGongSiQuanYi = parseDoubleValue(balanceSheet.getHeYingGongSiQuanYi());
                double yuLianYingGongSiKeShuHuiGongJuDeTouZi = parseDoubleValue(
                        balanceSheet.getYuLianYingGongSiKeShuHuiGongJuDeTouZi());
                // 计算负债比率
                Map<String, Double> debtRatioMap = new HashMap<>();
                if (totalAssets > 0) {
                    double totalLiabilitiesRate = totalLiabilities / totalAssets;
                    debtRatioMap.put("资产负债率", totalLiabilitiesRate);
                    debtRatioMap.put("流动负债率", currentLiabilities / totalAssets);
                    debtRatioMap.put("非流动负债率", nonCurrentLiabilities / totalAssets);
                    // 经营性负债
                    double operatingLiabilities = yingFuZhangKuan // 应付账款
                            + yingFuPiaoJu // 应付票据
                            + qiTaYingFuKuanJiYingJiFeiYong // 其他应付款及应计费用
                            + yingFuGuanLianFangKuanXiangLiuDong // 应付关联方款项（流动）
                            + diYanShouRuLiuDong // 递延收入（流动）
                            + diYanShouRuFeiLiuDong // 递延收入（非流动）
                            + yingFuShuiXiang // 应付税项
                            + heTongFuZhai; // 合同负债
                    double operatingLiabilitiesRate = operatingLiabilities / totalAssets;
                    debtRatioMap.put("经营性负债率", operatingLiabilitiesRate);
                    debtRatioMap.put("金融性负债率", totalLiabilitiesRate - operatingLiabilitiesRate);
                    debtRatioMap.put("融资租赁负债率", (rongZiZuLinFuZhaiFeiLiuDong + rongZiZuLinFuZhaiLiuDong) / totalAssets);
                }

                // 资产结构分析
                Map<String, Double> assetRatioMap = new HashMap<>();
                if (totalAssets > 0) {
                    assetRatioMap.put("现金及等价物", (cashAndEquivalents + duanQiCunKuan) / totalAssets);
                    assetRatioMap.put("存货", inventory / totalAssets);
                    assetRatioMap.put("两应收一预付", (receivables + inventory + payables) / totalAssets);
                    assetRatioMap.put("受限制存款及现金", shouXianZhiCunKuanJiXianJin / totalAssets);
                    assetRatioMap.put("商誉", shangyu / totalAssets);
                    assetRatioMap.put("无形资产", wuXingZiChan / totalAssets);
                    assetRatioMap.put("固定资产及在建工程", (guDingZiChan + zaiJianGongCheng) / totalAssets);
                    double touZiXingZiChan = jiaoYiXingJinRongZiChanLiuDong
                            + qiTaJinRongZiChanLiuDong
                            + yanShengJinRongGongJuZiChanLiuDong
                            + chiZuoChuShouDeZiChanLiuDong
                            + chiYouZhiDaoQiTouZiLiuDong
                            + zhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChanLiuDong
                            + touZiWuYe
                            + keGongChuShouTouZi
                            + chiYouZhiDaoQiTouZi
                            + zhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChan
                            + qiTaJinRongZiChanFeiLiuDong
                            + lianYingGongSiQuanYi
                            + heYingGongSiQuanYi
                            + yuLianYingGongSiKeShuHuiGongJuDeTouZi;
                    assetRatioMap.put("投资性资产占比", touZiXingZiChan / totalAssets);
                }

                // 利润表分析
                Map<String, Double> profitDataMap = new HashMap<>();
                Map<String, Double> profitRatioMap = new HashMap<>();
                if (incomeStatement != null) {
                    System.out.println("incomeStatement: " + incomeStatement);
                    double revenue = parseDoubleValue(incomeStatement.getRevenue());
                    double profitForTheYear = parseDoubleValue(incomeStatement.getProfitForTheYear());

                    // 收入相关
                    profitDataMap.put("营业收入", revenue);
                    profitDataMap.put("其他营业收入", parseDoubleValue(incomeStatement.getOtherRevenue()));
                    profitDataMap.put("毛利", parseDoubleValue(incomeStatement.getGrossProfit()));

                    // 费用相关
                    profitDataMap.put("销售及分销费用", parseDoubleValue(incomeStatement.getSellingAndDistributionExpenses()));
                    profitDataMap.put("行政费用", parseDoubleValue(incomeStatement.getAdministrativeExpenses()));
                    profitDataMap.put("研发费用", parseDoubleValue(incomeStatement.getResearchAndDevelopmentExpenses()));
                    profitDataMap.put("减值及拨备", parseDoubleValue(incomeStatement.getImpairmentAndProvisions()));
                    profitDataMap.put("折旧与摊销", parseDoubleValue(incomeStatement.getDepreciationAndAmortization()));

                    // 利润相关
                    profitDataMap.put("净利润", profitForTheYear);
                    profitDataMap.put("终止或非持续业务溢利", parseDoubleValue(incomeStatement.getDiscontinuedOperationsProfit()));

                    // 财务活动相关
                    profitDataMap.put("财务收入", parseDoubleValue(incomeStatement.getFinancialIncome()));
                    profitDataMap.put("财务费用", parseDoubleValue(incomeStatement.getFinancialExpenses()));
                    profitDataMap.put("财务成本", parseDoubleValue(incomeStatement.getFinancialCosts()));

                    // 投资活动相关
                    profitDataMap.put("应占合营公司溢利", parseDoubleValue(incomeStatement.getShareOfJointVentureProfit()));
                    profitDataMap.put("应占联营公司溢利", parseDoubleValue(incomeStatement.getShareOfAssociateProfit()));


                    // 比率计算
                    if (revenue > 0) {
                        profitRatioMap.put("毛利率", parseDoubleValue(incomeStatement.getGrossProfit()) / revenue);
                        profitRatioMap.put("净利率", profitForTheYear / revenue);
                        profitRatioMap.put("其他收入率", parseDoubleValue(incomeStatement.getOtherIncome()) / revenue);
                        profitRatioMap.put("研发费用率", parseDoubleValue(incomeStatement.getResearchAndDevelopmentExpenses()) / revenue);
                        profitRatioMap.put("销售及分销费用率", parseDoubleValue(incomeStatement.getSellingAndDistributionExpenses()) / revenue);
                        profitRatioMap.put("行政费用率", parseDoubleValue(incomeStatement.getAdministrativeExpenses()) / revenue);
                        profitRatioMap.put("财务成本率", parseDoubleValue(incomeStatement.getFinancialCosts()) / revenue);
                    }
                }

                // 财务比率分析
                Map<String, Double> financialRatioMap = new HashMap<>();
                if (balanceSheet != null && incomeStatement != null) {
                    // 使用已定义的变量
                    double totalEquity = parseDoubleValue(balanceSheet.getZongQuanYi());
                    double currentAssets = parseDoubleValue(balanceSheet.getLiuDongZiChanHeJi());
                    double profitForTheYear = parseDoubleValue(incomeStatement.getProfitForTheYear());
                    
                    // 盈利能力比率
                    if (totalEquity > 0) {
                        financialRatioMap.put("ROE", profitForTheYear / totalEquity);
                    }
                    
                    // 偿债能力比率
                    if (currentLiabilities > 0) {
                        financialRatioMap.put("流动比率", currentAssets / currentLiabilities);
                    }
                }
                
                if (cashFlow != null && incomeStatement != null) {
                    // 现金流相关比率
                    double operatingCashFlow = parseDoubleValue(cashFlow.getNetOperatingCashFlow());
                    double profitForTheYear = parseDoubleValue(incomeStatement.getProfitForTheYear());
                    
                    // 利润获现率
                    if (profitForTheYear > 0) {
                        financialRatioMap.put("利润获现率", operatingCashFlow / profitForTheYear);
                    }
                }

                // 现金流量表分析
                Map<String, Double> cashFlowDataMap = new HashMap<>();
                if (cashFlow != null) {
                    // 使用新的字段名称
                    double operatingCashFlow = parseDoubleValue(cashFlow.getNetOperatingCashFlow());
                    double investingCashFlow = parseDoubleValue(cashFlow.getNetInvestingCashFlow());
                    double financingCashFlow = parseDoubleValue(cashFlow.getNetFinancingCashFlow());   
                    double purchaseOfFixedAssets = parseDoubleValue(cashFlow.getPurchaseOfPropertyPlantAndEquipment());

                    // 基础现金流量数据
                    cashFlowDataMap.put("经营活动现金流", operatingCashFlow);
                    cashFlowDataMap.put("投资活动现金流", investingCashFlow);
                    cashFlowDataMap.put("筹资活动现金流", financingCashFlow);
                    
                    // 自由现金流计算
                    double freeCashFlow = operatingCashFlow - purchaseOfFixedAssets;
                    cashFlowDataMap.put("自由现金流", freeCashFlow);
                }

                // 报告期维度数据
                debtRatios.put(period, debtRatioMap);
                assetRatios.put(period, assetRatioMap);
                profitData.put(period, profitDataMap);
                profitRatios.put(period, profitRatioMap);
                financialRatios.put(period, financialRatioMap);
                cashFlowData.put(period, cashFlowDataMap);
            }

            // 构建返回结果
            Map<String, Object> debtAnalysis = new HashMap<>();
            debtAnalysis.put("stock", stock);
            debtAnalysis.put("yearlyRatios", debtRatios);
            debtAnalysis.put("success", true);

            Map<String, Object> assetAnalysis = new HashMap<>();
            assetAnalysis.put("stock", stock);
            assetAnalysis.put("yearlyRatios", assetRatios);
            assetAnalysis.put("success", true);

            Map<String, Object> profitDataAnalysis = new HashMap<>();
            profitDataAnalysis.put("stock", stock);
            profitDataAnalysis.put("yearlyRatios", profitData);
            profitDataAnalysis.put("success", true);

            Map<String, Object> profitRatioAnalysis = new HashMap<>();
            profitRatioAnalysis.put("stock", stock);
            profitRatioAnalysis.put("yearlyRatios", profitRatios);
            profitRatioAnalysis.put("success", true);

            Map<String, Object> financialRatioAnalysis = new HashMap<>();
            financialRatioAnalysis.put("stock", stock);
            financialRatioAnalysis.put("yearlyRatios", financialRatios);
            financialRatioAnalysis.put("success", true);

            Map<String, Object> cashFlowAnalysis = new HashMap<>();
            cashFlowAnalysis.put("stock", stock);
            cashFlowAnalysis.put("yearlyRatios", cashFlowData);
            cashFlowAnalysis.put("success", true);

            // 获取港股历史市盈率数据
            try {
                List<StockValuationData> hkValuationData = akShareService.getStockHkHistValuation(stock);
                if (hkValuationData != null && !hkValuationData.isEmpty()) {
                    // 构建市盈率历史数据
                    List<String> peDates = new ArrayList<>();
                    List<Double> peValues = new ArrayList<>();
                    
                    for (StockValuationData data : hkValuationData) {
                        if (data.getDate() != null && data.getValue() != null) {
                            peDates.add(data.getDate());
                            try {
                                double peValue = Double.parseDouble(data.getValue());
                                peValues.add(peValue);
                            } catch (NumberFormatException e) {
                                System.err.println("解析港股市盈率数值失败: " + data.getValue());
                            }
                        }
                    }
                    
                    Map<String, Object> peAnalysis = new HashMap<>();
                    peAnalysis.put("stock", stock);
                    peAnalysis.put("dates", peDates);
                    peAnalysis.put("values", peValues);
                    peAnalysis.put("success", true);
                    
                    result.put("peAnalysis", peAnalysis);
                    System.out.println("港股市盈率历史数据获取成功，数据点数量: " + peValues.size());
                } else {
                    System.out.println("港股市盈率历史数据为空");
                    result.put("peAnalysis", null);
                }
            } catch (Exception e) {
                System.err.println("获取港股市盈率历史数据失败: " + e.getMessage());
                result.put("peAnalysis", null);
            }
            
            result.put("debtAnalysis", debtAnalysis);
            result.put("assetAnalysis", assetAnalysis);
            result.put("profitDataAnalysis", profitDataAnalysis);
            result.put("profitRatioAnalysis", profitRatioAnalysis);
            result.put("financialRatioAnalysis", financialRatioAnalysis);
            result.put("cashFlowAnalysis", cashFlowAnalysis);
            result.put("success", true);

            // 构建StockWatchlist对象，使用最新报告期的数据
            try {
                // 获取最新报告期的数据
                String latestPeriod = periodData.keySet().iterator().next(); // 由于使用TreeMap倒序，第一个就是最新的
                Map<String, Object> latestData = periodData.get(latestPeriod);
                
                if (latestData != null) {
                    StockFinancialHkBalanceSheet latestBalanceSheet = (StockFinancialHkBalanceSheet) latestData.get("balanceSheet");
                    StockFinancialHkIncomeStatement latestIncomeStatement = (StockFinancialHkIncomeStatement) latestData.get("incomeStatement");
                    
                    if (latestBalanceSheet != null && latestIncomeStatement != null) {
                        // 构建StockWatchlist对象
                        StockWatchlist stockWatchlist = new StockWatchlist();
                        stockWatchlist.setStockCode(stock);
                        stockWatchlist.setStockName(stockName);
                        stockWatchlist.setStockType(0); // 默认类型为0，后续业务逻辑会设置
                        
                        // 设置财务指标（使用最新报告期数据）
                        // 1. peTtm为最新的StockValuationData
                        setHkPeTtm(stockWatchlist, stock);
                        
                        // 2. roe通过财务数据计算：净利润/净资产
                        setHkRoe(stockWatchlist, latestIncomeStatement, latestBalanceSheet);
                        
                        // 3. 利润质量为近五年年度财报现金流量表中经营现金流量净额之和除以近五年年度财务报表中净利润之和
                        
                        // 4. 资产质量得分为100-两应收一预付占比*100-商誉和无形资产占比*100-存货占比*100
                        setHkAssetsQuality(stockWatchlist, periodData, assetRatios);
                        setHkProfitQuality(stockWatchlist, periodData);
                        
                        // 5. 市盈率得分：将历史市盈率数据从小到大排序，计算最新市盈率在排序后的位置百分比
                        setHkPeScore(stockWatchlist, stock);
                        
                        // TODO: 后续业务逻辑会完善其他字段的计算
                        
                        // 这里只是构建对象，具体的业务逻辑后续再实现
                        System.out.println("构建StockWatchlist对象: " + stock + " - " + stockName + ", peTtm: " + stockWatchlist.getPeTtm() + ", roe: " + stockWatchlist.getRoe() + ", profitQuality: " + stockWatchlist.getProfitQuality() + ", assetsQuality: " + stockWatchlist.getAssetsQuality() + ", peScore: " + stockWatchlist.getPeScore());
                        
                        // 保存StockWatchlist对象到数据库
                        try {
                            // 检查是否已存在
                            StockWatchlist existingStock = stockWatchlistService.getStockByCode(stock);
                            if (existingStock != null) {
                                // 更新：保持原有StockType不变
                                stockWatchlist.setStockType(existingStock.getStockType());
                                stockWatchlist.setId(existingStock.getId());
                                stockWatchlist.setCreatedTime(existingStock.getCreatedTime());
                                stockWatchlist.setUpdatedTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                                
                                stockWatchlistService.updateStock(stockWatchlist);
                                System.out.println("更新StockWatchlist成功: " + stock + ", 保持原有类型: " + existingStock.getStockType());
                            } else {
                                // 新增：StockType设为0
                                stockWatchlist.setStockType(0);
                                stockWatchlist.setCreatedTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                                stockWatchlist.setUpdatedTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                                
                                stockWatchlistService.addStock(stockWatchlist);
                                System.out.println("新增StockWatchlist成功: " + stock + ", 类型设为: 0");
                            }
                        } catch (Exception e) {
                            System.err.println("保存StockWatchlist失败: " + stock + " - " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                // 构建StockWatchlist失败不影响主要分析结果
                System.err.println("构建StockWatchlist对象失败: " + e.getMessage());
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "分析过程中发生错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 解析Double类型字段值
     */
    private Double parseDoubleValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * 分析港股换手率分布
     * 
     * @param stock 港股代码
     * @return 换手率分布分析结果
     */
    public Map<String, Object> analyzeHkTurnoverDistribution(String stock) {
        Map<String, Object> result = new HashMap<>();
        result.put("stock", stock);

        try {
            // 1. 获取港股的换手率历史数据
            List<StockTurnoverData> turnoverDataList = akShareService.getStockHkHistTurnover(stock);

            if (turnoverDataList == null || turnoverDataList.isEmpty()) {
                result.put("success", false);
                result.put("message", "未找到该港股的换手率数据");
                return result;
            }

            // 2. 将换手率从大到小进行排序
            List<Double> turnoverValues = new ArrayList<>();
            String latestTurnover = null;
            String latestDate = null;

            for (StockTurnoverData data : turnoverDataList) {
                String turnoverStr = data.getTurnover();
                if (turnoverStr != null && !turnoverStr.trim().isEmpty()) {
                    try {
                        // 港股换手率可能包含%符号，需要处理
                        String cleanTurnoverStr = turnoverStr.replace("%", "").trim();
                        double turnover = Double.parseDouble(cleanTurnoverStr);
                        turnoverValues.add(turnover);

                        // 记录最新的换手率（数据按时间正序，最新的在最后）
                        if (data.getDate() != null) {
                            latestTurnover = turnoverStr;
                            latestDate = data.getDate();
                        }
                    } catch (NumberFormatException e) {
                        // 忽略无法解析的数据
                        continue;
                    }
                }
            }

            if (turnoverValues.isEmpty()) {
                result.put("success", false);
                result.put("message", "港股换手率数据解析失败");
                return result;
            }

            // 从大到小排序
            Collections.sort(turnoverValues, Collections.reverseOrder());

            // 3. 将全部换手率分割成1000个区间，统计落入每个区间换手率的日期的个数
            double minTurnover = turnoverValues.get(turnoverValues.size() - 1); // 最小值
            double maxTurnover = turnoverValues.get(0); // 最大值
            double rangeSize = (maxTurnover - minTurnover) / 1000.0; // 每个区间的大小

            int[] intervalCounts = new int[1000]; // 每个区间的计数
            int latestIntervalIndex = -1; // 最新换手率所在的区间索引

            // 统计每个区间的数据个数
            for (double turnover : turnoverValues) {
                int intervalIndex = (int) Math.floor((turnover - minTurnover) / rangeSize);
                if (intervalIndex >= 1000) {
                    intervalIndex = 999; // 防止越界
                }
                intervalCounts[intervalIndex]++;
            }

            // 找到最新换手率所在的区间
            if (latestTurnover != null) {
                try {
                    String cleanLatestTurnover = latestTurnover.replace("%", "").trim();
                    double latestTurnoverValue = Double.parseDouble(cleanLatestTurnover);
                    latestIntervalIndex = (int) Math.floor((latestTurnoverValue - minTurnover) / rangeSize);
                    if (latestIntervalIndex >= 1000) {
                        latestIntervalIndex = 999;
                    }
                } catch (NumberFormatException e) {
                    latestIntervalIndex = -1;
                }
            }

            // 构建区间标签
            List<String> intervalLabels = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                double startValue = minTurnover + i * rangeSize;
                String label2 = String.format("%.2f", startValue);
                intervalLabels.add(label2);
            }

            // 构建返回结果
            result.put("success", true);
            result.put("intervalCounts", intervalCounts);
            result.put("intervalLabels", intervalLabels);
            result.put("latestTurnover", latestTurnover);
            result.put("latestDate", latestDate);
            result.put("totalDays", turnoverValues.size());
            result.put("latestIntervalIndex", latestIntervalIndex);
            result.put("minTurnover", minTurnover);
            result.put("maxTurnover", maxTurnover);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "港股换手率分布分析失败：" + e.getMessage());
        }

        return result;
    }
    
    /**
     * 判断是否为年度报表
     * @param period 报告期
     * @return true为年度报表，false为季度报表
     */
    private boolean isAnnualReport(String period) {
        if (period == null || period.trim().isEmpty()) {
            return false;
        }
        
        String cleanPeriod = period.trim();
        
        // 年度报表格式：
        // 1. 包含"12-31"（如：2023-12-31）
        // 2. 包含"年"（如：2023年）
        // 3. 只包含年份（如：2023）
        // 4. 包含"年度"（如：2023年度）
        
        return cleanPeriod.contains("12-31") || 
               cleanPeriod.contains("年") || 
               cleanPeriod.matches("^\\d{4}$") || 
               cleanPeriod.contains("年度");
    }
    
    /**
     * 设置港股市盈率TTM
     */
    private void setHkPeTtm(StockWatchlist stockWatchlist, String stock) {
        try {
            List<StockValuationData> hkValuationData = akShareService.getStockHkHistValuation(stock);
            if (hkValuationData != null && !hkValuationData.isEmpty()) {
                StockValuationData latestValuation = hkValuationData.get(hkValuationData.size() - 1);
                if (latestValuation.getValue() != null && !latestValuation.getValue().trim().isEmpty()) {
                    double latestPeValue = Double.parseDouble(latestValuation.getValue());
                    stockWatchlist.setPeTtm(latestPeValue);
                    System.out.println("港股最新市盈率TTM设置完成: " + latestPeValue);
                    return;
                }
            }
            stockWatchlist.setPeTtm(0.0);
            System.err.println("港股市盈率数据获取失败或为空");
        } catch (Exception e) {
            stockWatchlist.setPeTtm(0.0);
            System.err.println("获取港股最新市盈率TTM失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置港股ROE
     */
    private void setHkRoe(StockWatchlist stockWatchlist, StockFinancialHkIncomeStatement incomeStatement, StockFinancialHkBalanceSheet balanceSheet) {
        try {
            String netProfitStr = incomeStatement.getProfitForTheYear();
            String netAssetsStr = balanceSheet.getJingZiChan();
            if (netAssetsStr == null || netAssetsStr.trim().isEmpty()) {
                netAssetsStr = balanceSheet.getGuDongQuanYi();
            }
            
            if (netProfitStr != null && netAssetsStr != null && 
                !netProfitStr.trim().isEmpty() && !netAssetsStr.trim().isEmpty()) {
                
                double netProfit = parseDoubleValue(netProfitStr);
                double netAssets = parseDoubleValue(netAssetsStr);
                
                if (netAssets != 0) {
                    double roe = (netProfit / netAssets) * 100;
                    stockWatchlist.setRoe(roe);
                    System.out.println("港股ROE计算完成 - 净利润: " + netProfit + ", 净资产: " + netAssets + ", ROE: " + roe + "%");
                    return;
                }
            }
            stockWatchlist.setRoe(0.0);
            System.err.println("港股ROE计算失败：数据不完整或净资产为0");
        } catch (Exception e) {
            stockWatchlist.setRoe(0.0);
            System.err.println("计算港股ROE失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置港股资产质量得分
     */
    private void setHkAssetsQuality(StockWatchlist stockWatchlist, Map<String, Map<String, Object>> periodData, Map<String, Map<String, Double>> assetRatios) {
        try {
            String hkLatestPeriod = periodData.keySet().iterator().next();
            Map<String, Double> hkLatestAssetRatios = assetRatios.get(hkLatestPeriod);
            
            if (hkLatestAssetRatios != null) {
                Double inventoryRatio = hkLatestAssetRatios.get("存货");
                Double goodwillRatio = hkLatestAssetRatios.get("商誉");
                Double intangibleRatio = hkLatestAssetRatios.get("无形资产");
                Double receivablesPrepaymentsRatio = hkLatestAssetRatios.get("两应收一预付");
                
                if (receivablesPrepaymentsRatio != null && inventoryRatio != null && 
                    goodwillRatio != null && intangibleRatio != null) {
                    
                    double adjustedReceivablesPrepaymentsRatio = receivablesPrepaymentsRatio - inventoryRatio;
                    double hkAssetsQuality = 100 - (adjustedReceivablesPrepaymentsRatio * 100) - 
                                          ((goodwillRatio + intangibleRatio) * 100) - (inventoryRatio * 100);
                    
                    hkAssetsQuality = Math.max(0, Math.min(100, hkAssetsQuality));
                    stockWatchlist.setAssetsQuality(hkAssetsQuality);
                    
                    System.out.println("港股资产质量得分计算完成 - 两应收一预付占比: " + (adjustedReceivablesPrepaymentsRatio * 100) + 
                                     "%, 商誉和无形资产占比: " + ((goodwillRatio + intangibleRatio) * 100) + 
                                     "%, 存货占比: " + (inventoryRatio * 100) + "%, 资产质量得分: " + hkAssetsQuality);
                    return;
                }
            }
            stockWatchlist.setAssetsQuality(0.0);
            System.err.println("港股资产质量得分计算失败：数据不完整");
        } catch (Exception e) {
            stockWatchlist.setAssetsQuality(0.0);
            System.err.println("计算港股资产质量得分失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置港股利润质量
     */
    private void setHkProfitQuality(StockWatchlist stockWatchlist, Map<String, Map<String, Object>> periodData) {
        try {
            double totalOperatingCashFlow = 0.0;
            double totalNetProfit = 0.0;
            int yearCount = 0;
            
            for (Map.Entry<String, Map<String, Object>> periodEntry : periodData.entrySet()) {
                if (yearCount >= 5) break;
                
                String period = periodEntry.getKey();
                if (!isAnnualReport(period)) continue;
                
                Map<String, Object> data = periodEntry.getValue();
                StockFinancialHkCashFlow cashFlow = (StockFinancialHkCashFlow) data.get("cashFlow");
                StockFinancialHkIncomeStatement incomeStatement = (StockFinancialHkIncomeStatement) data.get("incomeStatement");
                
                if (cashFlow != null && incomeStatement != null) {
                    String operatingCashFlowStr = cashFlow.getNetOperatingCashFlow();
                    String netProfitStr = incomeStatement.getProfitForTheYear();
                    
                    if (operatingCashFlowStr != null && !operatingCashFlowStr.trim().isEmpty()) {
                        totalOperatingCashFlow += parseDoubleValue(operatingCashFlowStr);
                    }
                    
                    if (netProfitStr != null && !netProfitStr.trim().isEmpty()) {
                        totalNetProfit += parseDoubleValue(netProfitStr);
                    }
                    
                    yearCount++;
                }
            }
            
            if (totalNetProfit != 0) {
                double profitQuality = totalOperatingCashFlow / totalNetProfit;
                stockWatchlist.setProfitQuality(profitQuality);
                System.out.println("港股利润质量计算完成 - 经营现金流量净额总和: " + totalOperatingCashFlow + 
                                 ", 净利润总和: " + totalNetProfit + ", 利润质量: " + profitQuality);
            } else {
                stockWatchlist.setProfitQuality(0.0);
                System.err.println("港股净利润为0，无法计算利润质量");
            }
        } catch (Exception e) {
            stockWatchlist.setProfitQuality(0.0);
            System.err.println("计算港股利润质量失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置港股市盈率得分
     */
    private void setHkPeScore(StockWatchlist stockWatchlist, String stock) {
        try {
            List<StockValuationData> hkValuationData = akShareService.getStockHkHistValuation(stock);
            if (hkValuationData != null && !hkValuationData.isEmpty()) {
                List<Double> validPeValues = new ArrayList<>();
                Double latestPeValue = null;
                
                // 提取所有有效的市盈率数据
                for (StockValuationData valuation : hkValuationData) {
                    if (valuation.getValue() != null && !valuation.getValue().trim().isEmpty()) {
                        try {
                            double peValue = Double.parseDouble(valuation.getValue());
                            validPeValues.add(peValue < 0 ? Double.POSITIVE_INFINITY : peValue);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                }
                
                if (!validPeValues.isEmpty()) {
                    // 获取最新市盈率
                    StockValuationData latestValuation = hkValuationData.get(hkValuationData.size() - 1);
                    if (latestValuation.getValue() != null && !latestValuation.getValue().trim().isEmpty()) {
                        try {
                            latestPeValue = Double.parseDouble(latestValuation.getValue());
                            if (latestPeValue < 0) {
                                latestPeValue = Double.POSITIVE_INFINITY;
                            }
                        } catch (NumberFormatException e) {
                            latestPeValue = null;
                        }
                    }
                    
                    if (latestPeValue != null) {
                        Collections.sort(validPeValues);
                        int position = Collections.binarySearch(validPeValues, latestPeValue);
                        if (position < 0) {
                            position = -(position + 1);
                        }
                        
                        double peScore = ((double) (validPeValues.size() - position) / validPeValues.size()) * 100;
                        peScore = Math.max(0, Math.min(100, peScore));
                        
                        stockWatchlist.setPeScore(peScore);
                        
                        String latestPeDisplay = latestPeValue == Double.POSITIVE_INFINITY ? "无穷大(负值)" : String.valueOf(latestPeValue);
                        System.out.println("港股市盈率得分计算完成 - 历史数据总数: " + validPeValues.size() + 
                                         ", 最新市盈率: " + latestPeDisplay + ", 排序位置: " + (position + 1) + 
                                         ", 市盈率得分: " + peScore);
                        return;
                    }
                }
            }
            stockWatchlist.setPeScore(0.0);
            System.err.println("港股市盈率得分计算失败：数据不完整");
        } catch (Exception e) {
            stockWatchlist.setPeScore(0.0);
            System.err.println("计算港股市盈率得分失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置A股市盈率TTM
     */
    private void setASharePeTtm(StockWatchlist stockWatchlist, List<StockValuationData> valuationDataList) {
        try {
            if (valuationDataList != null && !valuationDataList.isEmpty()) {
                StockValuationData latestValuation = valuationDataList.get(valuationDataList.size() - 1);
                if (latestValuation != null && latestValuation.getValue() != null) {
                    double peTtm = Double.parseDouble(latestValuation.getValue());
                    stockWatchlist.setPeTtm(peTtm);
                    return;
                }
            }
            stockWatchlist.setPeTtm(0.0);
            System.err.println("A股市盈率TTM数据获取失败或为空");
        } catch (Exception e) {
            stockWatchlist.setPeTtm(0.0);
            System.err.println("解析A股市盈率TTM失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置A股ROE
     */
    private void setAShareRoe(StockWatchlist stockWatchlist, String symbol) {
        try {
            StockInfo stockInfo = akShareService.getStockIndividualInfo(symbol);
            if (stockInfo != null && stockInfo.getRoe() != null) {
                String roeStr = stockInfo.getRoe().trim();
                if (roeStr.endsWith("%")) {
                    roeStr = roeStr.substring(0, roeStr.length() - 1);
                }
                double roe = Double.parseDouble(roeStr);
                stockWatchlist.setRoe(roe);
                System.out.println("设置A股ROE成功: " + roe);
                return;
            }
            stockWatchlist.setRoe(0.0);
            System.err.println("A股ROE数据为空");
        } catch (Exception e) {
            stockWatchlist.setRoe(0.0);
            System.err.println("获取A股StockInfo失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置A股利润质量
     */
    private void setAShareProfitQuality(StockWatchlist stockWatchlist, Map<String, Map<String, Object>> periodData) {
        try {
            double totalOperatingCashFlow = 0.0;
            double totalNetProfit = 0.0;
            int yearCount = 0;
            
            for (Map.Entry<String, Map<String, Object>> periodEntry : periodData.entrySet()) {
                if (yearCount >= 5) break;
                
                String period = periodEntry.getKey();
                if (!isAnnualReport(period)) continue;
                
                Map<String, Object> data = periodEntry.getValue();
                StockFinancialCashThs cashFlow = (StockFinancialCashThs) data.get("cashFlowStatement");
                StockFinancialBenefitThs incomeStatement = (StockFinancialBenefitThs) data.get("incomeStatement");
                
                if (cashFlow != null && incomeStatement != null) {
                    String operatingCashFlowStr = cashFlow.getNetOperatingCash();
                    String netProfitStr = incomeStatement.getNetProfit();
                    
                    if (operatingCashFlowStr != null && !operatingCashFlowStr.trim().isEmpty()) {
                        totalOperatingCashFlow += parseFinancialValue(operatingCashFlowStr);
                    }
                    
                    if (netProfitStr != null && !netProfitStr.trim().isEmpty()) {
                        totalNetProfit += parseFinancialValue(netProfitStr);
                    }
                    
                    yearCount++;
                }
            }
            
            if (totalNetProfit != 0) {
                double profitQuality = totalOperatingCashFlow / totalNetProfit;
                stockWatchlist.setProfitQuality(profitQuality);
                System.out.println("A股利润质量计算完成 - 经营现金流量净额总和: " + totalOperatingCashFlow + 
                                 ", 净利润总和: " + totalNetProfit + ", 利润质量: " + profitQuality);
            } else {
                stockWatchlist.setProfitQuality(0.0);
                System.err.println("A股净利润为0，无法计算利润质量");
            }
        } catch (Exception e) {
            stockWatchlist.setProfitQuality(0.0);
            System.err.println("计算A股利润质量失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置A股资产质量得分
     */
    private void setAShareAssetsQuality(StockWatchlist stockWatchlist, Map<String, Map<String, Double>> assetRatios, String latestPeriod) {
        try {
            Map<String, Double> latestAssetRatios = assetRatios.get(latestPeriod);
            
            if (latestAssetRatios != null) {
                Double receivablesPrepaymentsRatio = latestAssetRatios.get("两应收一预付占比");
                Double goodwillIntangibleRatio = latestAssetRatios.get("商誉和无形资产占比");
                Double inventoryRatio = latestAssetRatios.get("存货占比");
                
                if (receivablesPrepaymentsRatio != null && goodwillIntangibleRatio != null && inventoryRatio != null) {
                    double assetsQuality = 100 - (receivablesPrepaymentsRatio * 100) - 
                                        (goodwillIntangibleRatio * 100) - (inventoryRatio * 100);
                    
                    assetsQuality = Math.max(0, Math.min(100, assetsQuality));
                    stockWatchlist.setAssetsQuality(assetsQuality);
                    
                    System.out.println("A股资产质量得分计算完成 - 两应收一预付占比: " + (receivablesPrepaymentsRatio * 100) + 
                                     "%, 商誉和无形资产占比: " + (goodwillIntangibleRatio * 100) + 
                                     "%, 存货占比: " + (inventoryRatio * 100) + "%, 资产质量得分: " + assetsQuality);
                    return;
                }
            }
            stockWatchlist.setAssetsQuality(0.0);
            System.err.println("A股资产质量得分计算失败：数据不完整");
        } catch (Exception e) {
            stockWatchlist.setAssetsQuality(0.0);
            System.err.println("计算A股资产质量得分失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置A股市盈率得分
     */
    private void setASharePeScore(StockWatchlist stockWatchlist, List<StockValuationData> valuationDataList) {
        try {
            if (valuationDataList != null && !valuationDataList.isEmpty()) {
                List<Double> validPeValues = new ArrayList<>();
                Double latestPeValue = null;
                
                // 提取所有有效的市盈率数据
                for (StockValuationData valuation : valuationDataList) {
                    if (valuation.getValue() != null && !valuation.getValue().trim().isEmpty()) {
                        try {
                            double peValue = Double.parseDouble(valuation.getValue());
                            validPeValues.add(peValue < 0 ? Double.POSITIVE_INFINITY : peValue);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                }
                
                if (!validPeValues.isEmpty()) {
                    // 获取最新市盈率
                    StockValuationData latestValuation = valuationDataList.get(valuationDataList.size() - 1);
                    if (latestValuation.getValue() != null && !latestValuation.getValue().trim().isEmpty()) {
                        try {
                            latestPeValue = Double.parseDouble(latestValuation.getValue());
                            if (latestPeValue < 0) {
                                latestPeValue = Double.POSITIVE_INFINITY;
                            }
                        } catch (NumberFormatException e) {
                            latestPeValue = null;
                        }
                    }
                    
                    if (latestPeValue != null) {
                        Collections.sort(validPeValues);
                        int position = Collections.binarySearch(validPeValues, latestPeValue);
                        if (position < 0) {
                            position = -(position + 1);
                        }
                        
                        double peScore = ((double) (validPeValues.size() - position) / validPeValues.size()) * 100;
                        peScore = Math.max(0, Math.min(100, peScore));
                        
                        stockWatchlist.setPeScore(peScore);
                        
                        String latestPeDisplay = latestPeValue == Double.POSITIVE_INFINITY ? "无穷大(负值)" : String.valueOf(latestPeValue);
                        System.out.println("A股市盈率得分计算完成 - 历史数据总数: " + validPeValues.size() + 
                                         ", 最新市盈率: " + latestPeDisplay + ", 排序位置: " + (position + 1) + 
                                         ", 市盈率得分: " + peScore);
                        return;
                    }
                }
            }
            stockWatchlist.setPeScore(0.0);
            System.err.println("A股市盈率得分计算失败：数据不完整");
        } catch (Exception e) {
            stockWatchlist.setPeScore(0.0);
            System.err.println("计算A股市盈率得分失败: " + e.getMessage());
        }
    }

    
    
    /**
     * 计算并保存市危率数据
     * 对于每一支A股股票，获取其历史市盈率数据，整合全部股票在每个日期的市盈率，
     * 排序后取中位数作为市危率，存入数据库
     */
    @Deprecated
    public void calculateAndSaveMarketRiskRatio() {
        try {
            System.out.println("开始计算市危率...");
            
            // 获取所有A股股票列表
            List<StockInfo> stockList = akShareService.getStockList();
            if (stockList == null || stockList.isEmpty()) {
                System.err.println("获取A股股票列表失败");
                return;
            }
            
            System.out.println("获取到 " + stockList.size() + " 只A股股票");
            
            // 用于存储每个日期的市盈率数据
            Map<String, List<Double>> dateToPeValues = new HashMap<>();
            
            // 遍历每只股票，获取其历史市盈率数据
            int processedCount = 0;
            for (StockInfo stock : stockList) {
                try {
                    String stockCode = stock.getCode();
                    if (stockCode == null || stockCode.trim().isEmpty()) {
                        continue;
                    }
                    
                    // 获取该股票的历史市盈率数据
                    List<StockValuationData> valuationDataList = akShareService.getStockValuationData(stockCode);
                    if (valuationDataList != null && !valuationDataList.isEmpty()) {
                        // 处理每个日期的市盈率数据
                        for (StockValuationData valuation : valuationDataList) {
                            if (valuation.getDate() != null && valuation.getValue() != null && !valuation.getValue().trim().isEmpty()) {
                                String date = valuation.getDate();
                                
                                // 检查日期是否在2010年1月1日之后
                                if (isDateAfter2010(date)) {
                                    try {
                                        double peValue = Double.parseDouble(valuation.getValue());
                                        // 负数视为正无穷
                                        double processedPeValue = peValue < 0 ? Double.POSITIVE_INFINITY : peValue;
                                        
                                        // 标准化日期格式为YYYY-MM-DD
                                        String normalizedDate = normalizeDate(date);
                                        
                                        // 将市盈率添加到对应日期的列表中
                                        dateToPeValues.computeIfAbsent(normalizedDate, k -> new ArrayList<>()).add(processedPeValue);
                                    } catch (NumberFormatException e) {
                                        // 忽略无法解析的数据
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    
                    processedCount++;
                    if (processedCount % 100 == 0) {
                        System.out.println("已处理 " + processedCount + " 只股票...");
                    }
                    
                } catch (Exception e) {
                    System.err.println("处理股票 " + stock.getCode() + " 时发生错误: " + e.getMessage());
                    continue;
                }
            }
            
            System.out.println("市盈率数据收集完成，共处理 " + processedCount + " 只股票");
            System.out.println("收集到 " + dateToPeValues.size() + " 个日期的数据");
            
            // 计算每个日期的市危率（中位数）
            List<MarketRiskRatio> marketRiskRatios = new ArrayList<>();
            for (Map.Entry<String, List<Double>> entry : dateToPeValues.entrySet()) {
                String date = entry.getKey();
                List<Double> peValues = entry.getValue();
                
                if (peValues.size() >= 3) { // 至少需要3个数据点才计算中位数
                    // 排序市盈率数据
                    Collections.sort(peValues);
                    
                    // 计算中位数
                    double medianPe;
                    int size = peValues.size();
                    if (size % 2 == 0) {
                        // 偶数个数据，取中间两个数的平均值
                        medianPe = (peValues.get(size / 2 - 1) + peValues.get(size / 2)) / 2.0;
                    } else {
                        // 奇数个数据，取中间的数
                        medianPe = peValues.get(size / 2);
                    }
                    
                    // 创建市危率模型，保留两位小数
                    MarketRiskRatio marketRiskRatio = new MarketRiskRatio();
                    marketRiskRatio.setDate(date);
                    // 保留两位小数
                    double roundedMedianPe = Math.round(medianPe * 100.0) / 100.0;
                    marketRiskRatio.setMarketRiskRatio(roundedMedianPe);
                    
                    marketRiskRatios.add(marketRiskRatio);
                }
            }
            
            System.out.println("计算出 " + marketRiskRatios.size() + " 个日期的市危率");
            
            // 保存市危率数据到数据库
            saveMarketRiskRatiosToDatabase(marketRiskRatios);
            
            System.out.println("市危率计算和保存完成！");
            
        } catch (Exception e) {
            System.err.println("计算市危率时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 标准化日期格式为YYYY-MM-DD
     */
    private String normalizeDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            
            String cleanDate = dateStr.trim();
            
            // 如果包含T，去掉时间部分
            if (cleanDate.contains("T")) {
                cleanDate = cleanDate.split("T")[0];
            }
            
            // 如果包含空格，去掉时间部分
            if (cleanDate.contains(" ")) {
                cleanDate = cleanDate.split(" ")[0];
            }
            
            // 检查是否为YYYY-MM-DD格式
            if (cleanDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return cleanDate;
            }
            
            // 检查是否为YYYY/MM/DD格式
            if (cleanDate.matches("\\d{4}/\\d{2}/\\d{2}")) {
                return cleanDate.replace("/", "-");
            }
            
            // 检查是否为YYYYMMDD格式
            if (cleanDate.matches("\\d{8}")) {
                return cleanDate.substring(0, 4) + "-" + 
                       cleanDate.substring(4, 6) + "-" + 
                       cleanDate.substring(6, 8);
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("标准化日期 " + dateStr + " 时发生错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查日期是否在2010年1月1日之后
     */
    private boolean isDateAfter2010(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return false;
            }
            
            // 先标准化日期格式
            String normalizedDate = normalizeDate(dateStr);
            if (normalizedDate == null) {
                return false;
            }
            
            // 检查年份是否在2010年之后
            String[] parts = normalizedDate.split("-");
            int year = Integer.parseInt(parts[0]);
            return year >= 2010;
            
        } catch (Exception e) {
            System.err.println("解析日期 " + dateStr + " 时发生错误: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 保存市危率数据到数据库
     */
    private void saveMarketRiskRatiosToDatabase(List<MarketRiskRatio> marketRiskRatios) {
        try {
            System.out.println("准备保存 " + marketRiskRatios.size() + " 条市危率数据到数据库");
            
            int successCount = 0;
            int updateCount = 0;
            
            for (MarketRiskRatio ratio : marketRiskRatios) {
                try {
                    // 检查该日期是否已存在数据
                    MarketRiskRatio existingRatio = marketRiskRatioMapper.selectByDate(ratio.getDate());
                    
                    if (existingRatio != null) {
                        // 如果存在，则更新
                        int updateResult = marketRiskRatioMapper.updateByDate(ratio);
                        if (updateResult > 0) {
                            updateCount++;
                            System.out.println("更新市危率数据 - 日期: " + ratio.getDate() + 
                                             ", 市危率: " + ratio.getMarketRiskRatio());
                        }
                    } else {
                        // 如果不存在，则插入
                        int insertResult = marketRiskRatioMapper.insert(ratio);
                        if (insertResult > 0) {
                            successCount++;
                            System.out.println("插入市危率数据 - 日期: " + ratio.getDate() + 
                                             ", 市危率: " + ratio.getMarketRiskRatio());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("保存日期 " + ratio.getDate() + " 的市危率数据时发生错误: " + e.getMessage());
                    continue;
                }
            }
            
            System.out.println("市危率数据保存完成！");
            System.out.println("新增: " + successCount + " 条, 更新: " + updateCount + " 条");
            
        } catch (Exception e) {
            System.err.println("保存市危率数据到数据库时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 计算当日市危率并保存
     * 获取所有A股股票当日的市盈率数据，计算出中位数作为市危率，构建市危率并保存
     */
    public void marketRiskRatioToDate() {
        try {
            System.out.println("开始计算当日市危率...");
            
            // 获取当前日期（YYYY-MM-DD格式）
            String today = java.time.LocalDate.now().toString();
            System.out.println("计算日期: " + today);
            
            // 获取所有A股股票列表
            List<StockInfo> stockList = akShareService.getStockList();
            if (stockList == null || stockList.isEmpty()) {
                System.err.println("获取A股股票列表失败");
                return;
            }
            
            System.out.println("获取到 " + stockList.size() + " 只A股股票");
            
            // 用于存储当日所有股票的市盈率数据
            List<Double> todayPeValues = new ArrayList<>();
            
            // 遍历每只股票，获取其当日市盈率数据
            int processedCount = 0;
            int validPeCount = 0;
            
            for (StockInfo stock : stockList) {
                try {
                    String stockCode = stock.getCode();
                    if (stockCode == null || stockCode.trim().isEmpty()) {
                        continue;
                    }
                    
                    // 获取该股票当日的市盈率数据
                    List<StockValuationData> valuationDataList = akShareService.getStockValuationData(stockCode);
                    if (valuationDataList != null && !valuationDataList.isEmpty()) {
                        // 查找当日或最近的市盈率数据
                        StockValuationData todayValuation = null;
                        
                        for (StockValuationData valuation : valuationDataList) {
                            if (valuation.getDate() != null) {
                                String valuationDate = normalizeDate(valuation.getDate());
                                if (valuationDate != null && valuationDate.equals(today)) {
                                    todayValuation = valuation;
                                    break;
                                }
                            }
                        }
                        
                        // 如果没找到当日数据，尝试获取最近的数据
                        if (todayValuation == null && !valuationDataList.isEmpty()) {
                            todayValuation = valuationDataList.get(valuationDataList.size() - 1);
                        }
                        
                        // 处理市盈率数据
                        if (todayValuation != null && todayValuation.getValue() != null && !todayValuation.getValue().trim().isEmpty()) {
                            try {
                                double peValue = Double.parseDouble(todayValuation.getValue());
                                // 负数视为正无穷
                                double processedPeValue = peValue < 0 ? Double.POSITIVE_INFINITY : peValue;
                                
                                todayPeValues.add(processedPeValue);
                                validPeCount++;
                                
                            } catch (NumberFormatException e) {
                                // 忽略无法解析的数据
                                continue;
                            }
                        }
                    }
                    
                    processedCount++;
                    if (processedCount % 100 == 0) {
                        System.out.println("已处理 " + processedCount + " 只股票，有效市盈率数据: " + validPeCount + " 条");
                    }
                    
                } catch (Exception e) {
                    System.err.println("处理股票 " + stock.getCode() + " 时发生错误: " + e.getMessage());
                    continue;
                }
            }
            
            System.out.println("市盈率数据收集完成，共处理 " + processedCount + " 只股票，有效数据: " + validPeCount + " 条");
            
            if (todayPeValues.isEmpty()) {
                System.err.println("未收集到有效的市盈率数据，无法计算市危率");
                return;
            }
            
            // 计算当日市危率（中位数）
            Collections.sort(todayPeValues);
            double medianPe;
            int size = todayPeValues.size();
            
            if (size % 2 == 0) {
                // 偶数个数据，取中间两个数的平均值
                medianPe = (todayPeValues.get(size / 2 - 1) + todayPeValues.get(size / 2)) / 2.0;
            } else {
                // 奇数个数据，取中间的数
                medianPe = todayPeValues.get(size / 2);
            }
            
            // 保留两位小数
            double roundedMedianPe = Math.round(medianPe * 100.0) / 100.0;
            
            System.out.println("当日市危率计算完成 - 数据总数: " + size + ", 中位数: " + roundedMedianPe);
            
            // 创建市危率模型
            MarketRiskRatio marketRiskRatio = new MarketRiskRatio();
            marketRiskRatio.setDate(today);
            marketRiskRatio.setMarketRiskRatio(roundedMedianPe);
            
            // 保存到数据库
            saveSingleMarketRiskRatio(marketRiskRatio);
            
            System.out.println("当日市危率计算和保存完成！");
            
        } catch (Exception e) {
            System.err.println("计算当日市危率时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 保存单个市危率数据到数据库
     */
    private void saveSingleMarketRiskRatio(MarketRiskRatio marketRiskRatio) {
        try {
            // 检查该日期是否已存在数据
            MarketRiskRatio existingRatio = marketRiskRatioMapper.selectByDate(marketRiskRatio.getDate());
            
            if (existingRatio != null) {
                // 如果存在，则更新
                int updateResult = marketRiskRatioMapper.updateByDate(marketRiskRatio);
                if (updateResult > 0) {
                    System.out.println("更新当日市危率数据 - 日期: " + marketRiskRatio.getDate() + 
                                     ", 市危率: " + marketRiskRatio.getMarketRiskRatio());
                } else {
                    System.err.println("更新当日市危率数据失败");
                }
            } else {
                // 如果不存在，则插入
                int insertResult = marketRiskRatioMapper.insert(marketRiskRatio);
                if (insertResult > 0) {
                    System.out.println("插入当日市危率数据 - 日期: " + marketRiskRatio.getDate() + 
                                     ", 市危率: " + marketRiskRatio.getMarketRiskRatio());
                } else {
                    System.err.println("插入当日市危率数据失败");
                }
            }
            
        } catch (Exception e) {
            System.err.println("保存当日市危率数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}