package me.huangjiacheng.hundun.model;

public class StockTurnoverData {
    private String date;           // 日期
    private String symbol;         // 股票代码
    private String turnover;       // 换手率

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getTurnover() { return turnover; }
    public void setTurnover(String turnover) { this.turnover = turnover; }
} 