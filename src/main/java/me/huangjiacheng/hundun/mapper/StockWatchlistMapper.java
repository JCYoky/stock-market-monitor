package me.huangjiacheng.hundun.mapper;

import me.huangjiacheng.hundun.model.StockWatchlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自选股数据访问接口
 */
@Mapper
public interface StockWatchlistMapper {
    
    /**
     * 插入自选股
     */
    int insert(StockWatchlist stockWatchlist);
    
    /**
     * 根据股票代码删除自选股
     */
    int deleteByStockCode(@Param("stockCode") String stockCode);
    
    /**
     * 更新自选股信息
     */
    int update(StockWatchlist stockWatchlist);
    
    /**
     * 根据股票代码查询自选股
     */
    StockWatchlist selectByStockCode(@Param("stockCode") String stockCode);
    
    /**
     * 查询所有自选股
     */
    List<StockWatchlist> selectAll();
    
    /**
     * 根据股票类型查询自选股
     */
    List<StockWatchlist> selectByStockType(@Param("stockType") Integer stockType);
}
