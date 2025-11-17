package com.csh.mypicturebackend.mapper;

import com.csh.mypicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author YRoderick
* @description 针对表【space(空间)】的数据库操作Mapper
* @createDate 2025-10-29 19:54:51
* @Entity com.csh.mypicturebackend.model.entity.Space
*/
public interface SpaceMapper extends BaseMapper<Space> {


    // 自定义SQL查询
    /**
     * 获取存储使用量排名前 N 的空间
     * @param topN 排名前 N
     * @return List<Space>
     */
    @Select("SELECT id, spaceName, userId, totalSize " +
            "FROM space " +
            "ORDER BY totalSize DESC " +
            "LIMIT #{topN}")
    List<Space> getTopNSpaceUsage(int topN);

    /**
     * 删除某用户的所有空间
     *
     * @param userId 用户 ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM space WHERE userId = #{userId}")
    int deleteByUserId(Long userId);
}
