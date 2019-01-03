package com.example.twentyfive.mapper;

import com.example.twentyfive.vo.CmptAreaVo;
import org.apache.ibatis.annotations.Select;

public interface CmptAreaMapper {
    @Select("select * from cmpt_area where id = #{id}")
    CmptAreaVo selectCmptArea(int id);
    @Select("select * from cmpt_area where name = #{name}")
    CmptAreaVo selectCmptAeraByVo(CmptAreaVo cmptAreaVo);
}
