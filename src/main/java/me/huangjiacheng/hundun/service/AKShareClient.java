package me.huangjiacheng.hundun.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class AKShareClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://localhost:8080/api/public/";

    /**
     * 通过AKTools HTTP API获取数据，所有参数均为英文，apiPath直接对应item_id。
     * @param apiPath AKShare HTTP API的item_id，如 "stock_profit_forecast_em"
     * @param request 参数实体，所有key均为英文
     * @return 返回接口数据字符串，失败时返回 null
     */
    public String fetchData(String apiPath, AKShareRequest request) {
        String url;
        if (request != null && containsChineseParam(request.toParamMap())) {
            // 直接拼接原始参数字符串，避免RestTemplate自动编码
            StringBuilder sb = new StringBuilder(BASE_URL).append(apiPath).append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : request.toParamMap().entrySet()) {
                if (!first) sb.append("&");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            url = sb.toString();
        } else {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + apiPath);
            if (request != null) {
                for (Map.Entry<String, String> entry : request.toParamMap().entrySet()) {
                    builder.queryParam(entry.getKey(), entry.getValue());
                }
            }
            url = builder.toUriString();
        }
        System.out.println("请求URL: " + url);
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("第" + attempt + "次尝试请求: " + apiPath);
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                System.out.println("响应状态码: " + response.getStatusCode());
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("请求成功，返回数据。");
                    return response.getBody();
                } else {
                    System.out.println("请求失败，响应内容: " + response.getBody());
                }
            } catch (Exception e) {
                System.err.println("第" + attempt + "次请求异常: " + e.getMessage());
                if (attempt == maxRetries) {
                    System.err.println("AKShare 数据获取失败: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 判断参数map中是否包含中文字符
     */
    private boolean containsChineseParam(Map<String, String> params) {
        for (String value : params.values()) {
            if (value != null && value.matches(".*[\u4e00-\u9fa5].*")) {
                return true;
            }
        }
        return false;
    }
} 