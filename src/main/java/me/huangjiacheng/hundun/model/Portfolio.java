package me.huangjiacheng.hundun.model;

import lombok.Data;

import java.util.List;

/**
 * 资产组合模型
 */
@Data
public class Portfolio {

    List<Holdings> holdings; //持仓股列表

    Long totalValue; //总市值
    Integer holdingCount; //持仓股数量
    Integer industryCount; //行业数量
    Double expectedReturn; //预期收益率
}
