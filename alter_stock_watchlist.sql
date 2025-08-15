-- 为stock_watchlist表添加新字段
-- 执行前请先备份数据库

USE stock-market-monitor;

-- 添加新字段
ALTER TABLE stock_watchlist 
ADD COLUMN pe_ttm DOUBLE COMMENT '市盈率TTM',
ADD COLUMN roe DOUBLE COMMENT '净资产收益率',
ADD COLUMN profit_quality DOUBLE COMMENT '盈利质量',
ADD COLUMN assets_quality DOUBLE COMMENT '资产质量',
ADD COLUMN pe_score DOUBLE COMMENT 'PE评分';

-- 查看表结构确认
DESCRIBE stock_watchlist;

