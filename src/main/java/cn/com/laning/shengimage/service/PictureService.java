package cn.com.laning.shengimage.service;

import cn.com.laning.shengimage.model.dto.picture.*;
import cn.com.laning.shengimage.model.dto.user.UserQueryRequest;
import cn.com.laning.shengimage.model.entity.Picture;
import cn.com.laning.shengimage.model.entity.User;
import cn.com.laning.shengimage.model.vo.PictureVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lenovo
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2026-04-24 20:09:26
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取 PictureVO
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取列表
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 校验
     *
     * @param picture
     */
    void validPicture(Picture picture);


    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 文件审核参数
     *
     * @param picture
     * @param loginUser
     */
    void fileReviewParams(Picture picture, User loginUser);


    /**
     * 删除图片
     * @param pictureId
     * @param loginUser
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 编辑图片
     * @param pictureEditRequest
     * @param loginUser
     */
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 批量上传图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 返回上传成功的图片数
     */
    int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest,
                                 User loginUser);


    /**
     * 删除图片COS
     *
     * @param oldPicture
     */
    void clearPictureFile(Picture oldPicture);


    /**
     * 权限校验
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser, Picture picture);

}
