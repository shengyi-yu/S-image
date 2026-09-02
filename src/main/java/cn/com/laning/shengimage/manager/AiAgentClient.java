package cn.com.laning.shengimage.manager;

import cn.com.laning.shengimage.model.dto.ai.AiAnalyzeResult;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AI Agent 客户端
 *
 * 通过 HTTP 直连 Python 端的 ai-agent 服务（FastAPI）
 * 当前阶段不引入 Nacos/Feign，先跑通功能，后续再演进
 */
@Slf4j
@Component
public class AiAgentClient {

    @Value("${ai-agent.base-url:http://localhost:8000}")
    private String baseUrl;

    @Value("${ai-agent.timeout:60}")
    private int timeout;

    /**
     * 调用 Python 端 /api/ai/analyze 分析图片
     *
     * @param imageUrl 图片地址
     * @param question 可选的分析问题
     * @return 分析结果；调用失败返回 null（调用方自行降级，不阻塞主流程）
     */
    public AiAnalyzeResult analyze(String imageUrl, String question) {
        JSONObject body = new JSONObject();
        body.set("image_url", imageUrl);
        if (question != null) {
            body.set("question", question);
        }
        try {
            HttpResponse response = HttpRequest.post(baseUrl + "/api/ai/analyze")
                    .body(body.toString())
                    .contentType("application/json")
                    .timeout(timeout * 1000)
                    .execute();
            if (!response.isOk()) {
                log.warn("AI 分析 HTTP 状态异常: {}", response.getStatus());
                return null;
            }
            JSONObject json = JSONUtil.parseObj(response.body());
            if (json.getInt("code", 0) != 0) {
                log.warn("AI 分析业务失败: {}", json.getStr("message"));
                return null;
            }
            JSONObject data = json.getJSONObject("data");
            if (data == null) {
                return null;
            }
            return data.toBean(AiAnalyzeResult.class);
        } catch (Exception e) {
            log.error("调用 AI Agent 服务异常, imageUrl = {}", imageUrl, e);
            return null;
        }
    }

    /**
     * 调用 Python 端 /api/ai/chat 对话（后续前端聊天收口到 Java 时使用）
     *
     * @param message   用户消息
     * @param threadId  会话 id
     * @return 回复内容；调用失败返回 null
     */
    public String chat(String message, String threadId) {
        JSONObject body = new JSONObject();
        body.set("message", message);
        body.set("thread_id", threadId == null ? "default" : threadId);
        try {
            HttpResponse response = HttpRequest.post(baseUrl + "/api/ai/chat")
                    .body(body.toString())
                    .contentType("application/json")
                    .timeout(timeout * 1000)
                    .execute();
            if (!response.isOk()) {
                log.warn("AI 对话 HTTP 状态异常: {}", response.getStatus());
                return null;
            }
            JSONObject json = JSONUtil.parseObj(response.body());
            if (json.getInt("code", 0) != 0) {
                log.warn("AI 对话业务失败: {}", json.getStr("message"));
                return null;
            }
            return json.getStr("data");
        } catch (Exception e) {
            log.error("调用 AI Agent 服务异常, message = {}", message, e);
            return null;
        }
    }
}
