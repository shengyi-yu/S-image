package cn.com.laning.shengimage.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 批量导入图片接口请求参数
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    private static final long serialVersionUID = 1L;
}
