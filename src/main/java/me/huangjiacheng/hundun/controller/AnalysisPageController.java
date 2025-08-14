package me.huangjiacheng.hundun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnalysisPageController {
    /**
     * 显示张新民结构性财务分析主页面
     */
    @GetMapping("/zxm-analysis")
    public String showZxmAnalysisPage() {
        return "forward:/zxm-analysis.html";
    }
} 