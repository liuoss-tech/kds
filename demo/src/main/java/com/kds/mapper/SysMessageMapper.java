package com.kds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kds.model.entity.SysMessage;
import com.kds.model.vo.ConversationVO;
import com.kds.model.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMessageMapper extends BaseMapper<SysMessage> {

    @Select("SELECT * FROM sys_message " +
            "WHERE is_deleted = 0 " +
            "AND ((sender_id = #{userId} AND receiver_id = #{contactUserId}) " +
            "OR (sender_id = #{contactUserId} AND receiver_id = #{userId})) " +
            "ORDER BY create_time ASC " +
            "LIMIT #{offset}, #{limit}")
    List<SysMessage> selectMessageHistory(@Param("userId") Long userId,
                                          @Param("contactUserId") Long contactUserId,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM sys_message " +
            "WHERE is_deleted = 0 " +
            "AND sender_id = #{contactUserId} " +
            "AND receiver_id = #{userId} " +
            "AND is_read = 0")
    int countUnreadByContact(@Param("userId") Long userId, @Param("contactUserId") Long contactUserId);

    @Select("SELECT COUNT(*) FROM sys_message " +
            "WHERE is_deleted = 0 " +
            "AND receiver_id = #{userId} " +
            "AND is_read = 0")
    int countTotalUnread(@Param("userId") Long userId);
}
