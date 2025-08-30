/**
 * 图表服务模块 - 处理所有与ECharts相关的图表创建和更新
 */
const ChartService = {
    /**
     * 初始化所有图表实例
     */
    charts: {},

    /**
     * 初始化图表
     * @param {string} id - 图表容器ID
     * @returns {echarts.ECharts} - 返回图表实例
     */
    initChart: function(id) {
        const chartDom = document.getElementById(id);
        if (!chartDom) {
            console.error(`图表容器不存在: ${id}`);
            return null;
        }
        
        const chart = echarts.init(chartDom);
        this.charts[id] = chart;
        return chart;
    },

    /**
     * 获取图表实例
     * @param {string} id - 图表容器ID
     * @returns {echarts.ECharts|null} - 返回图表实例或null
     */
    getChart: function(id) {
        return this.charts[id] || null;
    },

    /**
     * 销毁图表实例
     * @param {string} id - 图表容器ID
     */
    disposeChart: function(id) {
        if (this.charts[id]) {
            this.charts[id].dispose();
            delete this.charts[id];
        }
    },

    /**
     * 重置所有图表
     */
    resetAllCharts: function() {
        Object.keys(this.charts).forEach(id => {
            this.disposeChart(id);
        });
    },

    /**
     * 创建ROE图表
     * @param {string} containerId - 图表容器ID
     * @param {Array} data - 图表数据
     * @param {boolean} isHK - 是否为港股
     */
    createROEChart: function(containerId, data, isHK) {
        const chart = this.initChart(containerId);
        if (!chart) return;

        const years = data.map(item => item.year);
        const roeValues = data.map(item => item.roe);
        
        const option = {
            title: {
                text: 'ROE走势',
                left: 'center',
                textStyle: {
                    fontSize: 16,
                    fontWeight: 'bold'
                }
            },
            tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                    const dataIndex = params[0].dataIndex;
                    const year = years[dataIndex];
                    const roe = roeValues[dataIndex].toFixed(2);
                    return `${year}年<br/>ROE: ${roe}%`;
                }
            },
            xAxis: {
                type: 'category',
                data: years,
                axisLabel: {
                    interval: 0,
                    rotate: 45
                }
            },
            yAxis: {
                type: 'value',
                name: 'ROE(%)',
                axisLabel: {
                    formatter: '{value}%'
                }
            },
            series: [{
                name: 'ROE',
                type: 'line',
                data: roeValues,
                smooth: true,
                lineStyle: {
                    width: 3,
                    color: '#3b82f6'
                },
                itemStyle: {
                    color: '#3b82f6'
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(59, 130, 246, 0.5)' },
                        { offset: 1, color: 'rgba(59, 130, 246, 0.1)' }
                    ])
                },
                markLine: {
                    silent: true,
                    lineStyle: {
                        color: '#ef4444',
                        type: 'dashed'
                    },
                    data: [
                        { yAxis: 10, name: '10%基准线' }
                    ]
                }
            }],
            grid: {
                left: '5%',
                right: '5%',
                bottom: '15%',
                top: '15%',
                containLabel: true
            }
        };
        
        chart.setOption(option);
        
        // 响应窗口大小变化
        window.addEventListener('resize', () => {
            chart.resize();
        });
    },

    /**
     * 创建利润表图表
     * @param {string} containerId - 图表容器ID
     * @param {Array} data - 图表数据
     * @param {boolean} isHK - 是否为港股
     */
    createIncomeChart: function(containerId, data, isHK) {
        const chart = this.initChart(containerId);
        if (!chart) return;

        const years = data.map(item => item.year);
        const revenue = data.map(item => item.revenue);
        const netProfit = data.map(item => item.netProfit);
        
        const option = {
            title: {
                text: '营收与净利润',
                left: 'center',
                textStyle: {
                    fontSize: 16,
                    fontWeight: 'bold'
                }
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
                formatter: function(params) {
                    const dataIndex = params[0].dataIndex;
                    const year = years[dataIndex];
                    const revenueValue = (revenue[dataIndex] / 100000000).toFixed(2);
                    const netProfitValue = (netProfit[dataIndex] / 100000000).toFixed(2);
                    return `${year}年<br/>营收: ${revenueValue}亿<br/>净利润: ${netProfitValue}亿`;
                }
            },
            legend: {
                data: ['营收', '净利润'],
                top: '10%'
            },
            xAxis: {
                type: 'category',
                data: years,
                axisLabel: {
                    interval: 0,
                    rotate: 45
                }
            },
            yAxis: {
                type: 'value',
                name: '金额(亿元)',
                axisLabel: {
                    formatter: function(value) {
                        return (value / 100000000).toFixed(0);
                    }
                }
            },
            series: [
                {
                    name: '营收',
                    type: 'bar',
                    data: revenue,
                    barWidth: '30%',
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#3b82f6' },
                            { offset: 1, color: '#93c5fd' }
                        ])
                    }
                },
                {
                    name: '净利润',
                    type: 'line',
                    data: netProfit,
                    smooth: true,
                    lineStyle: {
                        width: 3,
                        color: '#10b981'
                    },
                    itemStyle: {
                        color: '#10b981'
                    }
                }
            ],
            grid: {
                left: '5%',
                right: '5%',
                bottom: '15%',
                top: '20%',
                containLabel: true
            }
        };
        
        chart.setOption(option);
        
        // 响应窗口大小变化
        window.addEventListener('resize', () => {
            chart.resize();
        });
    },

    /**
     * 创建资产负债图表
     * @param {string} containerId - 图表容器ID
     * @param {Array} data - 图表数据
     * @param {boolean} isHK - 是否为港股
     */
    createBalanceChart: function(containerId, data, isHK) {
        const chart = this.initChart(containerId);
        if (!chart) return;

        const years = data.map(item => item.year);
        const assets = data.map(item => item.totalAssets);
        const liabilities = data.map(item => item.totalLiabilities);
        const equity = data.map(item => item.totalEquity);
        
        const option = {
            title: {
                text: '资产负债结构',
                left: 'center',
                textStyle: {
                    fontSize: 16,
                    fontWeight: 'bold'
                }
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
                formatter: function(params) {
                    const dataIndex = params[0].dataIndex;
                    const year = years[dataIndex];
                    const assetValue = (assets[dataIndex] / 100000000).toFixed(2);
                    const liabilityValue = (liabilities[dataIndex] / 100000000).toFixed(2);
                    const equityValue = (equity[dataIndex] / 100000000).toFixed(2);
                    return `${year}年<br/>总资产: ${assetValue}亿<br/>总负债: ${liabilityValue}亿<br/>净资产: ${equityValue}亿`;
                }
            },
            legend: {
                data: ['总资产', '总负债', '净资产'],
                top: '10%'
            },
            xAxis: {
                type: 'category',
                data: years,
                axisLabel: {
                    interval: 0,
                    rotate: 45
                }
            },
            yAxis: {
                type: 'value',
                name: '金额(亿元)',
                axisLabel: {
                    formatter: function(value) {
                        return (value / 100000000).toFixed(0);
                    }
                }
            },
            series: [
                {
                    name: '总资产',
                    type: 'bar',
                    stack: 'total',
                    data: assets,
                    itemStyle: {
                        color: '#3b82f6'
                    }
                },
                {
                    name: '总负债',
                    type: 'bar',
                    stack: 'total',
                    data: liabilities,
                    itemStyle: {
                        color: '#ef4444'
                    }
                },
                {
                    name: '净资产',
                    type: 'line',
                    data: equity,
                    smooth: true,
                    lineStyle: {
                        width: 3,
                        color: '#10b981'
                    },
                    itemStyle: {
                        color: '#10b981'
                    }
                }
            ],
            grid: {
                left: '5%',
                right: '5%',
                bottom: '15%',
                top: '20%',
                containLabel: true
            }
        };
        
        chart.setOption(option);
        
        // 响应窗口大小变化
        window.addEventListener('resize', () => {
            chart.resize();
        });
    },

    /**
     * 创建现金流量图表
     * @param {string} containerId - 图表容器ID
     * @param {Array} data - 图表数据
     * @param {boolean} isHK - 是否为港股
     */
    createCashFlowChart: function(containerId, data, isHK) {
        const chart = this.initChart(containerId);
        if (!chart) return;

        const years = data.map(item => item.year);
        const operatingCashFlow = data.map(item => item.operatingCashFlow);
        const investingCashFlow = data.map(item => item.investingCashFlow);
        const financingCashFlow = data.map(item => item.financingCashFlow);
        
        const option = {
            title: {
                text: '现金流量',
                left: 'center',
                textStyle: {
                    fontSize: 16,
                    fontWeight: 'bold'
                }
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
                formatter: function(params) {
                    const dataIndex = params[0].dataIndex;
                    const year = years[dataIndex];
                    const operatingValue = (operatingCashFlow[dataIndex] / 100000000).toFixed(2);
                    const investingValue = (investingCashFlow[dataIndex] / 100000000).toFixed(2);
                    const financingValue = (financingCashFlow[dataIndex] / 100000000).toFixed(2);
                    return `${year}年<br/>经营现金流: ${operatingValue}亿<br/>投资现金流: ${investingValue}亿<br/>筹资现金流: ${financingValue}亿`;
                }
            },
            legend: {
                data: ['经营现金流', '投资现金流', '筹资现金流'],
                top: '10%'
            },
            xAxis: {
                type: 'category',
                data: years,
                axisLabel: {
                    interval: 0,
                    rotate: 45
                }
            },
            yAxis: {
                type: 'value',
                name: '金额(亿元)',
                axisLabel: {
                    formatter: function(value) {
                        return (value / 100000000).toFixed(0);
                    }
                }
            },
            series: [
                {
                    name: '经营现金流',
                    type: 'bar',
                    data: operatingCashFlow,
                    itemStyle: {
                        color: '#10b981'
                    }
                },
                {
                    name: '投资现金流',
                    type: 'bar',
                    data: investingCashFlow,
                    itemStyle: {
                        color: '#3b82f6'
                    }
                },
                {
                    name: '筹资现金流',
                    type: 'bar',
                    data: financingCashFlow,
                    itemStyle: {
                        color: '#f59e0b'
                    }
                }
            ],
            grid: {
                left: '5%',
                right: '5%',
                bottom: '15%',
                top: '20%',
                containLabel: true
            }
        };
        
        chart.setOption(option);
        
        // 响应窗口大小变化
        window.addEventListener('resize', () => {
            chart.resize();
        });
    },

    /**
     * 创建市盈率图表
     * @param {string} containerId - 图表容器ID
     * @param {Array} data - 图表数据
     */
    createPEChart: function(containerId, data) {
        const chart = this.initChart(containerId);
        if (!chart) return;

        const dates = data.map(item => item.date);
        const peValues = data.map(item => item.pe);
        
        const option = {
            title: {
                text: '市盈率(PE)走势',
                left: 'center',
                textStyle: {
                    fontSize: 16,
                    fontWeight: 'bold'
                }
            },
            tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                    const dataIndex = params[0].dataIndex;
                    const date = dates[dataIndex];
                    const pe = peValues[dataIndex].toFixed(2);
                    return `${date}<br/>PE: ${pe}`;
                }
            },
            xAxis: {
                type: 'category',
                data: dates,
                axisLabel: {
                    interval: Math.floor(dates.length / 10),
                    rotate: 45
                }
            },
            yAxis: {
                type: 'value',
                name: 'PE'
            },
            series: [{
                name: 'PE',
                type: 'line',
                data: peValues,
                smooth: true,
                lineStyle: {
                    width: 3,
                    color: '#f59e0b'
                },
                itemStyle: {
                    color: '#f59e0b'
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(245, 158, 11, 0.5)' },
                        { offset: 1, color: 'rgba(245, 158, 11, 0.1)' }
                    ])
                }
            }],
            grid: {
                left: '5%',
                right: '5%',
                bottom: '15%',
                top: '15%',
                containLabel: true
            }
        };
        
        chart.setOption(option);
        
        // 响应窗口大小变化
        window.addEventListener('resize', () => {
            chart.resize();
        });
    },

    /**
     * 创建市场概况图表
     * @param {string} containerId - 图表容器ID
     * @param {Array} data - 图表数据
     */
    createMarketOverviewChart: function(containerId, data) {
        const chart = this.initChart(containerId);
        if (!chart) return;

        const dates = data.map(item => item.date);
        const peMedian = data.map(item => item.peMedian);
        const indexValue = data.map(item => item.indexValue);
        
        const option = {
            title: {
                text: 'A股市场概况',
                left: 'center',
                textStyle: {
                    fontSize: 16,
                    fontWeight: 'bold'
                }
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: function(params) {
                    const dataIndex = params[0].dataIndex;
                    const date = dates[dataIndex];
                    const pe = peMedian[dataIndex].toFixed(2);
                    const index = indexValue[dataIndex].toFixed(2);
                    return `${date}<br/>市危率: ${pe}<br/>指数: ${index}`;
                }
            },
            legend: {
                data: ['市危率', '指数'],
                top: '10%'
            },
            xAxis: {
                type: 'category',
                data: dates,
                axisLabel: {
                    interval: Math.floor(dates.length / 10),
                    rotate: 45
                }
            },
            yAxis: [
                {
                    type: 'value',
                    name: '市危率',
                    position: 'left'
                },
                {
                    type: 'value',
                    name: '指数',
                    position: 'right'
                }
            ],
            series: [
                {
                    name: '市危率',
                    type: 'bar',
                    data: peMedian,
                    yAxisIndex: 0,
                    itemStyle: {
                        color: function(params) {
                            const value = params.value;
                            if (value > 30) return '#ef4444';
                            if (value > 20) return '#f59e0b';
                            return '#10b981';
                        }
                    }
                },
                {
                    name: '指数',
                    type: 'line',
                    data: indexValue,
                    yAxisIndex: 1,
                    smooth: true,
                    lineStyle: {
                        width: 3,
                        color: '#3b82f6'
                    },
                    itemStyle: {
                        color: '#3b82f6'
                    }
                }
            ],
            grid: {
                left: '5%',
                right: '5%',
                bottom: '15%',
                top: '20%',
                containLabel: true
            }
        };
        
        chart.setOption(option);
        
        // 响应窗口大小变化
        window.addEventListener('resize', () => {
            chart.resize();
        });
    }
};