package com.csh.mypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csh.mypicturebackend.model.dto.space.SpaceAddRequest;
import com.csh.mypicturebackend.model.dto.space.SpaceQueryRequest;
import com.csh.mypicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csh.mypicturebackend.model.entity.User;
import com.csh.mypicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author YRoderick
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-10-29 19:54:51
*/
public interface SpaceService extends IService<Space> {

    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    void validSpace(Space space, boolean add);

    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    void fillSpaceBySpaceLevel(Space space);

    void checkSpaceAuth(User loginUser, Space space);
}
