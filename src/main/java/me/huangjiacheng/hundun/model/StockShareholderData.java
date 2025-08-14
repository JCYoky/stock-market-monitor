package me.huangjiacheng.hundun.model;

/**
 * 股东户数数据模型
 */
public class StockShareholderData {
    private String symbol; // 股票代码
    private String name; // 股票名称
    private String statisticalDate; // 股东户数统计截止日
    private String shareholderCount; // 股东户数-本次

    public StockShareholderData() {}

    public StockShareholderData(String symbol, String name, String statisticalDate, String shareholderCount) {
        this.symbol = symbol;
        this.name = name;
        this.statisticalDate = statisticalDate;
        this.shareholderCount = shareholderCount;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatisticalDate() {
        return statisticalDate;
    }

    public void setStatisticalDate(String statisticalDate) {
        this.statisticalDate = statisticalDate;
    }

    public String getShareholderCount() {
        return shareholderCount;
    }

    public void setShareholderCount(String shareholderCount) {
        this.shareholderCount = shareholderCount;
    }

    @Override
    public String toString() {
        return "StockShareholderData{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", statisticalDate='" + statisticalDate + '\'' +
                ", shareholderCount='" + shareholderCount + '\'' +
                '}';
    }
} 