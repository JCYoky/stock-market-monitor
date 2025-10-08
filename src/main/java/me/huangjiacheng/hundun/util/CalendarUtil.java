package me.huangjiacheng.hundun.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

public class CalendarUtil {

    private static final String API_URL = "https://api.jiejiariapi.com/v1/is_holiday?date=";

    private static final RestTemplate restTemplate = new RestTemplate();
    
    public static Boolean isTradeDate(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || 
            date.getDayOfWeek() == DayOfWeek.SUNDAY ||
            isHoliday(date)) {
            return false;
        }
        
        return true;
    }

    private static Boolean isHoliday(LocalDate date) {
        String url = API_URL + date.toString();
        Boolean result = false;//默认为false，即非节假日。
        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = JSONObject.parseObject(response);

            // 判断 API 返回是否成功
            if (json.getIntValue("code") != 0) {
                throw new RuntimeException("API调用失败: " + json.getString("msg"));
            }
            result = json.getBoolean("is_holiday");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 标准化日期格式为YYYY-MM-DD
     */
    public static String normalizeDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            
            String cleanDate = dateStr.trim();
            
            // 如果包含T，去掉时间部分
            if (cleanDate.contains("T")) {
                cleanDate = cleanDate.split("T")[0];
            }
            
            // 如果包含空格，去掉时间部分
            if (cleanDate.contains(" ")) {
                cleanDate = cleanDate.split(" ")[0];
            }
            
            // 检查是否为YYYY-MM-DD格式
            if (cleanDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return cleanDate;
            }
            
            // 检查是否为YYYY/MM/DD格式
            if (cleanDate.matches("\\d{4}/\\d{2}/\\d{2}")) {
                return cleanDate.replace("/", "-");
            }
            
            // 检查是否为YYYYMMDD格式
            if (cleanDate.matches("\\d{8}")) {
                return cleanDate.substring(0, 4) + "-" + 
                       cleanDate.substring(4, 6) + "-" + 
                       cleanDate.substring(6, 8);
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("标准化日期 " + dateStr + " 时发生错误: " + e.getMessage());
            return null;
        }
    }
}