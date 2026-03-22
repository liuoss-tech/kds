package com.kds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kds.model.entity.BusiStudentParent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusiStudentParentMapper extends BaseMapper<BusiStudentParent> {

    @Select("SELECT student_id FROM busi_student_parent WHERE parent_user_id = #{parentUserId} AND is_deleted = 0")
    List<Long> selectChildIdsByParentUserId(@Param("parentUserId") Long parentUserId);
}
