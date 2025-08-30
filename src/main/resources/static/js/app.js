/**
 * 全局变量
 */
let globalHkAnalysisData = null;
let globalAShareAnalysisData = null;
let currentStockCode = '';
let currentStockName = '';
let currentStockType = '';
let isHKStock = false;
let isYearlyMode = false;

/**
 * DOM加载完成后初始化应用
 */
document.addEventListener('DOMContentLoaded', function() {
    // 初始化事件监听器
    initEventListeners();
    
    // 初始化股票搜索建议
    initStockSuggestions();
    
    // 检查URL参数，如果有股票代码则自动分析
    checkUrlParams();
});

/**
 * 初始化所有事件监听器
 */
function initEventListeners() {
    // 分析按钮点击事件
    document.getElementById('analyzeBtn').addEventListener('click', function() {
        const stockCode = document.getElementById('stockInput').value.trim();
        if (stockCode) {
            analyzeStock(stockCode);
        }
    });
    
    // 股票输入框回车事件
    document.getElementById('stockInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            const stockCode = this.value.trim();
            if (stockCode) {
                analyzeStock(stockCode);
            }
        }
    });
    
    // 年度/季度模式切换
    document.getElementById('yearlyModeSwitch').addEventListener('change', function() {
        isYearlyMode = this.checked;
        if (currentStockCode) {
            updateCharts();
        }
    });
    
    // 自选股按钮点击事件
    document.getElementById('watchlistBtn').addEventListener('click', function() {
        toggleWatchlistSection();
    });
    
    // 黑名单按钮点击事件
    document.getElementById('blacklistBtn').addEventListener('click', function() {
        toggleBlacklistSection();
    });
    
    // 市场概况按钮点击事件
    document.getElementById('marketOverviewBtn').addEventListener('click', function() {
        showMarketOverview();
    });
    
    // 关闭自选股区域按钮
    document.getElementById('closeWatchlistBtn').addEventListener('click', function() {
        hideWatchlistSection();
    });
    
    // 添加到自选股按钮
    document.getElementById('addToWatchlistBtn').addEventListener('click', function() {
        if (currentStockCode && currentStockName) {
            addToWatchlist(currentStockCode, currentStockName, currentStockType);
        }
    });
    
    // 添加到黑名单按钮
    document.getElementById('addToBlacklistBtn').addEventListener('click', function() {
        if (currentStockCode && currentStockName) {
            addToBlacklist(currentStockCode, currentStockName, currentStockType);
        }
    });
    
    // 窗口大小变化时重新调整图表大小
    window.addEventListener('resize', function() {
        Object.values(ChartService.charts).forEach(chart => {
            chart.resize();
        });
    });
}

/**
 * 初始化股票搜索建议功能
 */
function initStockSuggestions() {
    const stockInput = document.getElementById('stockInput');
    const suggestionsContainer = document.getElementById('stockSuggestions');
    
    let selectedIndex = -1;
    let suggestions = [];
    
    stockInput.addEventListener('input', async function() {
        const query = this.value.trim();
        if (query.length < 2) {
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
            return;
        }
        
        try {
            suggestions = await ApiService.getStockSuggestions(query);
            if (suggestions.length > 0) {
                renderSuggestions(suggestions);
                suggestionsContainer.style.display = 'block';
                selectedIndex = -1;
            } else {
                suggestionsContainer.innerHTML = '';
                suggestionsContainer.style.display = 'none';
            }
        } catch (error) {
            console.error('获取股票建议失败:', error);
        }
    });
    
    stockInput.addEventListener('keydown', function(e) {
        if (!suggestions.length) return;
        
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            selectedIndex = (selectedIndex + 1) % suggestions.length;
            highlightSuggestion();
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            selectedIndex = selectedIndex <= 0 ? suggestions.length - 1 : selectedIndex - 1;
            highlightSuggestion();
        } else if (e.key === 'Enter' && selectedIndex >= 0) {
            e.preventDefault();
            selectSuggestion(suggestions[selectedIndex]);
        } else if (e.key === 'Escape') {
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
        }
    });
    
    document.addEventListener('click', function(e) {
        if (!stockInput.contains(e.target) && !suggestionsContainer.contains(e.target)) {
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
        }
    });
    
    function renderSuggestions(suggestions) {
        suggestionsContainer.innerHTML = '';
        
        suggestions.forEach((suggestion, index) => {
            const item = document.createElement('div');
            item.className = 'suggestion-item';
            item.innerHTML = `
                <div>
                    <span class="suggestion-code">${suggestion.code}</span>
                    <span class="suggestion-name">${suggestion.name}</span>
                </div>
                <span class="suggestion-type ${suggestion.type === 'A股' ? 'ashare' : 'hk'}">${suggestion.type}</span>
            `;
            
            item.addEventListener('click', function() {
                selectSuggestion(suggestion);
            });
            
            item.addEventListener('mouseover', function() {
                selectedIndex = index;
                highlightSuggestion();
            });
            
            suggestionsContainer.appendChild(item);
        });
    }
    
    function highlightSuggestion() {
        const items = suggestionsContainer.querySelectorAll('.suggestion-item');
        items.forEach((item, index) => {
            if (index === selectedIndex) {
                item.classList.add('selected');
            } else {
                item.classList.remove('selected');
            }
        });
    }
    
    function selectSuggestion(suggestion) {
        stockInput.value = suggestion.code;
        suggestionsContainer.innerHTML = '';
        suggestionsContainer.style.display = 'none';
        analyzeStock(suggestion.code);
    }
}

/**
 * 检查URL参数，如果有股票代码则自动分析
 */
function checkUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const stockCode = urlParams.get('code');
    if (stockCode) {
        document.getElementById('stockInput').value = stockCode;
        analyzeStock(stockCode);
    }
}

/**
 * 分析股票
 * @param {string} stockCode - 股票代码
 */
async function analyzeStock(stockCode) {
    showLoading('正在分析股票数据', '请稍候，正在获取并处理财务数据...');
    
    try {
        // 判断是A股还是港股
        isHKStock = stockCode.startsWith('0') || stockCode.startsWith('3') || stockCode.startsWith('6') ? false : true;
        
        // 根据股票类型调用不同的API
        let data;
        if (isHKStock) {
            data = await ApiService.getHKAnalysis(stockCode);
            globalHkAnalysisData = data;
            currentStockType = '港股';
        } else {
            data = await ApiService.getAShareAnalysis(stockCode);
            globalAShareAnalysisData = data;
            currentStockType = 'A股';
        }
        
        // 更新当前股票信息
        currentStockCode = stockCode;
        currentStockName = data.basicInfo.stockName;
        
        // 更新页面
        updateStockInfo(data.basicInfo);
        updateCharts();
        
        // 显示股票信息区域
        document.getElementById('stockInfoSection').style.display = 'block';
        document.getElementById('chartSection').style.display = 'block';
        document.getElementById('welcomeSection').style.display = 'none';
        
        // 更新URL参数
        updateUrlParam('code', stockCode);
        
        hideLoading();
    } catch (error) {
        console.error('分析股票失败:', error);
        hideLoading();
        showError('分析失败', '获取股票数据时出错，请检查股票代码是否正确。');
    }
}

/**
 * 更新股票基本信息
 * @param {Object} basicInfo - 股票基本信息
 */
function updateStockInfo(basicInfo) {
    document.getElementById('stockName').textContent = basicInfo.stockName;
    document.getElementById('stockCode').textContent = basicInfo.stockCode;
    document.getElementById('stockIndustry').textContent = basicInfo.industry;
    document.getElementById('stockPrice').textContent = basicInfo.currentPrice;
    document.getElementById('stockPE').textContent = basicInfo.pe;
    document.getElementById('stockPB').textContent = basicInfo.pb;
    document.getElementById('stockROE').textContent = basicInfo.roe;
    
    // 更新股票评分
    const scoreElement = document.getElementById('stockScore');
    const score = basicInfo.score;
    scoreElement.textContent = score;
    
    // 根据评分设置不同的颜色
    if (score >= 80) {
        scoreElement.className = 'score-excellent';
    } else if (score >= 60) {
        scoreElement.className = 'score-good';
    } else if (score >= 40) {
        scoreElement.className = 'score-average';
    } else {
        scoreElement.className = 'score-poor';
    }
}

/**
 * 更新所有图表
 */
function updateCharts() {
    // 重置所有图表
    ChartService.resetAllCharts();
    
    const data = isHKStock ? globalHkAnalysisData : globalAShareAnalysisData;
    if (!data) return;
    
    // 根据年度/季度模式选择数据
    const financialData = isYearlyMode ? data.yearlyData : data.quarterlyData;
    
    // 创建ROE图表
    ChartService.createROEChart('roeChart', financialData, isHKStock);
    
    // 创建利润表图表
    ChartService.createIncomeChart('incomeChart', financialData, isHKStock);
    
    // 创建资产负债图表
    ChartService.createBalanceChart('balanceChart', financialData, isHKStock);
    
    // 创建现金流量图表
    ChartService.createCashFlowChart('cashFlowChart', financialData, isHKStock);
    
    // 创建市盈率图表
    if (data.peHistory && data.peHistory.length > 0) {
        ChartService.createPEChart('peChart', data.peHistory);
    }
}

/**
 * 显示自选股区域
 */
async function toggleWatchlistSection() {
    const watchlistSection = document.getElementById('watchlistSection');
    
    if (watchlistSection.style.display === 'block') {
        hideWatchlistSection();
    } else {
        showLoading('正在加载自选股', '请稍候...');
        
        try {
            const watchlistData = await ApiService.getWatchlist();
            renderWatchlist(watchlistData);
            
            watchlistSection.style.display = 'block';
            hideLoading();
        } catch (error) {
            console.error('获取自选股失败:', error);
            hideLoading();
            showError('加载失败', '获取自选股数据时出错。');
        }
    }
}

/**
 * 隐藏自选股区域
 */
function hideWatchlistSection() {
    const watchlistSection = document.getElementById('watchlistSection');
    watchlistSection.classList.add('fade-out');
    
    setTimeout(() => {
        watchlistSection.style.display = 'none';
        watchlistSection.classList.remove('fade-out');
    }, 300);
}

/**
 * 渲染自选股列表
 * @param {Array} watchlistData - 自选股数据
 */
function renderWatchlist(watchlistData) {
    const tableBody = document.getElementById('watchlistTableBody');
    tableBody.innerHTML = '';
    
    if (watchlistData.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center;">暂无自选股</td></tr>';
        return;
    }
    
    watchlistData.forEach(stock => {
        const row = document.createElement('tr');
        
        const typeClass = stock.type === 'A股' ? 'stock-type-1' : 'stock-type-2';
        
        row.innerHTML = `
            <td><span class="stock-type-badge ${typeClass}">${stock.type}</span></td>
            <td>${stock.code}</td>
            <td>${stock.name}</td>
            <td>${stock.price || '-'}</td>
            <td>${stock.pe || '-'}</td>
            <td>${stock.score || '-'}</td>
            <td>
                <button class="analyze-btn" data-code="${stock.code}">分析</button>
                <button class="detail-btn" data-code="${stock.code}">详情</button>
                <button class="remove-btn" data-code="${stock.code}">移除</button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
    
    // 添加事件监听器
    tableBody.querySelectorAll('.analyze-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const code = this.getAttribute('data-code');
            analyzeStock(code);
            hideWatchlistSection();
        });
    });
    
    tableBody.querySelectorAll('.detail-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const code = this.getAttribute('data-code');
            window.open(`/stock-detail?code=${code}`, '_blank');
        });
    });
    
    tableBody.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const code = this.getAttribute('data-code');
            
            try {
                await ApiService.removeFromWatchlist(code);
                this.closest('tr').remove();
                
                // 如果表格为空，显示提示信息
                if (tableBody.children.length === 0) {
                    tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center;">暂无自选股</td></tr>';
                }
            } catch (error) {
                console.error('移除自选股失败:', error);
                showError('操作失败', '移除自选股时出错。');
            }
        });
    });
}

/**
 * 添加股票到自选股
 * @param {string} code - 股票代码
 * @param {string} name - 股票名称
 * @param {string} type - 股票类型
 */
async function addToWatchlist(code, name, type) {
    showLoading('正在添加到自选股', '请稍候...');
    
    try {
        const stockData = {
            code: code,
            name: name,
            type: type,
            price: document.getElementById('stockPrice').textContent,
            pe: document.getElementById('stockPE').textContent,
            score: document.getElementById('stockScore').textContent
        };
        
        await ApiService.addToWatchlist(stockData);
        hideLoading();
        showSuccess('添加成功', '股票已添加到自选股。');
    } catch (error) {
        console.error('添加自选股失败:', error);
        hideLoading();
        showError('添加失败', '添加自选股时出错。');
    }
}

/**
 * 显示黑名单区域
 */
async function toggleBlacklistSection() {
    const blacklistSection = document.getElementById('blacklistSection');
    
    if (blacklistSection.style.display === 'block') {
        hideBlacklistSection();
    } else {
        showLoading('正在加载黑名单', '请稍候...');
        
        try {
            const blacklistData = await ApiService.getBlacklist();
            renderBlacklist(blacklistData);
            
            blacklistSection.style.display = 'block';
            hideLoading();
        } catch (error) {
            console.error('获取黑名单失败:', error);
            hideLoading();
            showError('加载失败', '获取黑名单数据时出错。');
        }
    }
}

/**
 * 隐藏黑名单区域
 */
function hideBlacklistSection() {
    const blacklistSection = document.getElementById('blacklistSection');
    blacklistSection.classList.add('fade-out');
    
    setTimeout(() => {
        blacklistSection.style.display = 'none';
        blacklistSection.classList.remove('fade-out');
    }, 300);
}

/**
 * 渲染黑名单列表
 * @param {Array} blacklistData - 黑名单数据
 */
function renderBlacklist(blacklistData) {
    const tableBody = document.getElementById('blacklistTableBody');
    tableBody.innerHTML = '';
    
    if (blacklistData.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6" style="text-align: center;">暂无黑名单股票</td></tr>';
        return;
    }
    
    blacklistData.forEach(stock => {
        const row = document.createElement('tr');
        
        const typeClass = stock.type === 'A股' ? 'stock-type-1' : 'stock-type-2';
        
        row.innerHTML = `
            <td><span class="stock-type-badge ${typeClass}">${stock.type}</span></td>
            <td>${stock.code}</td>
            <td>${stock.name}</td>
            <td>${stock.reason || '-'}</td>
            <td>${stock.addTime || '-'}</td>
            <td>
                <button class="analyze-btn" data-code="${stock.code}">分析</button>
                <button class="remove-blacklist-btn" data-code="${stock.code}">移除</button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
    
    // 添加事件监听器
    tableBody.querySelectorAll('.analyze-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const code = this.getAttribute('data-code');
            analyzeStock(code);
            hideBlacklistSection();
        });
    });
    
    tableBody.querySelectorAll('.remove-blacklist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const code = this.getAttribute('data-code');
            
            try {
                await ApiService.removeFromBlacklist(code);
                this.closest('tr').remove();
                
                // 如果表格为空，显示提示信息
                if (tableBody.children.length === 0) {
                    tableBody.innerHTML = '<tr><td colspan="6" style="text-align: center;">暂无黑名单股票</td></tr>';
                }
            } catch (error) {
                console.error('移除黑名单失败:', error);
                showError('操作失败', '移除黑名单时出错。');
            }
        });
    });
}

/**
 * 添加股票到黑名单
 * @param {string} code - 股票代码
 * @param {string} name - 股票名称
 * @param {string} type - 股票类型
 */
async function addToBlacklist(code, name, type) {
    // 弹出对话框，让用户输入拉黑原因
    const reason = prompt('请输入将该股票加入黑名单的原因:', '');
    if (reason === null) return; // 用户取消
    
    showLoading('正在添加到黑名单', '请稍候...');
    
    try {
        const stockData = {
            code: code,
            name: name,
            type: type,
            reason: reason,
            addTime: new Date().toISOString().split('T')[0]
        };
        
        await ApiService.addToBlacklist(stockData);
        hideLoading();
        showSuccess('添加成功', '股票已添加到黑名单。');
    } catch (error) {
        console.error('添加黑名单失败:', error);
        hideLoading();
        showError('添加失败', '添加黑名单时出错。');
    }
}

/**
 * 显示市场概况
 */
async function showMarketOverview() {
    showLoading('正在加载市场概况', '请稍候...');
    
    try {
        const marketData = await ApiService.getMarketOverview();
        
        // 显示市场概况区域
        document.getElementById('marketOverviewSection').style.display = 'block';
        document.getElementById('welcomeSection').style.display = 'none';
        
        // 创建市场概况图表
        ChartService.createMarketOverviewChart('marketOverviewChart', marketData);
        
        hideLoading();
    } catch (error) {
        console.error('获取市场概况失败:', error);
        hideLoading();
        showError('加载失败', '获取市场概况数据时出错。');
    }
}

/**
 * 显示加载中提示
 * @param {string} title - 标题
 * @param {string} message - 消息
 */
function showLoading(title, message) {
    const loadingElement = document.getElementById('loading');
    const loadingTextElement = document.getElementById('loadingText');
    const loadingSubtextElement = document.getElementById('loadingSubtext');
    
    loadingTextElement.textContent = title;
    loadingSubtextElement.textContent = message;
    loadingElement.style.display = 'flex';
}

/**
 * 隐藏加载中提示
 */
function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

/**
 * 显示错误提示
 * @param {string} title - 标题
 * @param {string} message - 消息
 */
function showError(title, message) {
    alert(`${title}: ${message}`);
}

/**
 * 显示成功提示
 * @param {string} title - 标题
 * @param {string} message - 消息
 */
function showSuccess(title, message) {
    alert(`${title}: ${message}`);
}

/**
 * 更新URL参数
 * @param {string} key - 参数名
 * @param {string} value - 参数值
 */
function updateUrlParam(key, value) {
    const url = new URL(window.location.href);
    url.searchParams.set(key, value);
    window.history.replaceState({}, '', url);
}