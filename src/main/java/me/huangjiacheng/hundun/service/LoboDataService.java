package me.huangjiacheng.hundun.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.huangjiacheng.hundun.model.StockValuationData;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

/**
 * 乐博数据服务
 * 用于处理乐博相关的数据操作
 */
@Service
@Slf4j
public class LoboDataService {
    
    /**
     * 批量获取股票市盈率数据
     * @param stockCode 股票代码
     * @return 市盈率数据
     */
    public List<StockValuationData> batchGetStockValuationData(String stockCode) {
        LocalDate beginDate = LocalDate.of(2005, 1, 1);
        LocalDate endDate = LocalDate.now();
        List<StockValuationData> result = new ArrayList<>();
        // 构建URL，获取该股票从beginDate到endDate的市盈率数据
        String url = String.format("https://gw.datayes.com/rrp_adventure/web/stockModel/band/%s?apiType=4&category=1&subCategory=1&flag=-1&beginDate=%s&endDate=%s",
                stockCode,
                beginDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        // 获取响应数据
        String response = readResponse(url);
        if (response == null) {
            log.error("No response received for stock: {}", stockCode);
            return null;
        }

        try {
            JSONObject jsonObject = JSONObject.parseObject(response);

            // 检查响应状态
            Integer code = jsonObject.getInteger("code");
            if (code == null || code != 1) {
                String message = jsonObject.getString("message");
                log.error("API returned error for stock {}: code={}, message={}", stockCode, code, message);
                return null;
            }

            log.info("Successfully retrieved PE band data for stock: {}", stockCode);

            // 获取嵌套的data对象
            JSONObject dataObject = jsonObject.getJSONObject("data");
            if (dataObject == null) {
                log.error("No data object found in response for stock: {}", stockCode);
                return null;
            }

            // 获取data数组
            JSONArray dataArray = dataObject.getJSONArray("data");
            if (dataArray == null || dataArray.isEmpty()) {
                log.warn("No PE band data available for stock: {}", stockCode);
                return null;
            }

            Map<LocalDate, JSONObject> dateToDataMap = new HashMap<>();
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject data = dataArray.getJSONObject(i);
                LocalDate tradeDate = this.parseDate(data.getString("tradeDate"));
                if (!tradeDate.isBefore(beginDate) && !tradeDate.isAfter(endDate)) {
                    dateToDataMap.put(tradeDate, data);
                }
            }

            while (!endDate.isBefore(beginDate)) {
                JSONObject data = dateToDataMap.get(endDate);
                if (data != null) {
                    result.add(buildStockValuationData(stockCode, data));
                }
                endDate = endDate.minusDays(1);
            }
            return result;
        } catch (Exception e) {
            log.error("Error processing PE band data for stock {}: {}", stockCode, e.getMessage(), e);
            return null;
        }
    }

    private StockValuationData buildStockValuationData(String stockCode, JSONObject data) {
        StockValuationData valuationData = new StockValuationData();
        valuationData.setSymbol(stockCode);
        // 设置基本信息
        valuationData.setDate(data.getString("tradeDate"));
        valuationData.setValue(data.getString("value"));
        return valuationData;
    }

    Map<String, LocalDate> dateCache = new HashMap<>();

    LocalDate parseDate(String dateStr) {
        return dateCache.computeIfAbsent(dateStr, LocalDate::parse);
    }

    /**
     * 从URL读取响应内容
     * @param urlString URL字符串
     * @return 响应内容
     */
    public String readResponse(String urlString) {
        int maxRetries = 10;
        int retryCount = 0;
        int retryDelayMs = 1000; // 1 second initial delay

        while (retryCount <= maxRetries) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json, text/plain, */*");
                conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
                conn.setRequestProperty("Accept-Language", "zh-cn");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Cookie", "gr_user_id=bc74eac7-a91c-4318-a497-386740088ce9; _ga=GA1.2.1137682322.1747531570; cloud-anonymous-token=3c4515dba09a4306a933130ee52bd781; _gid=GA1.2.49647166.1755511533; rrp-wechat-login-token=ce4da1d4-6e48-4b9a-b5ec-477cc053a654; cloud-sso-token=E3D4453E5952246A6AFF585873C73494; _gat=1; ba895d61f7404b76_gr_last_sent_sid_with_cs1=f94e70c9-d69f-4ea6-bedc-4a22e7ce4f6a; ba895d61f7404b76_gr_last_sent_cs1=10802237@wmcloud.com; ba895d61f7404b76_gr_session_id=f94e70c9-d69f-4ea6-bedc-4a22e7ce4f6a; ba895d61f7404b76_gr_session_id_sent_vst=f94e70c9-d69f-4ea6-bedc-4a22e7ce4f6a; ba895d61f7404b76_gr_cs1=10802237@wmcloud.com; _ga_YBHR4XSTK7=GS2.2.s1755511533$o23$g1$t1755512121$j49$l0$h0");
                conn.setRequestProperty("Dnt", "1");
                conn.setRequestProperty("Host", "gw.datayes.com");
                conn.setRequestProperty("Origin", "https://robo.datayes.com");
                conn.setRequestProperty("Pragma", "no-cache");
                conn.setRequestProperty("Referer", "https://robo.datayes.com/");
                conn.setRequestProperty("Sec-Ch-Ua", "\"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Google Chrome\";v=\"134\"");
                conn.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
                conn.setRequestProperty("Sec-Ch-Ua-Platform", "\"macOS\"");
                conn.setRequestProperty("Sec-Fetch-Dest", "empty");
                conn.setRequestProperty("Sec-Fetch-Mode", "cors");
                conn.setRequestProperty("Sec-Fetch-Site", "same-site");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
                conn.setConnectTimeout(10000); // 10 seconds connect timeout
                conn.setReadTimeout(30000);    // 30 seconds read timeout

                try {
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException("Server returned HTTP response code: " + conn.getResponseCode());
                    }
                    //处理gzip压缩数据
                    InputStream inputStream = conn.getInputStream();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream)))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        return response.toString();
                    }
                } finally {
                    conn.disconnect();
                }
            } catch (IOException e) {
                retryCount++;
                // Only retry for specific network errors
                if (retryCount <= maxRetries) {
                    log.warn("Network error occurred (attempt {}/{}): {}. Retrying in {} ms...",
                            retryCount, maxRetries, e.getMessage(), retryDelayMs);
                    try {
                        Thread.sleep(retryDelayMs);
                        // Exponential backoff
                        retryDelayMs *= 1.1;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.warn("已达到最大重试次数{}，请检查网络连接...", maxRetries);
                }
            }
        }
        log.warn("读取网络数据失败...");
        return null;
    }
    
}
