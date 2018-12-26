package com.example.twentyfive;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DbManger {
    PreparedStatement statement = null;
    DruidDataSource dataSource = null;
    DruidPooledConnection connection = null;
    @Autowired
    DruidDataSource druidDataSource;
    static HashMap<String, DruidDataSource> dataSourceHashMap = Maps.newHashMap();
    public  DruidPooledConnection connection() throws Exception{
        /*DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://172.16.19.117:3306/twcart_qa_mall_mgmt?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        druidDataSource.setInitialSize(20);
        druidDataSource.setMaxActive(20);
        druidDataSource.setRemoveAbandonedTimeout(1800);
        connection = druidDataSource.getConnection();
        this.connection = druidDataSource.getConnection();*/
        this.connection = druidDataSource.getConnection();
        return this.connection;
    }

    public DruidDataSource getDataSource(String db_name) throws Exception{

        String sql = "select * from s_indexbuilder_datasource where dr = 0 and db_name = '"+db_name+"'";
        Map<String, Object> map = new HashMap<>();
        try {
            //connection = dataSource().getConnection();
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    map.put(metaData.getColumnName(i), packageObject(resultSet.getObject(i)));
                }
            }
             dataSource = createDataSource(map);


        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }

        return dataSource;
    }



    public DruidDataSource createDataSource(Map map) {
        String db_name = map.get("db_name").toString().trim();
        String driver = map.get("driver").toString().trim();
        String db_url = map.get("db_url").toString().trim();
        DruidDataSource druidDataSource = null;
        if (dataSourceHashMap.containsKey(db_name)) {
            druidDataSource = dataSourceHashMap.get(db_name);
        }
        if (druidDataSource == null) {
            druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(driver);
            druidDataSource.setUrl(db_url);
            druidDataSource.setUsername("root");
            druidDataSource.setPassword("root");
            druidDataSource.setInitialSize(10);
            druidDataSource.setMaxActive(20);
            druidDataSource.setRemoveAbandonedTimeout(1800);
            dataSourceHashMap.put(db_name, druidDataSource);
        }
        return druidDataSource;
    }

    public void closeConnection(Connection connection,PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            log.info("关闭连接失败 {}",e);
        }

    }


    public Object packageObject(Object object) {
        if (object == null)
            return object;
        if (object.equals(false)) {
            return "0";
        }
        if (object.equals(true)) {
            return "1";
        }
        return object;
    }
}
