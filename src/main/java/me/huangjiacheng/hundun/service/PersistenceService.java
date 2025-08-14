package me.huangjiacheng.hundun.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import me.huangjiacheng.hundun.model.Evaluation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 数据持久化服务
 * 用于将分析结果永久保存到数据库或文件系统
 */
@Service
public class PersistenceService {
    
    /**
     * 将批量评估结果以Excel格式保存到本地
     * @param yearlyEvaluations batchEvaluate方法的返回数据
     * @return 是否保存成功
     */
    public boolean persistentBatchEvaluation(Map<String, List<Evaluation>> yearlyEvaluations) {
        try {
            // 创建工作簿和工作表
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("股票评估结果");
            
            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // 对年份进行排序，从大到小（降序）
            List<String> sortedYears = new ArrayList<>(yearlyEvaluations.keySet());
            sortedYears.sort((y1, y2) -> {
                try {
                    int year1 = Integer.parseInt(y1);
                    int year2 = Integer.parseInt(y2);
                    return Integer.compare(year2, year1); // 降序排列
                } catch (NumberFormatException e) {
                    // 如果年份不是纯数字，按字符串降序排列
                    return y2.compareTo(y1);
                }
            });
            
            System.out.println("排序后的年份顺序: " + sortedYears);
            
            // 设置表头（第一行为年份，每个年份占两列）
            Row headerRow = sheet.createRow(0);
            int colIndex = 0;
            
            // 为每个年份创建两列：股票名称和得分，按排序后的年份顺序
            for (String year : sortedYears) {
                // 股票名称列
                Cell nameHeaderCell = headerRow.createCell(colIndex++);
                nameHeaderCell.setCellValue(year + "-股票名称");
                nameHeaderCell.setCellStyle(headerStyle);
                
                // 得分列
                Cell scoreHeaderCell = headerRow.createCell(colIndex++);
                scoreHeaderCell.setCellValue(year + "-得分");
                scoreHeaderCell.setCellStyle(headerStyle);
            }
            
            // 获取所有年份中最大的评估数量，用于确定行数
            int maxEvaluations = 0;
            for (List<Evaluation> evaluations : yearlyEvaluations.values()) {
                maxEvaluations = Math.max(maxEvaluations, evaluations.size());
            }
            
            // 填充数据行，每行对应一个排名位置
            for (int rowIndex = 1; rowIndex <= maxEvaluations; rowIndex++) {
                Row dataRow = sheet.createRow(rowIndex);
                colIndex = 0;
                
                // 为每个年份填充数据，按排序后的年份顺序
                for (String year : sortedYears) {
                    List<Evaluation> yearEvaluations = yearlyEvaluations.get(year);
                    
                    // 按得分从大到小排序
                    List<Evaluation> sortedEvaluations = new ArrayList<>(yearEvaluations);
                    sortedEvaluations.sort((e1, e2) -> {
                        if (e1.getScore() == null && e2.getScore() == null) return 0;
                        if (e1.getScore() == null) return 1;
                        if (e2.getScore() == null) return -1;
                        return e2.getScore().compareTo(e1.getScore()); // 降序排列
                    });
                    
                    // 填充股票名称
                    Cell nameCell = dataRow.createCell(colIndex++);
                    if (rowIndex - 1 < sortedEvaluations.size()) {
                        Evaluation evaluation = sortedEvaluations.get(rowIndex - 1);
                        nameCell.setCellValue(evaluation.getStockName() + "(" + evaluation.getSymbol() + ")");
                    } else {
                        nameCell.setCellValue("-");
                    }
                    
                    // 填充得分
                    Cell scoreCell = dataRow.createCell(colIndex++);
                    if (rowIndex - 1 < sortedEvaluations.size()) {
                        Integer score = sortedEvaluations.get(rowIndex - 1).getScore();
                        if (score != null) {
                            scoreCell.setCellValue(score);
                        } else {
                            scoreCell.setCellValue("-");
                        }
                    } else {
                        scoreCell.setCellValue("-");
                    }
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 生成文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "股票评估结果_" + timestamp + ".xlsx";
            
            System.out.println("正在保存Excel文件: " + fileName);
            System.out.println("包含年份数量: " + sortedYears.size());
            System.out.println("最大评估数量: " + maxEvaluations);
            
            // 保存文件
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }
            
            workbook.close();
            System.out.println("Excel文件保存成功: " + fileName);
            return true;
            
        } catch (IOException e) {
            System.err.println("保存Excel文件失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("处理数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 测试方法：验证年份排序和数据对应关系
     * @param yearlyEvaluations 测试数据
     */
    public void testYearSorting(Map<String, List<Evaluation>> yearlyEvaluations) {
        System.out.println("=== 测试年份排序和数据对应关系 ===");
        
        // 对年份进行排序，从大到小（降序）
        List<String> sortedYears = new ArrayList<>(yearlyEvaluations.keySet());
        sortedYears.sort((y1, y2) -> {
            try {
                int year1 = Integer.parseInt(y1);
                int year2 = Integer.parseInt(y2);
                return Integer.compare(year2, year1); // 降序排列
            } catch (NumberFormatException e) {
                // 如果年份不是纯数字，按字符串降序排列
                return y2.compareTo(y1);
            }
        });
        
        System.out.println("原始年份顺序: " + new ArrayList<>(yearlyEvaluations.keySet()));
        System.out.println("排序后年份顺序: " + sortedYears);
        
        // 验证每个年份的数据
        for (String year : sortedYears) {
            List<Evaluation> evaluations = yearlyEvaluations.get(year);
            System.out.println("年份 " + year + " 包含 " + evaluations.size() + " 个评估结果");
            
            // 按得分排序
            List<Evaluation> sortedEvaluations = new ArrayList<>(evaluations);
            sortedEvaluations.sort((e1, e2) -> {
                if (e1.getScore() == null && e2.getScore() == null) return 0;
                if (e1.getScore() == null) return 1;
                if (e2.getScore() == null) return -1;
                return e2.getScore().compareTo(e1.getScore()); // 降序排列
            });
            
            // 显示前3名的结果
            System.out.println("  前3名结果:");
            for (int i = 0; i < Math.min(3, sortedEvaluations.size()); i++) {
                Evaluation eval = sortedEvaluations.get(i);
                System.out.println("    " + (i + 1) + ". " + eval.getStockName() + 
                                 "(" + eval.getSymbol() + ") - 得分: " + eval.getScore());
            }
        }
        
        System.out.println("=== 测试完成 ===");
    }
    
}

