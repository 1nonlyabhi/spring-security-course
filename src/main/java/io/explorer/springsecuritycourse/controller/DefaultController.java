package io.explorer.springsecuritycourse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefaultController {

    @RequestMapping("/")
    public @ResponseBody String hello() {
        return "Hello, World!";
    }
}
