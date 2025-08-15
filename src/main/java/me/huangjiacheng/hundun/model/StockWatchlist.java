package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 自选股模型
 */
@Data
public class StockWatchlist {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 股票代码
     */
    private String stockCode;
    
    /**
     * 股票名称
     */
    private String stockName;
    
    /**
     * 股票类型：1为观察股、2为持仓股
     */
    private Integer stockType;
    
    /**
     * 创建时间
     */
    private String createdTime;
    
    /**
     * 更新时间
     */
    private String updatedTime;
}
