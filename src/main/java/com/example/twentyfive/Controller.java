package com.example.twentyfive;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class Controller {
    @RequestMapping("/word")
    public String test() {
        return "hello,word";
    }
}
