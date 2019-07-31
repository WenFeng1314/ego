package com.sxt.mapper;

import com.sxt.domain.Content;
import com.sxt.domain.ContentExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

 /**  
    * @title:  ${NAME}
    * @projectName ego
    * @description: ${PACKAGE_NAME}
    * @author WWF
    * @date 2019/5/26 17:46
    */
@Mapper
public interface ContentMapper extends IMapper<Content> {
    long countByExample(ContentExample example);

    int deleteByExample(ContentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Content record);

    int insertSelective(Content record);

    List<Content> selectByExample(ContentExample example);

    Content selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Content record, @Param("example") ContentExample example);

    int updateByExample(@Param("record") Content record, @Param("example") ContentExample example);

    int updateByPrimaryKeySelective(Content record);

    int updateByPrimaryKey(Content record);
}
