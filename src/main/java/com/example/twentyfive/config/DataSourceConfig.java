package com.example.twentyfive.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.edas.configcenter.config.ConfigService;
import com.alibaba.fastjson.JSON;
import com.example.twentyfive.vo.DataSourceVo;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * 数据源配置
 */

@Configuration
@MapperScan(basePackages = {"com.example.twentyfive.mapper"},sqlSessionFactoryRef = "sqlSessionFactory")
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
        //DataSourceVo dataSourceVo1 = JSON.parseObject(value, dataSourceVo.getClass());
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

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlsessionFactoryBean(@Qualifier("datasource") DruidDataSource dataSource) throws Exception{
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        config.setMapUnderscoreToCamelCase(true);
        config.setLogImpl(Slf4jImpl.class);
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfiguration(config);

        return sqlSessionFactoryBean.getObject();
    }

}
