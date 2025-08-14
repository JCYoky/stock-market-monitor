# 贡献指南 (Contributing Guide)

感谢您对股票市场监控系统项目的关注！我们欢迎所有形式的贡献，包括但不限于代码贡献、文档改进、问题报告和功能建议。

## 🚀 快速开始

### 1. 环境准备
- 确保您已安装 Java 17+ 和 Maven 3.6+
- 熟悉 Spring Boot 和前端开发技术
- 了解 Git 基本操作

### 2. Fork 项目
1. 访问 [项目主页](https://github.com/yourusername/stock-market-monitor)
2. 点击右上角的 "Fork" 按钮
3. 将项目 Fork 到您的 GitHub 账户

### 3. 克隆项目
```bash
git clone https://github.com/YOUR_USERNAME/stock-market-monitor.git
cd stock-market-monitor
```

### 4. 添加远程仓库
```bash
git remote add upstream https://github.com/original-owner/stock-market-monitor.git
```

## 🔧 开发环境设置

### 后端环境
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
```

### 前端开发
- 前端代码位于 `src/main/resources/static/`
- 主要文件：`zxm-analysis.html`
- 支持实时编辑和预览

## 📝 贡献类型

### 🐛 Bug 报告
如果您发现了 Bug，请：

1. **搜索现有 Issues**: 避免重复报告
2. **创建新 Issue**: 使用 Bug 报告模板
3. **提供详细信息**:
   - 操作系统和版本
   - 浏览器版本（如果相关）
   - 重现步骤
   - 预期行为和实际行为
   - 错误日志或截图

### 💡 功能建议
对于新功能建议：

1. **描述需求**: 清楚说明功能用途
2. **使用场景**: 提供具体的使用案例
3. **优先级**: 标注功能的重要程度
4. **技术考虑**: 如果有技术实现想法，请一并提出

### 🔧 代码贡献
代码贡献是最直接的贡献方式：

1. **选择任务**: 从 Issues 中选择要处理的任务
2. **创建分支**: 为每个任务创建独立分支
3. **开发功能**: 编写代码和测试
4. **提交代码**: 遵循提交规范
5. **创建 PR**: 提交 Pull Request

## 🌿 分支管理

### 分支命名规范
```
feature/功能名称          # 新功能开发
bugfix/问题描述          # Bug 修复
hotfix/紧急修复          # 紧急问题修复
docs/文档更新            # 文档改进
refactor/重构            # 代码重构
```

### 分支创建示例
```bash
# 创建功能分支
git checkout -b feature/add-new-financial-indicator

# 创建 Bug 修复分支
git checkout -b bugfix/fix-chart-display-issue

# 创建文档更新分支
git checkout -b docs/update-api-documentation
```

## 📋 代码规范

### Java 代码规范
- 遵循 Java 命名约定
- 使用有意义的变量和方法名
- 添加适当的注释和文档
- 遵循 SOLID 原则

### 前端代码规范
- 使用语义化的 HTML 标签
- CSS 类名使用 BEM 命名法
- JavaScript 使用 ES6+ 语法
- 保持代码整洁和可读性

### 注释规范
```java
/**
 * 计算股票的财务比率
 * 
 * @param financialData 财务数据
 * @param period 计算周期
 * @return 财务比率结果
 * @throws IllegalArgumentException 如果数据无效
 */
public FinancialRatio calculateRatio(FinancialData financialData, String period) {
    // 实现逻辑
}
```

## 🧪 测试要求

### 测试覆盖
- **单元测试**: 核心业务逻辑必须有测试覆盖
- **集成测试**: API 接口需要集成测试
- **前端测试**: 关键功能需要前端测试

### 测试示例
```java
@Test
public void testCalculateROE() {
    // 准备测试数据
    FinancialData data = new FinancialData();
    data.setNetProfit("1000000");
    data.setShareholdersEquity("5000000");
    
    // 执行测试
    FinancialRatio ratio = service.calculateRatio(data, "2023");
    
    // 验证结果
    assertEquals(0.2, ratio.getRoe(), 0.001);
}
```

## 📤 提交规范

### 提交信息格式
```
类型(范围): 简短描述

详细描述（可选）

相关 Issue: #123
```

### 提交类型
- **feat**: 新功能
- **fix**: Bug 修复
- **docs**: 文档更新
- **style**: 代码格式调整
- **refactor**: 代码重构
- **test**: 测试相关
- **chore**: 构建过程或辅助工具的变动

### 提交示例
```bash
git commit -m "feat(analysis): 添加ROE计算功能

- 实现净资产收益率计算逻辑
- 添加相关单元测试
- 更新API文档

相关 Issue: #45"
```

## 🔄 Pull Request 流程

### 1. 准备 PR
- 确保代码通过所有测试
- 更新相关文档
- 添加必要的测试用例

### 2. 创建 PR
1. 在 GitHub 上创建 Pull Request
2. 使用 PR 模板填写信息
3. 关联相关 Issue
4. 添加标签和里程碑

### 3. PR 审查
- 代码审查员会检查代码质量
- 可能需要根据反馈进行调整
- 通过审查后合并到主分支

### PR 模板示例
```markdown
## 变更描述
简要描述本次变更的内容和目的。

## 变更类型
- [ ] Bug 修复
- [ ] 新功能
- [ ] 文档更新
- [ ] 代码重构
- [ ] 其他

## 测试情况
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 手动测试通过

## 相关 Issue
关联的 Issue: #123

## 截图（如果适用）
添加相关的截图或演示。

## 检查清单
- [ ] 代码遵循项目规范
- [ ] 添加了必要的测试
- [ ] 更新了相关文档
- [ ] 本地测试通过
```

## 🏷️ Issue 标签说明

### 标签分类
- **bug**: Bug 报告
- **enhancement**: 功能增强
- **documentation**: 文档相关
- **good first issue**: 适合新手的任务
- **help wanted**: 需要帮助的任务
- **priority: high**: 高优先级
- **priority: medium**: 中优先级
- **priority: low**: 低优先级

## 📚 学习资源

### 技术文档
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [ECharts 官方文档](https://echarts.apache.org/)
- [AKShare 官方文档](https://akshare.akfamily.xyz/)

### 开发工具
- **IDE**: IntelliJ IDEA, Eclipse, VS Code
- **API 测试**: Postman, Insomnia
- **版本控制**: Git, GitHub Desktop

## 🤝 社区交流

### 交流渠道
- **GitHub Issues**: 技术讨论和问题报告
- **GitHub Discussions**: 功能讨论和社区交流
- **邮件列表**: [您的邮箱]

### 行为准则
- 尊重所有贡献者
- 保持专业和友善的交流态度
- 欢迎不同观点和建议
- 避免个人攻击和不当言论

## 🎯 新手任务

如果您是新手，可以从以下任务开始：

1. **文档改进**: 完善 README 或 API 文档
2. **Bug 修复**: 修复标记为 "good first issue" 的问题
3. **测试补充**: 为现有功能添加测试用例
4. **代码优化**: 改进代码注释和格式

## 🏆 贡献者荣誉

所有贡献者都会被记录在项目的贡献者列表中，包括：

- 代码贡献者
- 文档贡献者
- 问题报告者
- 功能建议者

## 📞 联系我们

如果您有任何问题或建议，请通过以下方式联系：

- **项目维护者**: [您的姓名]
- **邮箱**: [您的邮箱]
- **GitHub**: [您的GitHub主页]

---

再次感谢您的贡献！您的参与让这个项目变得更好！ 🎉
