# 股票市场监控系统 (Stock Market Monitor)

一个基于Spring Boot和AKShare的智能财务分析系统，提供A股和港股的财务数据分析、评估和可视化展示。

## 🚀 功能特性

### 📊 财务分析
- **A股分析**: 资产负债表、利润表、现金流量表、财务比率等
- **港股分析**: 综合财务报告、财务结构分析
- **数据可视化**: 基于ECharts的交互式图表展示
- **批量分析**: 支持多股票批量财务分析

### 🔍 数据展示
- **按报告期展示**: 支持季度、半年度、年度数据展示
- **按年度展示**: 自动聚合季度数据为年度数据
- **智能切换**: 前端开关控制数据展示模式
- **实时数据**: 通过AKShare API获取最新财务数据

### 💡 智能评估
- **综合评分**: 基于财务指标的综合评估体系
- **ROE分析**: 净资产收益率分析
- **PE-TTM**: 滚动市盈率分析
- **趋势分析**: 历史数据趋势可视化

## 🛠️ 技术栈

### 后端
- **Java 17+**
- **Spring Boot 3.x**
- **Maven**
- **AKShare API**: 金融数据接口

### 前端
- **HTML5 + CSS3**
- **JavaScript (ES6+)**
- **ECharts**: 数据可视化图表库
- **响应式设计**: 支持移动端和桌面端

### 数据源
- **同花顺**: A股财务数据
- **东方财富**: 港股财务数据
- **实时API**: 股票基本信息

## 📋 系统要求

- Java 17 或更高版本
- Maven 3.6+
- 网络连接 (用于访问AKShare API)

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/yourusername/stock-market-monitor.git
cd stock-market-monitor
```

### 2. 编译项目
```bash
mvn clean compile
```

### 3. 运行应用
```bash
mvn spring-boot:run
```

### 4. 访问系统
打开浏览器访问: http://localhost:8080

## 📖 使用说明

### A股分析
1. 在输入框中输入A股代码 (如: 600519)
2. 点击"开始分析"按钮
3. 系统将自动获取并分析财务数据
4. 查看各类财务图表和分析结果

### 港股分析
1. 在输入框中输入港股代码 (如: 00700)
2. 点击"开始分析"按钮
3. 系统将获取港股财务报告数据
4. 查看财务结构分析图表

### 数据展示模式
- **按报告期**: 显示所有报告期数据 (默认)
- **按年度**: 自动聚合为年度数据展示
- 使用页面左侧开关进行模式切换

### 批量分析
1. 点击"批量评分"按钮
2. 输入多个股票代码 (用逗号分隔)
3. 系统将批量分析并生成评估报告

## 🏗️ 项目结构

```
stock-market-monitor/
├── src/main/java/me/huangjiacheng/hundun/
│   ├── controller/          # 控制器层
│   │   ├── AnalysisApiController.java      # API接口
│   │   ├── AnalysisPageController.java     # 页面控制器
│   │   └── BatchAnalysisApiController.java # 批量分析接口
│   ├── model/               # 数据模型
│   │   ├── Evaluation.java                 # 评估结果模型
│   │   ├── StockInfo.java                  # 股票基本信息
│   │   ├── StockFinancialDebtThs.java     # A股负债表
│   │   ├── StockFinancialHkBalanceSheet.java # 港股资产负债表
│   │   └── ...                            # 其他财务数据模型
│   ├── service/             # 业务逻辑层
│   │   ├── AKShareService.java            # AKShare数据服务
│   │   ├── FinancialAnalysisService.java  # 财务分析服务
│   │   ├── BatchAnalysisService.java      # 批量分析服务
│   │   └── PersistenceService.java        # 数据持久化服务
│   └── HundunApplication.java             # 主应用类
├── src/main/resources/
│   ├── static/
│   │   └── zxm-analysis.html              # 前端页面
│   └── application.properties              # 应用配置
├── pom.xml                                 # Maven配置
└── README.md                               # 项目说明
```

## 🔧 配置说明

### application.properties
```properties
# 服务器端口
server.port=8080

# AKShare API配置
akshare.base-url=http://localhost:8080/api/public
akshare.timeout=30000
```

## 📊 API接口

### 股票基本信息
- `GET /api/public/stock_basic_info_em?symbol={code}` - 获取股票基本信息

### A股财务数据
- `GET /api/public/stock_financial_debt_ths?symbol={code}` - 资产负债表
- `GET /api/public/stock_financial_benefit_ths?symbol={code}` - 利润表
- `GET /api/public/stock_financial_cash_ths?symbol={code}` - 现金流量表

### 港股财务数据
- `GET /api/public/stock_financial_hk_report_em?symbol={code}` - 综合财务报告

### 批量分析
- `POST /api/public/batch_analysis` - 批量财务分析

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📝 更新日志

### v1.0.0 (2024-12-19)
- ✨ 初始版本发布
- 🎯 支持A股和港股财务分析
- 📊 集成ECharts图表展示
- 🔄 支持按报告期/年度数据展示
- 📱 响应式设计，支持移动端

## 📄 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

## 🙏 致谢

- [AKShare](https://github.com/akfamily/akshare) - 金融数据接口
- [ECharts](https://echarts.apache.org/) - 数据可视化库
- [Spring Boot](https://spring.io/projects/spring-boot) - Java应用框架

## 📞 联系方式

- 项目维护者: [您的姓名]
- 邮箱: [您的邮箱]
- 项目地址: [GitHub仓库地址]

## ⚠️ 免责声明

本系统仅用于学习和研究目的，不构成投资建议。投资有风险，入市需谨慎。使用者应自行承担使用本系统进行投资决策的风险。

---

如果这个项目对您有帮助，请给个 ⭐ Star 支持一下！
