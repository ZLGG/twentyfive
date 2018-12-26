package com.example.twentyfive.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.edas.configcenter.config.ConfigService;
import com.example.twentyfive.vo.DataSourceVo;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * 数据源配置
 */

@Configuration
public class DataSourceConfig {
    @Resource
    Environment environment;
    @Bean(name = "datasource",destroyMethod = "close")
    public DruidDataSource dataSource() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        //获取tomcat中vm的配置
        String group = environment.getProperty("dtyunxi.service.group");
        //从edas中获取数据源配置
        String value = ConfigService.getConfig("yundt.smartsales.mall.appmgmt.datasourcevo", group, 3000L);
        DataSourceVo dataSourceVo = objectMapper.readValue(value, DataSourceVo.class);
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(dataSourceVo.getDriverClassName());
        dataSource.setUrl(dataSourceVo.getJdbcUrl());
        dataSource.setUsername(dataSourceVo.getJdbcUserName());
        dataSource.setPassword(dataSourceVo.getJdbcUserPassword());
        dataSource.setMaxActive(dataSourceVo.getMaxActive());
        dataSource.setValidationQuery(dataSourceVo.getValidationQuery());
        dataSource.setInitialSize(dataSourceVo.getInitialSize());
        dataSource.setMinIdle(dataSourceVo.getMinIdle());
        dataSource.setMaxWait(dataSourceVo.getMaxWait());
        return dataSource;
    }

}
