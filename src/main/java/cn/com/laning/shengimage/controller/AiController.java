package cn.com.laning.shengimage.controller;

import cn.com.laning.shengimage.annotation.AuthCheck;
import cn.com.laning.shengimage.common.BaseResponse;
import cn.com.laning.shengimage.common.ResultUtils;
import cn.com.laning.shengimage.exception.ErrorCode;
import cn.com.laning.shengimage.exception.ThrowUtils;
import cn.com.laning.shengimage.manager.AiAgentClient;
import cn.com.laning.shengimage.model.dto.ai.AiChatRequest;
import cn.com.laning.shengimage.model.entity.User;
import cn.com.laning.shengimage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * AI 助手接口
 * 设计：聊天收口到 Java（前端只认识 8123），由 Java 代理调用 Python 端，
 * 这样 AI 额度有登录保护、后续可加限流/审计，Python 端保持纯粹算法服务。
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AiAgentClient aiAgentClient;

    @Resource
    private UserService userService;

    /**
     * AI 对话
     * 需要登录（@AuthCheck 默认角色即强制要求登录，保护 AI 额度）
     */
    @PostMapping("/chat")
    @AuthCheck
    public BaseResponse<String> chat(@RequestBody AiChatRequest request, HttpServletRequest httpServletRequest) {
        // 校验参数
        ThrowUtils.throwIf(request == null || request.getMessage() == null || request.getMessage().trim().isEmpty(),
                ErrorCode.PARAMS_ERROR, "消息不能为空");
        // 确保登录用户存在（@AuthCheck 已要求登录）
        User loginUser = userService.getLoginUser(httpServletRequest);
        // 调用 Python 端 AI 服务
        String reply = aiAgentClient.chat(request.getMessage(), request.getThreadId());
        ThrowUtils.throwIf(reply == null, ErrorCode.SYSTEM_ERROR, "AI 服务暂时不可用，请稍后重试");
        return ResultUtils.success(reply);
    }
}
