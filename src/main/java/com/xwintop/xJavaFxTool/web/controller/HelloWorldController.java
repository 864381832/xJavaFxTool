package com.xwintop.xJavaFxTool.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot HelloWorld 案例
 * <p>
 * Created by bysocket on 16/4/26.
 */
@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @RequestMapping("/say")
    public String sayHello() {
        return "Hello,World!";
    }
}
