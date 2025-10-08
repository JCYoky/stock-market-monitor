package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 持仓股数据模型
 */
@Data
public class Holdings {
    String symbol; //股票代码
    String name; //股票名称
    String industry; //所属行业
    Double price; //当前价格
    Integer shares; //持仓数量
    Double marketValue; //市值
    Double expectedReturn; //预期收益率
    Double holdingWeight; //持仓占比
}
