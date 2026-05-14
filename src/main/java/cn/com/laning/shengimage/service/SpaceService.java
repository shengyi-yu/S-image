package cn.com.laning.shengimage.service;

import cn.com.laning.shengimage.model.dto.space.SpaceQueryRequest;
import cn.com.laning.shengimage.model.entity.Space;
import cn.com.laning.shengimage.model.vo.SpaceVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lenovo
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2026-05-12 15:13:06
 */
public interface SpaceService extends IService<Space> {
    /**
     * 校验
     *
     * @param space
     * @param add
     */
    void validSpace(Space space, boolean add);

    /**
     * 获取 SpaceVO
     *
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取列表
     *
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间级别填充空间对象
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);
}
