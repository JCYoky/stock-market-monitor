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
     * 股票类型：1为观察股、2为持仓股、0为其他
     */
    private Integer stockType;
    /**
     * 市盈率TTM
     */
    private Double peTtm;
    /**
     * 净资产收益率
     */
    private Double roe;
    /**
     * 盈利质量
     */
    private Double profitQuality;
    /**
     * 资产质量
     */
    private Double assetsQuality;
    /**
     * PE评分
     */
    private Double peScore;
    /**
     * 创建时间
     */
    private String createdTime;
    /**
     * 更新时间
     */
    private String updatedTime;
}
