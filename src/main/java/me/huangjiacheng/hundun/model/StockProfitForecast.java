package me.huangjiacheng.hundun.model;

import lombok.Data;

@Data
public class StockProfitForecast {
    //股票代码
    String symbol;
    //预测年度
    Integer year;
    //预测平均值 单位：亿元
    Double forecastAverage;
}
