package com.example.twentyfive.service;

import org.springframework.stereotype.Service;

@Service("sayService")
public class SayServiceImpl implements SayService {
    @Override
    public void say() {
        System.out.println("成功调用服务");
    }

}
