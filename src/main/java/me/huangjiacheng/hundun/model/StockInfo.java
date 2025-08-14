package me.huangjiacheng.hundun.model;

/**
 * 股票基本信息模型
 */
public class StockInfo {
    private String code;           // 股票代码
    private String name;           // 股票简称
    private String latestPrice;    // 最新价格
    private String totalShares;    // 总股本
    private String circulatingShares; // 流通股
    private String totalMarketValue; // 总市值
    private String circulatingMarketValue; // 流通市值
    private String industry;       // 行业
    private String listingDate;    // 上市时间
    private String roe;            // ROE (净资产收益率)
    private String pe;             // 市盈率
    
    // 默认构造函数
    public StockInfo() {
    }
    
    // Getter 和 Setter 方法
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLatestPrice() {
        return latestPrice;
    }
    
    public void setLatestPrice(String latestPrice) {
        this.latestPrice = latestPrice;
    }
    
    public String getTotalShares() {
        return totalShares;
    }
    
    public void setTotalShares(String totalShares) {
        this.totalShares = totalShares;
    }
    
    public String getCirculatingShares() {
        return circulatingShares;
    }
    
    public void setCirculatingShares(String circulatingShares) {
        this.circulatingShares = circulatingShares;
    }
    
    public String getTotalMarketValue() {
        return totalMarketValue;
    }
    
    public void setTotalMarketValue(String totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }
    
    public String getCirculatingMarketValue() {
        return circulatingMarketValue;
    }
    
    public void setCirculatingMarketValue(String circulatingMarketValue) {
        this.circulatingMarketValue = circulatingMarketValue;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getListingDate() {
        return listingDate;
    }
    
    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }
    
    public String getRoe() {
        return roe;
    }
    
    public void setRoe(String roe) {
        this.roe = roe;
    }
    
    public String getPe() {
        return pe;
    }
    
    public void setPe(String pe) {
        this.pe = pe;
    }
    
    @Override
    public String toString() {
        return "StockInfo{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", latestPrice='" + latestPrice + '\'' +
                ", totalShares='" + totalShares + '\'' +
                ", circulatingShares='" + circulatingShares + '\'' +
                ", totalMarketValue='" + totalMarketValue + '\'' +
                ", circulatingMarketValue='" + circulatingMarketValue + '\'' +
                ", industry='" + industry + '\'' +
                ", listingDate='" + listingDate + '\'' +
                ", roe='" + roe + '\'' +
                ", pe='" + pe + '\'' +
                '}';
    }
} 