package cn.com.laning.shengimage.model.dto.ai;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 对话请求
 */
@Data
public class AiChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户消息
     */
    private String message;

    /**
     * 会话 id（前端传用户 id，保证不同用户对话隔离）
     */
    private String threadId;
}
