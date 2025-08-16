package me.huangjiacheng.hundun.controller;

import me.huangjiacheng.hundun.service.FinancialAnalysisService;
import me.huangjiacheng.hundun.service.AKShareService;
import me.huangjiacheng.hundun.model.StockInfo;
import me.huangjiacheng.hundun.model.StockValuationData;
import me.huangjiacheng.hundun.model.StockFinancialAbstractThs;
import me.huangjiacheng.hundun.model.StockFinancialHkBalanceSheet;
import me.huangjiacheng.hundun.model.StockFinancialHkIncomeStatement;
import me.huangjiacheng.hundun.model.StockFinancialHkCashFlow;
import me.huangjiacheng.hundun.model.StockHkBasicInfo;
import me.huangjiacheng.hundun.model.StockShareholderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import me.huangjiacheng.hundun.service.AKShareRequest;
import me.huangjiacheng.hundun.service.AKShareClient;
import me.huangjiacheng.hundun.mapper.MarketRiskRatioMapper;
import me.huangjiacheng.hundun.model.MarketRiskRatio;

/**
 * 张新民结构性财务分析API控制器
 */
@RestController
@RequestMapping("/api/zxm")
public class AnalysisApiController {
    
    @Autowired
    private FinancialAnalysisService zxmFinancialAnalysisService;
    
    @Autowired
    private AKShareService akShareService;
    
    @Autowired
    private AKShareClient akShareClient;
    
    @Autowired
    private MarketRiskRatioMapper marketRiskRatioMapper;

    
    /**
     * 获取股票综合分析数据（包括负债结构和资产结构）
     * @param symbol 股票代码
     * @param stockName 股票名称
     * @return 综合分析结果
     */
    @GetMapping("/comprehensive-analysis")
    public Map<String, Object> getComprehensiveAnalysis(@RequestParam String symbol, @RequestParam(required = false) String stockName) {
        try {
            return zxmFinancialAnalysisService.analyzeFinancialStructure(symbol, stockName);
        } catch (Exception e) {
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "分析失败：" + e.getMessage());
            errorResult.put("symbol", symbol);
            return errorResult;
        }
    }
    
    /**
     * 获取股票基本信息
     * @param symbol 股票代码
     * @return 股票基本信息
     */
    @GetMapping("/stock-info")
    public StockInfo getStockInfo(@RequestParam String symbol) {
        try {
            return akShareService.getStockIndividualInfo(symbol);
        } catch (Exception e) {
            StockInfo errorInfo = new StockInfo();
            errorInfo.setCode(symbol);
            errorInfo.setName("获取失败");
            return errorInfo;
        }
    }
    
    /**
     * 获取全部A股股票列表
     * @return 股票列表
     */
    @GetMapping("/stock-list")
    public List<StockInfo> getStockList() {
        try {
            return akShareService.getStockList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取港股通成分股列表
     * @return 港股列表
     */
    @GetMapping("/hk-stock-list")
    public List<StockInfo> getHKStockList() {
        try {
            return akShareService.getHKStockList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取股票历史市盈率数据
     * @param symbol 股票代码
     * @return 历史市盈率数据
     */
    @GetMapping("/valuation-data")
    public List<StockValuationData> getValuationData(@RequestParam String symbol) {
        try {
            return akShareService.getStockValuationData(symbol);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取股票财务摘要历史数据
     * @param symbol 股票代码
     * @return 财务摘要历史数据
     */
    @GetMapping("/financial-abstract-data")
    public List<StockFinancialAbstractThs> getFinancialAbstractData(@RequestParam String symbol) {
        try {
            return akShareService.getStockFinancialAbstractThs(symbol);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取股票换手率分布分析数据
     * @param symbol 股票代码
     * @return 换手率分布分析结果
     */
    @GetMapping("/turnover-distribution")
    public Map<String, Object> getTurnoverDistribution(@RequestParam String symbol) {
        try {
            return zxmFinancialAnalysisService.analyzeTurnoverDistribution(symbol);
        } catch (Exception e) {
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "获取换手率分布分析失败：" + e.getMessage());
            errorResult.put("symbol", symbol);
            return errorResult;
        }
    }
    
    /**
     * 获取股东户数数据
     * @param symbol 股票代码
     * @return 股东户数数据
     */
    @GetMapping("/shareholder-data")
    public Map<String, Object> getShareholderData(@RequestParam String symbol) {
        try {
            List<StockShareholderData> shareholderData = akShareService.getStockShareholderData(symbol);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("symbol", symbol);
            result.put("shareholderData", shareholderData);
            
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "获取股东户数数据失败：" + e.getMessage());
            errorResult.put("symbol", symbol);
            return errorResult;
        }
    }
    
    /**
     * 获取港股资产负债表数据
     * @param stock 股票代码（5位数字）
     * @return 港股资产负债表数据列表
     */
    @GetMapping("/hk-balance-sheet")
    public List<StockFinancialHkBalanceSheet> getHkBalanceSheet(@RequestParam String stock) {
        try {
            return akShareService.getStockFinancialHkBalanceSheet(stock);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取港股利润表数据
     * @param stock 股票代码（5位数字）
     * @return 港股利润表数据列表
     */
    @GetMapping("/hk-income-statement")
    public List<StockFinancialHkIncomeStatement> getHkIncomeStatement(@RequestParam String stock) {
        try {
            return akShareService.getStockFinancialHkIncomeStatement(stock);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取港股现金流量表数据
     * @param stock 股票代码（5位数字）
     * @return 港股现金流量表数据列表
     */
    @GetMapping("/hk-cash-flow")
    public List<StockFinancialHkCashFlow> getHkCashFlow(@RequestParam String stock) {
        try {
            return akShareService.getStockFinancialHkCashFlow(stock);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 港股综合分析
     * @param stock 股票代码（5位数字）
     * @param stockName 股票名称
     * @return 综合分析结果
     */
    @GetMapping("/hk-financial-analysis")
    public Map<String, Object> analyzeHkFinancialStructure(@RequestParam String stock, @RequestParam(required = false) String stockName) {
        try {
            return zxmFinancialAnalysisService.analyzeHkFinancialStructure(stock, stockName);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "分析失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取港股基本信息
     * @param stock 港股代码（5位数字）
     * @return StockHkBasicInfo对象
     */
    @GetMapping("/hk-basic-info")
    public StockHkBasicInfo getHkBasicInfo(@RequestParam String stock) {
        try {
            return akShareService.getStockHkBasicInfo(stock);
        } catch (Exception e) {
            System.err.println("获取港股基本信息失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取港股换手率分布数据
     * @param stock 港股代码
     * @return 换手率分布数据
     */
    @GetMapping("/hk-turnover-distribution")
    public Map<String, Object> getHkTurnoverDistribution(@RequestParam String stock) {
        try {
            return zxmFinancialAnalysisService.analyzeHkTurnoverDistribution(stock);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "港股换手率分布分析失败：" + e.getMessage());
            errorResult.put("stock", stock);
            return errorResult;
        }
    }

    @GetMapping("/hk-income-statement-raw")
    public ResponseEntity<String> getHkIncomeStatementRaw(@RequestParam String stock) {
        try {
            AKShareRequest request = new AKShareRequest();
            request.setStock(stock);
            request.setExtraParam("symbol", "利润表");
            request.setIndicator("年度");
            String json = akShareClient.fetchData("stock_financial_hk_report_em", request);
            
            if (json == null) {
                return ResponseEntity.ok("{\"error\": \"数据获取失败\"}");
            }
            
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.ok("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/hk-cash-flow-raw")
    public ResponseEntity<String> getHkCashFlowRaw(@RequestParam String stock) {
        try {
            AKShareRequest request = new AKShareRequest();
            request.setStock(stock);
            request.setExtraParam("symbol", "现金流量表");
            request.setIndicator("年度");
            String json = akShareClient.fetchData("stock_financial_hk_report_em", request);
            
            if (json == null) {
                return ResponseEntity.ok("{\"error\": \"数据获取失败\"}");
            }
            
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.ok("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * 计算并保存市危率数据（从数据库最新日期开始计算）
     * @return 计算结果
     */
    @PostMapping("/calculate-market-risk-ratio")
    public ResponseEntity<Map<String, Object>> calculateMarketRiskRatio() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("开始调用市危率计算接口...");
            
            // 在后台线程中执行计算，避免阻塞
            new Thread(() -> {
                try {
                    zxmFinancialAnalysisService.calculateAndSaveMarketRiskRatio();
                } catch (Exception e) {
                    System.err.println("后台计算市危率时发生错误: " + e.getMessage());
                }
            }).start();
            
            result.put("success", true);
            result.put("message", "市危率计算已启动，请查看后台日志");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "启动市危率计算失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取所有市危率数据
     * @return 市危率数据列表
     */
    @GetMapping("/market-risk-ratio/all")
    public ResponseEntity<Map<String, Object>> getAllMarketRiskRatios() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用Mapper获取所有市危率数据
            List<MarketRiskRatio> marketRiskRatios = marketRiskRatioMapper.selectAll();
            
            if (marketRiskRatios != null && !marketRiskRatios.isEmpty()) {
                result.put("success", true);
                result.put("data", marketRiskRatios);
                result.put("message", "获取市危率数据成功，共 " + marketRiskRatios.size() + " 条记录");
            } else {
                result.put("success", true);
                result.put("data", new ArrayList<>());
                result.put("message", "暂无市危率数据，请先点击'更新市危率'按钮计算数据");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取市危率数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

} 