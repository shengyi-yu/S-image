package cn.com.laning.shengimage.controller;


import cn.com.laning.shengimage.common.BaseResponse;
import cn.com.laning.shengimage.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 *
 */

@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping("")
    public BaseResponse<String> health() {
        return ResultUtils.success("OK");
    }
}
