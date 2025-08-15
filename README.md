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

### ⭐ 自选股管理
- **观察股管理**: 添加、删除、修改观察股
- **持仓股管理**: 设置和管理持仓股
- **财务数据追踪**: 自动保存分析过的股票财务数据
- **智能排序**: 按市盈率得分排序，优先显示高价值股票

## 🛠️ 技术栈

### 后端
- **Java 17+**
- **Spring Boot 3.x**
- **Maven**
- **MyBatis**: 数据持久化框架
- **MySQL**: 关系型数据库
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
- MySQL 8.0+ 或 MariaDB 10.5+
- 网络连接 (用于访问AKShare API)

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/JCYoky/stock-market-monitor.git
cd stock-market-monitor
```

### 2. 数据库配置

#### 创建数据库
```sql
CREATE DATABASE `stock-market-monitor` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 执行数据库表结构
```sql
-- 自选股表
CREATE TABLE `stock_watchlist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
  `stock_name` varchar(100) NOT NULL COMMENT '股票名称',
  `stock_type` int NOT NULL DEFAULT '0' COMMENT '股票类型：0-系统添加，1-观察股，2-持仓股',
  `pe_ttm` decimal(10,2) DEFAULT '0.00' COMMENT '市盈率TTM',
  `roe` decimal(10,2) DEFAULT '0.00' COMMENT '净资产收益率',
  `profit_quality` decimal(10,4) DEFAULT '0.0000' COMMENT '利润质量',
  `assets_quality` decimal(10,4) DEFAULT '0.0000' COMMENT '资产质量得分',
  `pe_score` decimal(10,4) DEFAULT '0.0000' COMMENT '市盈率得分',
  `created_time` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `updated_time` varchar(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stock_code` (`stock_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自选股表';
```

### 3. 配置数据库连接

编辑 `src/main/resources/application.properties`:
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/stock-market-monitor?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis配置
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=me.huangjiacheng.hundun.model
mybatis.configuration.map-underscore-to-camel-case=true

# 服务器端口
server.port=8888
```

### 4. AKShare本地部署

#### 方式一：使用Docker（推荐）
```bash
# 拉取AKShare镜像
docker pull akfamily/akshare:latest

# 运行AKShare服务
docker run -d --name akshare -p 8080:8080 akfamily/akshare:latest

# 验证服务
curl http://localhost:8080/api/public/stock_basic_info_em?symbol=000001
```

#### 方式二：本地Python环境
```bash
# 安装Python 3.8+
pip install akshare

# 启动AKShare服务
akshare run --host 0.0.0.0 --port 8080
```

#### 方式三：使用预构建的JAR包
```bash
# 下载AKShare JAR包
wget https://github.com/akfamily/akshare/releases/download/v1.12.0/akshare-1.12.0.jar

# 运行服务
java -jar akshare-1.12.0.jar --server.port=8080
```

### 5. 编译项目
```bash
mvn clean compile
```

### 6. 运行应用
```bash
mvn spring-boot:run
```

应用启动后，访问 `http://localhost:8888` 即可使用。

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

### 自选股管理
1. 点击右上角"自选股"按钮查看自选股列表
2. 分析股票后，点击"加观察股"或"设为持仓股"
3. 在自选股列表中点击"查看详情"重新分析股票
4. 系统自动保存分析过的股票财务数据

### 数据展示模式
- **按报告期**: 显示所有报告期数据 (默认)
- **按年度**: 自动聚合为年度数据展示
- 使用页面左侧开关进行模式切换

## 🏗️ 项目结构

```
stock-market-monitor/
├── src/main/java/me/huangjiacheng/hundun/
│   ├── controller/          # 控制器层
│   │   ├── AnalysisApiController.java      # API接口
│   │   ├── AnalysisPageController.java     # 页面控制器
│   │   ├── BatchAnalysisApiController.java # 批量分析接口
│   │   └── StockWatchlistController.java   # 自选股管理接口
│   ├── model/               # 数据模型
│   │   ├── Evaluation.java                 # 评估结果模型
│   │   ├── StockInfo.java                  # 股票基本信息
│   │   ├── StockWatchlist.java            # 自选股模型
│   │   ├── StockFinancialDebtThs.java     # A股负债表
│   │   ├── StockFinancialHkBalanceSheet.java # 港股资产负债表
│   │   └── ...                            # 其他财务数据模型
│   ├── mapper/              # MyBatis映射器
│   │   └── StockWatchlistMapper.java      # 自选股数据访问
│   ├── service/             # 业务逻辑层
│   │   ├── AKShareService.java            # AKShare数据服务
│   │   ├── FinancialAnalysisService.java  # 财务分析服务
│   │   ├── BatchAnalysisService.java      # 批量分析服务
│   │   ├── StockWatchlistService.java     # 自选股服务
│   │   └── PersistenceService.java        # 数据持久化服务
│   └── HundunApplication.java             # 主应用类
├── src/main/resources/
│   ├── static/
│   │   └── zxm-analysis.html              # 前端页面
│   ├── mapper/
│   │   └── StockWatchlistMapper.xml       # MyBatis XML映射
│   └── application.properties              # 应用配置
├── pom.xml                                 # Maven配置
└── README.md                               # 项目说明
```

## 🔧 配置说明

### 数据库配置
```properties
# MySQL数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/stock-market-monitor?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password

# MyBatis配置
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=me.huangjiacheng.hundun.model
mybatis.configuration.map-underscore-to-camel-case=true
```

### AKShare配置
```properties
# AKShare服务地址
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

### 自选股管理
- `POST /api/watchlist/add` - 添加/更新自选股
- `DELETE /api/watchlist/delete/{stockCode}` - 删除自选股
- `GET /api/watchlist/all` - 获取所有自选股
- `GET /api/watchlist/stock/{stockCode}` - 查询特定股票

### 批量分析
- `POST /api/public/batch_analysis` - 批量财务分析

## 🐛 常见问题

### 数据库连接失败
- 检查MySQL服务是否启动
- 验证数据库用户名和密码
- 确认数据库名称是否正确

### AKShare服务无法访问
- 检查AKShare服务是否启动在8080端口
- 验证防火墙设置
- 尝试使用Docker方式部署

### 自选股数据不显示
- 检查数据库表结构是否正确
- 确认MyBatis映射文件路径
- 查看应用日志中的错误信息

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📝 更新日志

### v1.1.0 (2024-12-19)
- ✨ 新增自选股管理功能
- 🎯 支持观察股和持仓股分类
- 📊 自动保存分析过的股票财务数据
- 🔄 优化自选股列表排序和显示
- 🎨 美化UI界面，使用现代化设计

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
- [MyBatis](https://mybatis.org/) - 数据持久化框架

## 📞 联系方式

- 项目维护者: [您的姓名]
- 邮箱: [您的邮箱]
- 项目地址: [GitHub仓库地址]

## ⚠️ 免责声明

本系统仅用于学习和研究目的，不构成投资建议。投资有风险，入市需谨慎。使用者应自行承担使用本系统进行投资决策的风险。

---

如果这个项目对您有帮助，请给个 ⭐ Star 支持一下！
