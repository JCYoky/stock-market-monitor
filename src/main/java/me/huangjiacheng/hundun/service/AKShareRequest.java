package me.huangjiacheng.hundun.service;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 用于构建AKShare HTTP API的请求参数，所有key均为英文。
 */
@Data
public class AKShareRequest {
    private String symbol;
    private String indicator;
    private String period;
    private String date;
    private String stock;//港股股票代码
    private Map<String, String> extraParams = new HashMap<>();
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