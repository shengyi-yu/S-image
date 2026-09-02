package cn.com.laning.shengimage.model.dto.ai;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AI 图片分析结果（对应 Python 端 /api/ai/analyze 的返回 data 字段）
 */
@Data
public class AiAnalyzeResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片描述
     */
    private String description;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 识别到的物体
     */
    private List<String> objects;

    /**
     * 图片风格
     */
    private String style;

    /**
     * 场景描述
     */
    private String scene;
}
