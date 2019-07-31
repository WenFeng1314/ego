package com.sxt.mapper;

import com.sxt.domain.Goods;
import com.sxt.domain.GoodsExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

 /**  
    * @title:  ${NAME}
    * @projectName ego
    * @description: ${PACKAGE_NAME}
    * @author WWF
    * @date 2019/5/27 22:13
    */
@Mapper
public interface GoodsMapper extends IMapper<Goods> {
    long countByExample(GoodsExample example);

    int deleteByExample(GoodsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Goods record);

    int insertSelective(Goods record);

    List<Goods> selectByExample(GoodsExample example);

    Goods selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Goods record, @Param("example") GoodsExample example);

    int updateByExample(@Param("record") Goods record, @Param("example") GoodsExample example);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);
}
