package com.sxt.mapper;

import com.sxt.domain.Cart;
import com.sxt.domain.CartExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
   * @title:  ${NAME}
   * @projectName ego
   * @description: ${PACKAGE_NAME}
   * @author WWF
   * @date 2019/5/31 12:39
   */
@Mapper
public interface CartMapper {
   long countByExample(CartExample example);

   int deleteByExample(CartExample example);

   int deleteByPrimaryKey(Integer id);

   int insert(Cart record);

   int insertSelective(Cart record);

   List<Cart> selectByExample(CartExample example);

   Cart selectByPrimaryKey(Integer id);

   int updateByExampleSelective(@Param("record") Cart record, @Param("example") CartExample example);

   int updateByExample(@Param("record") Cart record, @Param("example") CartExample example);

   int updateByPrimaryKeySelective(Cart record);

   int updateByPrimaryKey(Cart record);
   //直接去数据库查询总条数
   int getDatabaseTatol(Integer uid);

}
