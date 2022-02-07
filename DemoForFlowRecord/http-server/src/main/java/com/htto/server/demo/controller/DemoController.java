package com.htto.server.demo.controller;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/test")
    public String invokeGet(HttpServletRequest request) {
        System.out.println("### http get invoke success");
        return request.getParameterMap().toString();
//        return "success";
    }

    @PostMapping("/test")
    public String invokePost(HttpServletRequest request) {
        System.out.println(request);
        System.out.println("### http post invoke success");
        return "success";
    }

}
