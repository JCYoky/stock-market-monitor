package me.huangjiacheng.hundun.model;

import lombok.Data;

/**
 * 港股资产负债表数据实体类（全字段版）
 * 按照通行资产负债表格式排序：资产=负债+所有者权益
 */
@Data
public class StockFinancialHkBalanceSheet {
    // 报告期
    private String reportPeriod;
    // 股票代码
    private String stockCode;
    // 股票名称
    private String stockName;

    // ==================== 资产部分 ====================
    // 流动资产
    private String liuDongZiChanHeJi; // 流动资产合计
    private String xianJinJiDengJiaWu; // 现金及等价物
    private String duanQiCunKuan; // 短期存款 
    private String shouXianZhiCunKuanJiXianJin; // 受限制存款及现金
    private String zhongChangQiCunKuan; // 中长期存款
    private String jiaoYiXingJinRongZiChanLiuDong; // 交易性金融资产(流动)
    private String qiTaJinRongZiChanLiuDong; // 其他金融资产(流动)
    private String yanShengJinRongGongJuZiChanLiuDong; // 衍生金融工具-资产(流动)
    private String yingShouZhangKuan; // 应收帐款
    private String yingShouGuanLianFangKuanXiang; // 应收关联方款项
    private String yuFuKuanAnJinJiQiTaYingShouKuan; // 预付款按金及其他应收款
    private String yuFuKuanXiang; // 预付款项
    private String cunHuo; // 存货
    private String chiZuoChuShouDeZiChanLiuDong; // 持作出售的资产(流动)
    private String chiYouZhiDaoQiTouZiLiuDong; // 持有至到期投资(流动)
    private String zhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChanLiuDong; // 指定以公允价值记账之金融资产(流动)
    private String jingLiuDongZiChan; // 净流动资产

    // 非流动资产
    private String feiLiuDongZiChanHeJi; // 非流动资产合计
    private String guDingZiChan; // 固定资产
    private String wuYeChangFangJiSheBei; // 物业厂房及设备
    private String zaiJianGongCheng; // 在建工程
    private String touZiWuYe; // 投资物业
    private String tuDiShiYongQuan; // 土地使用权
    private String wuXingZiChan; // 无形资产
    private String shangYu; // 商誉
    private String diYanShuiXiangZiChan; // 递延税项资产
    private String keGongChuShouTouZi; // 可供出售投资
    private String chiYouZhiDaoQiTouZi; // 持有至到期投资
    private String zhiDingYiGongYunJiaZhiJiZhangZhiJinRongZiChan; // 指定以公允价值记账之金融资产
    private String qiTaJinRongZiChanFeiLiuDong; // 其他金融资产(非流动)
    private String lianYingGongSiQuanYi; // 联营公司权益
    private String heYingGongSiQuanYi; // 合营公司权益
    private String yuLianYingGongSiKeShuHuiGongJuDeTouZi; // 于联营公司可赎回工具的投资
    private String feiLiuDongZiChanQiTaXiangMu; // 非流动资产其他项目

    // 资产总计
    private String zongZiChan; // 总资产
    private String zongZiChanJianLiuDongFuZhai; // 总资产减流动负债
    private String zongZiChanJianZongFuZhaiHeJi; // 总资产减总负债合计

    // ==================== 负债部分 ====================
    // 流动负债
    private String liuDongFuZhaiHeJi; // 流动负债合计
    private String duanQiDaiKuan; // 短期贷款
    private String yingFuZhangKuan; // 应付帐款
    private String yingFuPiaoJu; // 应付票据
    private String yingFuPiaoJuFeiLiuDong; // 应付票据(非流动)
    private String yingFuShuiXiang; // 应付税项
    private String yingFuGuLi; // 应付股利
    private String yingFuGuanLianFangKuanXiangLiuDong; // 应付关联方款项(流动)
    private String qiTaYingFuKuanJiYingJiFeiYong; // 其他应付款及应计费用
    private String qiTaJinRongFuZhaiLiuDong; // 其他金融负债(流动)
    private String yanShengJinRongGongJuFuZhaiLiuDong; // 衍生金融工具-负债(流动)
    private String rongZiZuLinFuZhaiLiuDong; // 融资租赁负债(流动)
    private String diYanShouRuLiuDong; // 递延收入(流动)

    // 非流动负债
    private String feiLiuDongFuZhaiHeJi; // 非流动负债合计
    private String changQiDaiKuan; // 长期贷款
    private String changQiYingFuKuan; // 长期应付款
    private String diYanShuiXiangFuZhai; // 递延税项负债
    private String qiTaJinRongFuZhaiFeiLiuDong; // 其他金融负债(非流动)
    private String rongZiZuLinFuZhaiFeiLiuDong; // 融资租赁负债(非流动)
    private String diYanShouRuFeiLiuDong; // 递延收入(非流动)

    // 负债总计
    private String zongFuZhai; // 总负债

    // ==================== 所有者权益部分 ====================
    private String guDongQuanYi; // 股东权益
    private String guBen; // 股本
    private String guBenYiJia; // 股本溢价
    private String chuBei; // 储备
    private String qiTaChuBei; // 其他储备
    private String baoLiuYiLiLeiJiKuiSun; // 保留溢利(累计亏损)
    private String kuCunGu; // 库存股
    private String shaoShuGuDongQuanYi; // 少数股东权益
    private String zongQuanYi; // 总权益
    private String zongQuanYiJiZongFuZhai; // 总权益及总负债
    private String zongQuanYiJiFeiLiuDongFuZhai; // 总权益及非流动负债
    private String jingZiChan; // 净资产
    
    // ==================== 其他可能出现的字段 ====================
    private String keZhuanHuanPiaoJuJiZhaiQuan; // 可转换票据及债券
    private String heTongFuZhai; // 合同负债
    private String yingFuZhaiQuan; // 应付债券
    private String liuDongZiChanQiTaXiangMu; // 流动资产其他项目
    private String daiKuanJiDianKuan; // 贷款及垫款
    private String changQiYingShouKuan; // 长期应收款
    private String boBeiLiuDong; // 拨备(流动)
    private String boBeiFeiLiuDong; // 拨备(非流动)
    private String chiZuoChuShouDeFuZhaiLiuDong; // 持作出售的负债(流动)
    private String gongJiJin; // 公积金
    private String qiTaTouZi; // 其他投资
    private String qiTaZongHeXingShouYi; // 其他综合性收益
    private String qiTaFeiLiuDongFuZhai; // 其他非流动负债
    private String waiBiBaoBiaoZheSuanChaE; // 外币报表折算差额
    private String liuDongFuZhaiQiTaXiangMu; // 流动负债其他项目
    private String duanQiTouZi; // 短期投资
    private String guDongQuanYiQiTaXiangMu; // 股东权益其他项目
    private String zhengQuanTouZi; // 证券投资
    private String zhongGuChuBei; // 重估储备
    private String feiLiuDongFuZhaiQiTaXiangMu; // 非流动负债其他项目
    private String feiYunSuanXiangMu; // 非运算项目


} 