-- 创建市危率表
CREATE TABLE IF NOT EXISTS `market_risk_ratio` (
  `date` varchar(10) NOT NULL COMMENT '日期（主键，YYYY-MM-DD格式）',
  `market_risk_ratio` decimal(10,4) DEFAULT '0.0000' COMMENT '市危率',
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='市危率表';
