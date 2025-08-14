package me.huangjiacheng.hundun.model;

public class StockValuationData {
    private String date;           // 日期
    private String symbol;         // 股票代码
    private String value;          // 估值指标值

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
} 