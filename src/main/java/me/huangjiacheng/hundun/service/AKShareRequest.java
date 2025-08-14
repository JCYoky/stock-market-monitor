package me.huangjiacheng.hundun.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于构建AKShare HTTP API的请求参数，所有key均为英文。
 */
public class AKShareRequest {
    private String symbol;
    private String indicator;
    private String period;
    private String date;
    private String stock;//港股股票代码
    private Map<String, String> extraParams = new HashMap<>();

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getIndicator() { return indicator; }
    public void setIndicator(String indicator) { this.indicator = indicator; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }

    public Map<String, String> getExtraParams() { return extraParams; }
    public void setExtraParam(String key, String value) { this.extraParams.put(key, value); }

    /**
     * 转为query参数map，所有key均为英文，且只包含非空参数。
     */
    public Map<String, String> toParamMap() {
        Map<String, String> params = new HashMap<>();
        if (symbol != null && !symbol.isEmpty()) params.put("symbol", symbol);
        if (stock != null && !stock.isEmpty()) params.put("stock", stock);
        if (indicator != null && !indicator.isEmpty()) params.put("indicator", indicator);
        if (period != null && !period.isEmpty()) params.put("period", period);
        if (date != null && !date.isEmpty()) params.put("date", date);
        params.putAll(extraParams);
        return params;
    }
} 