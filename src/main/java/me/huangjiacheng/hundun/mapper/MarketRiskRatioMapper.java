package me.huangjiacheng.hundun.mapper;

import me.huangjiacheng.hundun.model.MarketRiskRatio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 市危率数据访问接口
 */
@Mapper
public interface MarketRiskRatioMapper {
    
    /**
     * 插入市危率数据
     */
    int insert(MarketRiskRatio marketRiskRatio);
    
    /**
     * 根据日期更新市危率数据
     */
    int updateByDate(MarketRiskRatio marketRiskRatio);
    
    /**
     * 根据日期查询市危率数据
     */
    MarketRiskRatio selectByDate(@Param("date") String date);
    
    /**
     * 查询所有市危率数据
     */
    List<MarketRiskRatio> selectAll();
    
    /**
     * 根据日期范围查询市危率数据
     */
    List<MarketRiskRatio> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
