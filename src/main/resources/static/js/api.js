/**
 * API服务模块 - 处理所有与后端API的交互
 */
const ApiService = {
    /**
     * 获取A股财务分析数据
     * @param {string} stockCode - 股票代码
     * @returns {Promise} - 返回API响应Promise
     */
    getAShareAnalysis: async function(stockCode) {
        try {
            const response = await fetch(`/api/ashare/analysis/${stockCode}`);
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('获取A股分析数据失败:', error);
            throw error;
        }
    },

    /**
     * 获取港股财务分析数据
     * @param {string} stockCode - 股票代码
     * @returns {Promise} - 返回API响应Promise
     */
    getHKAnalysis: async function(stockCode) {
        try {
            const response = await fetch(`/api/hk/analysis/${stockCode}`);
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('获取港股分析数据失败:', error);
            throw error;
        }
    },

    /**
     * 获取自选股列表
     * @returns {Promise} - 返回API响应Promise
     */
    getWatchlist: async function() {
        try {
            const response = await fetch('/api/watchlist');
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('获取自选股列表失败:', error);
            throw error;
        }
    },

    /**
     * 添加股票到自选股
     * @param {Object} stockData - 股票数据
     * @returns {Promise} - 返回API响应Promise
     */
    addToWatchlist: async function(stockData) {
        try {
            const response = await fetch('/api/watchlist/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(stockData)
            });
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('添加自选股失败:', error);
            throw error;
        }
    },

    /**
     * 从自选股中移除股票
     * @param {string} stockCode - 股票代码
     * @returns {Promise} - 返回API响应Promise
     */
    removeFromWatchlist: async function(stockCode) {
        try {
            const response = await fetch(`/api/watchlist/remove/${stockCode}`, {
                method: 'DELETE'
            });
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('移除自选股失败:', error);
            throw error;
        }
    },

    /**
     * 获取股票搜索建议
     * @param {string} query - 搜索关键词
     * @returns {Promise} - 返回API响应Promise
     */
    getStockSuggestions: async function(query) {
        try {
            const response = await fetch(`/api/stock/search?query=${encodeURIComponent(query)}`);
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('获取股票搜索建议失败:', error);
            throw error;
        }
    },

    /**
     * 获取市场概况数据
     * @returns {Promise} - 返回API响应Promise
     */
    getMarketOverview: async function() {
        try {
            const response = await fetch('/api/market/overview');
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('获取市场概况失败:', error);
            throw error;
        }
    },

    /**
     * 获取黑名单股票列表
     * @returns {Promise} - 返回API响应Promise
     */
    getBlacklist: async function() {
        try {
            const response = await fetch('/api/blacklist');
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('获取黑名单失败:', error);
            throw error;
        }
    },

    /**
     * 添加股票到黑名单
     * @param {Object} stockData - 股票数据
     * @returns {Promise} - 返回API响应Promise
     */
    addToBlacklist: async function(stockData) {
        try {
            const response = await fetch('/api/blacklist/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(stockData)
            });
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('添加黑名单失败:', error);
            throw error;
        }
    },

    /**
     * 从黑名单中移除股票
     * @param {string} stockCode - 股票代码
     * @returns {Promise} - 返回API响应Promise
     */
    removeFromBlacklist: async function(stockCode) {
        try {
            const response = await fetch(`/api/blacklist/remove/${stockCode}`, {
                method: 'DELETE'
            });
            if (!response.ok) {
                throw new Error(`API错误: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('移除黑名单失败:', error);
            throw error;
        }
    }
};