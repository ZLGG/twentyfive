package com.example.twentyfive;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;

@Service
public class MappingsUtil {
    @Autowired
    private DbManger dbManager;
    @Autowired
    DdlCreateMapping ddlCreateMapping;
    private static Logger logger = LoggerFactory.getLogger(MappingsUtil.class);

    public String createMappings(String dbName, String tableName, String parent, int flag) throws Exception {
        PreparedStatement pstate = null;
        DruidPooledConnection connection =null;
        String sql = new String("SHOW CREATE TABLE " + tableName);
        String ddl = null;
        String mappings = null;
        logger.info("SQL:{}", sql);
        try {
            DruidDataSource druidDataSource = dbManager.getDataSource(dbName);
            connection = druidDataSource.getConnection();
            pstate = connection.prepareStatement(sql);
            ResultSet results = pstate.executeQuery();
            while (results.next()) {
                ddl = results.getString(2);
            }

        } catch (Exception e) {
            logger.error("", e);
        } finally {
            dbManager.closeConnection(connection,pstate);
        }
        if (ddl != null) {
            mappings = ddlCreateMapping.createMappings(ddl, parent,flag);
        }
        return mappings;
    }
}
