package com.farmer.seckill.mapper;

import com.farmer.seckill.entity.SecOrder;
import com.farmer.seckill.entity.SecOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseSecOrderMapper {
    long countByExample( SecOrderExample example);

    int deleteByExample( SecOrderExample example);

    int deleteByPrimaryKey( Long id);

    int insert( SecOrder record);

    int insertSelective( SecOrder record);

    List<SecOrder> selectByExampleWithBLOBs( SecOrderExample example);

    List<SecOrder> selectByExample( SecOrderExample example);

    SecOrder selectByPrimaryKey( Long id);

    int updateByExampleSelective(@Param("record") SecOrder record, @Param("example") SecOrderExample example);

    int updateByExampleWithBLOBs(@Param("record") SecOrder record, @Param("example") SecOrderExample example);

    int updateByExample(@Param("record") SecOrder record, @Param("example") SecOrderExample example);

    int updateByPrimaryKeySelective( SecOrder record);

    int updateByPrimaryKeyWithBLOBs( SecOrder record);

    int updateByPrimaryKey( SecOrder record);
}