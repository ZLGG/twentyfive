package com.example.twentyfive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 导入rpc配置
 */
@Configuration
@ImportResource({"classpath*:rpc/*.xml",})
public class TestImportConfig {
}
