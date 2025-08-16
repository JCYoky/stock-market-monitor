package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 市危率模型
 */
@Data
public class MarketRiskRatio {
    
    /**
     * 日期（主键）
     */
    private String date;
    
    /**
     * 市危率
     */
    private Double marketRiskRatio;
}
