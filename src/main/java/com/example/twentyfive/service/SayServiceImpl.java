package com.example.twentyfive.service;

import org.springframework.stereotype.Service;

@Service("sayService")
public class SayServiceImpl implements SayService {
    @Override
    public String say() {
        System.out.println("成功调用服务");
        return "成功调用服务";
    }

}
