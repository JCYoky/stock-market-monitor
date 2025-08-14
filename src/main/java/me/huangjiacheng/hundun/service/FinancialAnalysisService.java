package me.huangjiacheng.hundun.service;

import me.huangjiacheng.hundun.model.StockFinancialDebtThs;
import me.huangjiacheng.hundun.model.StockFinancialBenefitThs;
import me.huangjiacheng.hundun.model.StockFinancialCashThs;
import me.huangjiacheng.hundun.model.StockValuationData;
import me.huangjiacheng.hundun.model.StockTurnoverData;
import me.huangjiacheng.hundun.model.StockFinancialHkBalanceSheet;
import me.huangjiacheng.hundun.model.StockFinancialHkIncomeStatement;
import me.huangjiacheng.hundun.model.StockFinancialHkCashFlow;

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

    /**
     * 综合分析上市公司财务状况，包括负债结构和资产结构
     * 
     * @param symbol 股票代码
     * @return Map: { debtAnalysis, assetAnalysis }
     */
    public Map<String, Object> analyzeFinancialStructure(String symbol) {
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
        Map<String, Object> valuationAnalysis = new HashMap<>();
        valuationAnalysis.put("symbol", symbol);
        valuationAnalysis.put("valuationData", valuationDataList);
        valuationAnalysis.put("success", true);
        result.put("valuationAnalysis", valuationAnalysis);

        result.put("success", true);

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
    public Map<String, Object> analyzeHkFinancialStructure(String stock) {
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

            result.put("debtAnalysis", debtAnalysis);
            result.put("assetAnalysis", assetAnalysis);
            result.put("profitDataAnalysis", profitDataAnalysis);
            result.put("profitRatioAnalysis", profitRatioAnalysis);
            result.put("financialRatioAnalysis", financialRatioAnalysis);
            result.put("cashFlowAnalysis", cashFlowAnalysis);
            result.put("success", true);

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
}