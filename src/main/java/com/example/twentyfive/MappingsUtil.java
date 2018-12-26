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
    private static Logger logger = LoggerFactory.getLogger(MappingsUtil.class);
    PreparedStatement pstate = null;
    DruidPooledConnection connection =null;
    DruidDataSource druidDataSource = null;
    public String createMappings(String dbName, String tableName, String parent) throws Exception {
        StringBuilder sql = new StringBuilder("select * from " + tableName);
        return create(dbName, sql.toString(),parent,tableName);
    }

    public String create(String dbName, String sql,String parent,String tableName) throws Exception {
        String[] types1 = new String[]{"TINYINT","SMALLINT","MEDIUMINT","BOOLEAN"};//integer
        String[] types2 = new String[]{"INT","BIGINT","FLOAT","DOUBLE"," REAL","BIT","SERIAL"};//long
        String[] types3 = new String[]{"DATE","DATETIME","TIMESTAMP","TIME","YEAR","CHAR","VARCHAR","TINYTEXT"};//text
        StringBuffer mappings = new StringBuffer("\"" + tableName + "\": {\n" +
                "        \"dynamic\": \"true\",\n" +
                "        \"_all\": {\n" +
                "          \"enabled\": false\n" +
                "        },");
        if (parent != null){
            mappings.append("\"_parent\": {\n" +
                    "          \"type\": \""+parent+"\"\n" +
                    "        },\n" +
                    "        \"_routing\": {\n" +
                    "          \"required\": true\n" +
                    "        },\n" +
                    "        ");
        }
        mappings.append("\n" +
                "        \"properties\": {");
        logger.info("SQL:{}", sql);
        try {
            druidDataSource = dbManager.getDataSource(dbName);
            connection = druidDataSource.getConnection();
            pstate = connection.prepareStatement(sql);
            ResultSet results = pstate.executeQuery();
            // 获取键名
            ResultSetMetaData md = results.getMetaData();
            for (int i = 1 ; i <= md.getColumnCount() ; i++ ) {
                boolean end = false;
                if (i == md.getColumnCount()) {
                    end = true;
                }
                if (Arrays.asList(types2).contains(md.getColumnTypeName(i))){
                    mappings.append("\"" + md.getColumnName(i) + "\":");
                    mappings.append("{\n" +
                            "            \"type\": \"long\"\n" +
                            "          }");
                }
                else if (Arrays.asList(types1).contains(md.getColumnTypeName(i))) {
                    mappings.append("\"" + md.getColumnName(i) + "\":");
                    mappings.append("{\n" +
                            "            \"type\": \"integer\"\n" +
                            "          }");
                }
                else  {
                    mappings.append("\"" + md.getColumnName(i) + "\":");
                    mappings.append("{\n" +
                            "            \"type\": \"text\",\n" +
                            "            \"fields\": {\n" +
                            "              \"keyword\": {\n" +
                            "                \"type\": \"keyword\",\n" +
                            "                \"ignore_above\": 256\n" +
                            "              }\n" +
                            "            }\n" +
                            "          }");
                }
                if (end == false){
                    mappings.append(",");
                }
            }
            mappings.append("}\n" +
                    "}");
            System.out.println(mappings);
            /*byte[] bytes = mappings.toString().getBytes();
            fileOutputStream.write(bytes);*/
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            dbManager.closeConnection(connection,pstate);
        }
        return mappings.toString();

    }
}
