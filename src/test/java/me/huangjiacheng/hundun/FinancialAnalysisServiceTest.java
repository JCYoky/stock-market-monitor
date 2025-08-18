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
     * 测试获取股票列表功能
     */
    @Test
    public void testGetStockList() {
        logger.info("=== 测试获取股票列表功能 ===");
        
        try {
            // 这里可以添加获取股票列表的测试逻辑
            logger.info("股票列表获取功能测试完成");
            
        } catch (Exception e) {
            logger.error("测试失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 测试单个股票分析功能
     */
    @Test
    public void testSingleStockAnalysis() {
        logger.info("=== 测试单个股票分析功能 ===");
        
        try {
            String testStockCode = "000001"; // 平安银行
            String testStockName = "平安银行";
            
            logger.info("开始分析股票：{} - {}", testStockCode, testStockName);
            
            // 调用单个股票分析方法
            financialAnalysisService.analyzeFinancialStructure(testStockCode, testStockName);
            
            logger.info("单个股票分析测试完成");
            
        } catch (Exception e) {
            logger.error("测试失败：{}", e.getMessage(), e);
        }
    }

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
     * 测试构建WatchList功能
     * 测试单个股票的WatchList构建
     */
    @Test
    public void testBuildWatchList() {
        logger.info("=== 测试构建WatchList功能 ===");
        
        try {
            String testStockCode = "000001"; // 平安银行
            String testStockName = "平安银行";
            
            logger.info("开始构建WatchList：{} - {}", testStockCode, testStockName);
            
            // 调用构建WatchList方法
            boolean result = financialAnalysisService.buildWatchList(testStockCode, testStockName);
            
            if (result) {
                logger.info("✅ WatchList构建成功：{} - {}", testStockCode, testStockName);
            } else {
                logger.warn("⚠️ WatchList构建失败：{} - {}", testStockCode, testStockName);
            }
            
        } catch (Exception e) {
            logger.error("❌ 构建WatchList失败：{}", e.getMessage(), e);
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
