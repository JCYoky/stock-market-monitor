package me.huangjiacheng.hundun.model;

/**
 * 同花顺财务摘要数据实体类
 */
public class StockFinancialAbstractThs {
    private String reportPeriod;        // 报告期
    private String roe;                 // 净资产收益率
    private String dilutedRoe;          // 净资产收益率-摊薄
    private String netProfitMargin;     // 销售净利率
    private String currentRatio;        // 流动比率
    private String quickRatio;          // 速动比率
    private String debtToAssetRatio;    // 资产负债率
    private String grossProfitMargin;   // 销售毛利率
    private String assetTurnover;       // 总资产周转率
    private String inventoryTurnover;   // 存货周转率
    private String receivableTurnover;  // 应收账款周转率

    // Getters and Setters
    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public String getRoe() {
        return roe;
    }

    public void setRoe(String roe) {
        this.roe = roe;
    }

    public String getDilutedRoe() {
        return dilutedRoe;
    }

    public void setDilutedRoe(String dilutedRoe) {
        this.dilutedRoe = dilutedRoe;
    }

    public String getNetProfitMargin() {
        return netProfitMargin;
    }

    public void setNetProfitMargin(String netProfitMargin) {
        this.netProfitMargin = netProfitMargin;
    }

    public String getCurrentRatio() {
        return currentRatio;
    }

    public void setCurrentRatio(String currentRatio) {
        this.currentRatio = currentRatio;
    }

    public String getQuickRatio() {
        return quickRatio;
    }

    public void setQuickRatio(String quickRatio) {
        this.quickRatio = quickRatio;
    }

    public String getDebtToAssetRatio() {
        return debtToAssetRatio;
    }

    public void setDebtToAssetRatio(String debtToAssetRatio) {
        this.debtToAssetRatio = debtToAssetRatio;
    }

    public String getGrossProfitMargin() {
        return grossProfitMargin;
    }

    public void setGrossProfitMargin(String grossProfitMargin) {
        this.grossProfitMargin = grossProfitMargin;
    }

    public String getAssetTurnover() {
        return assetTurnover;
    }

    public void setAssetTurnover(String assetTurnover) {
        this.assetTurnover = assetTurnover;
    }

    public String getInventoryTurnover() {
        return inventoryTurnover;
    }

    public void setInventoryTurnover(String inventoryTurnover) {
        this.inventoryTurnover = inventoryTurnover;
    }

    public String getReceivableTurnover() {
        return receivableTurnover;
    }

    public void setReceivableTurnover(String receivableTurnover) {
        this.receivableTurnover = receivableTurnover;
    }
} 