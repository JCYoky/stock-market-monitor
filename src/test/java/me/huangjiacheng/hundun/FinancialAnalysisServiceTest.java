package me.huangjiacheng.hundun;

import me.huangjiacheng.hundun.service.FinancialAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FinancialAnalysisService测试类
 * 用于测试和启动构建所有股票StockWatchlist功能
 */
@SpringBootTest
@ActiveProfiles("test")
public class FinancialAnalysisServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(FinancialAnalysisServiceTest.class);

    @Autowired
    private FinancialAnalysisService financialAnalysisService;

    /**
     * 手动触发更新市危率操作
     * 这个方法可以单独运行来手动启动市危率计算
     */
    @Test
    public void testUpdateMarketRiskRatio() {
        logger.info("🔥 === 开始手动触发更新市危率操作 ===");
        logger.info("🔥 当前时间：{}", java.time.LocalDateTime.now());
        
        try {
            // 手动调用市危率计算方法
            logger.info("🔥 正在调用calculateAndSaveMarketRiskRatio方法...");
            financialAnalysisService.calculateAndSaveMarketRiskRatio();
            
            logger.info("🔥 === 市危率更新操作已完成 ===");
            logger.info("🔥 请查看控制台输出和后台日志了解执行情况");
            
        } catch (Exception e) {
            logger.error("❌ 手动触发市危率更新失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 测试为我的股票构建WatchList功能
     * 测试批量更新type为1或2的股票
     */
    @Test
    public void testBuildWatchListForMyStocks() {
        logger.info("=== 测试为我的股票构建WatchList功能 ===");
        
        try {
            logger.info("开始为我的股票构建WatchList...");
            
            // 调用为我的股票构建WatchList方法
            financialAnalysisService.buildWatchListForMyStocks();
            
            logger.info("✅ 为我的股票构建WatchList完成");
            
        } catch (Exception e) {
            logger.error("❌ 为我的股票构建WatchList失败：{}", e.getMessage(), e);
        }
    }
}
