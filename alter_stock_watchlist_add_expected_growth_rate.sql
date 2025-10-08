-- 为stock_watchlist表添加expected_growth_rate字段
-- 执行日期: 2025-01-20
-- 说明: 新增预期净利润增长率字段，用于存储股票的预期增长率

-- 添加expected_growth_rate字段
ALTER TABLE stock_watchlist 
ADD COLUMN expected_growth_rate DECIMAL(10,4) DEFAULT NULL COMMENT '预期净利润增长率(%)';

-- 验证字段是否添加成功
DESCRIBE stock_watchlist;

-- 查看表结构
SHOW CREATE TABLE stock_watchlist;
