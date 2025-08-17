package me.huangjiacheng.hundun;

import me.huangjiacheng.hundun.service.FinancialAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * FinancialAnalysisService测试类
 * 用于测试和启动构建所有股票StockWatchlist功能
 */
@SpringBootTest
@ActiveProfiles("test")
public class FinancialAnalysisServiceTest {

    @Autowired
    private FinancialAnalysisService financialAnalysisService;

    /**
     * 测试获取股票列表功能
     */
    @Test
    public void testGetStockList() {
        System.out.println("=== 测试获取股票列表功能 ===");
        
        try {
            // 这里可以添加获取股票列表的测试逻辑
            System.out.println("股票列表获取功能测试完成");
            
        } catch (Exception e) {
            System.err.println("测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试单个股票分析功能
     */
    @Test
    public void testSingleStockAnalysis() {
        System.out.println("=== 测试单个股票分析功能 ===");
        
        try {
            String testStockCode = "000001"; // 平安银行
            String testStockName = "平安银行";
            
            System.out.println("开始分析股票：" + testStockCode + " - " + testStockName);
            
            // 调用单个股票分析方法
            financialAnalysisService.analyzeFinancialStructure(testStockCode, testStockName);
            
            System.out.println("单个股票分析测试完成");
            
        } catch (Exception e) {
            System.err.println("测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 手动触发更新市危率操作
     * 这个方法可以单独运行来手动启动市危率计算
     */
    @Test
    public void testUpdateMarketRiskRatio() {
        System.out.println("=== 开始手动触发更新市危率操作 ===");
        
        try {
            // 手动调用市危率计算方法
            financialAnalysisService.calculateAndSaveMarketRiskRatio();
            
            System.out.println("=== 市危率更新操作已启动 ===");
            System.out.println("请查看控制台输出和后台日志了解执行情况");
            
        } catch (Exception e) {
            System.err.println("手动触发市危率更新失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
