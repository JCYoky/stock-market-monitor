package me.huangjiacheng.hundun.model;

/**
 * 股票评估模型
 * 用于存储股票的评估结果信息
 */
public class Evaluation {
    
    /**
     * 年份
     */
    private String year;
    
    /**
     * 股票代码
     */
    private String symbol;
    
    /**
     * 股票名称
     */
    private String stockName;
    
    /**
     * 评估分数
     */
    private Integer score;
    
    /**
     * 净资产收益率 (ROE)
     */
    private Double roe;
    
    /**
     * 市盈率TTM (滚动市盈率)
     */
    private Double peTtm;
    
    // 默认构造函数
    public Evaluation() {
    }
    
    // 带所有参数的构造函数
    public Evaluation(String year, String symbol, String stockName, Integer score, Double roe, Double peTtm) {
        this.year = year;
        this.symbol = symbol;
        this.stockName = stockName;
        this.score = score;
        this.roe = roe;
        this.peTtm = peTtm;
    }
    
    // Getter和Setter方法
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public Double getRoe() {
        return roe;
    }
    
    public void setRoe(Double roe) {
        this.roe = roe;
    }
    
    public Double getPeTtm() {
        return peTtm;
    }
    
    public void setPeTtm(Double peTtm) {
        this.peTtm = peTtm;
    }
    
    @Override
    public String toString() {
        return "Evaluation{" +
                "year='" + year + '\'' +
                ", symbol='" + symbol + '\'' +
                ", stockName='" + stockName + '\'' +
                ", score=" + score +
                ", roe=" + roe +
                ", peTtm=" + peTtm +
                '}';
    }
}
