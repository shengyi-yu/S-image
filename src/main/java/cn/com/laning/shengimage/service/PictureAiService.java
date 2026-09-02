package cn.com.laning.shengimage.service;

import cn.com.laning.shengimage.manager.AiAgentClient;
import cn.com.laning.shengimage.mapper.PictureMapper;
import cn.com.laning.shengimage.model.dto.ai.AiAnalyzeResult;
import cn.com.laning.shengimage.model.entity.Picture;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AI 图片信息补全服务（异步）
 *
 * 设计要点：
 * 1. 独立成一个 Bean，是为了让 @Async 通过 Spring 代理生效（同类内 this 调用不生效）
 * 2. AI 生成失败绝不能影响上传主流程：异常全部捕获，只记日志
 */
@Service
@Slf4j
public class PictureAiService {

    @Resource
    private AiAgentClient aiAgentClient;

    @Resource
    private PictureMapper pictureMapper;

    /**
     * 异步调用 Python 端分析图片，把 AI 标签/描述写回数据库
     *
     * @param picture 已入库的图片（id 必须已生成）
     */
    @Async
    public void fillAiInfo(Picture picture) {
        if (picture == null || picture.getId() == null) {
            log.warn("AI 补全跳过：图片为空或 id 未生成");
            return;
        }
        try {
            AiAnalyzeResult result = aiAgentClient.analyze(picture.getUrl(), null);
            if (result == null || result.getTags() == null || result.getTags().isEmpty()) {
                log.warn("AI 分析无结果，跳过，pictureId = {}", picture.getId());
                return;
            }
            // 只更新 AI 字段，避免覆盖用户手动修改的其它字段
            Picture update = new Picture();
            update.setId(picture.getId());
            update.setAiTags(JSONUtil.toJsonStr(result.getTags()));
            update.setAiDescription(result.getDescription());
            boolean updated = pictureMapper.updateById(update) > 0;
            if (!updated) {
                log.warn("AI 标签入库失败，pictureId = {}", picture.getId());
            } else {
                log.info("AI 标签生成成功, pictureId = {}, tags = {}", picture.getId(), result.getTags());
            }
        } catch (Exception e) {
            log.error("AI 标签生成异常, pictureId = {}", picture.getId(), e);
        }
    }
}
