package main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class DefaultController {
    @RequestMapping(value = "/")
    public String index(){
        return "index";
    }
    @GetMapping(value = "/**/{path:[^.]*}")
    public String redirect(){
        return "forward:/";
    }
}
