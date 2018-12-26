package com.example.twentyfive;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("createMappings")
public class testController {

    @Autowired
    DbManger dbManger;
    @Autowired
    MappingsUtil mappingsUtil;

    DruidPooledConnection connection = null;
    PreparedStatement pstate = null;

    @RequestMapping("/index")
    public String byIndexName(String indexName) throws Exception{

        FileOutputStream fileOutputStream =null;
        fileOutputStream = new FileOutputStream("C:\\Users\\123\\Desktop\\Mappings.txt");

        indexName = "twcart_qa_trade";
        //获取连接
        connection = dbManger.connection();
        String sql = "select * from s_indexbuilder_config where index_name = '"+indexName+"' and dr = 0";
        List<Map<String,Object>> list = new ArrayList<>();
        try {
            //connection = druidDataSource.getConnection();
            pstate = connection.prepareStatement(sql);
            ResultSet resultSet = pstate.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()){
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(metaData.getColumnName(i), packageObject(resultSet.getObject(i)));
                }
                list.add(map);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            //dbManger.closeConnection(connection,pstate);
            if (pstate != null) {
                pstate.close();
            }

        }
        log.info("result:{}", JSON.toJSON(list));
       /* writeMappings("{\n" +
                "    \"mappings\": {\n" +
                "      ");*/
       fileOutputStream.write(new String("{\n" +
               "    \"mappings\": {\n" +
               "      ").getBytes());
        for (Map map : list) {
            String parentSourceTable = null;
            String sourceDbName = map.get("source_db").toString().trim();
            String sourceTable = map.get("source_table").toString();
            String parentId = map.get("parent_id").toString();
            if (!parentId.equals("0")) {
                //connection = druidDataSource.getConnection();
                pstate =  connection.prepareStatement("select * from s_indexbuilder_config where dr = 0 and id ='" + parentId + "'");
                ResultSet resultSet = pstate.executeQuery();
                while (resultSet.next()){
                     parentSourceTable = resultSet.getString(4);
                }
            }
            String mappings = mappingsUtil.createMappings(sourceDbName, sourceTable, parentSourceTable);
           // writeMappings(mappings);
            fileOutputStream.write(mappings.getBytes());
            if (map != list.get(list.size() - 1)) {
                //writeMappings(",");
                fileOutputStream.write(new String(",").getBytes());
            }
            if (pstate != null) {
                pstate.close();
            }

        }
        /*writeMappings("\n" +
                "   }\n" +
                "}");*/
        fileOutputStream.write(new String("\n" +
                "   }\n" +
                "}").getBytes());
        dbManger.closeConnection(connection,pstate);

        fileOutputStream.close();

        return "hello,word";
    }
    @RequestMapping("/table")
    public String bySourceTable(String table) throws Exception{
        FileOutputStream fileOutputStream =null;
        fileOutputStream = new FileOutputStream("C:\\Users\\123\\Desktop\\Mappings.txt");

        if (connection == null) {
            connection = dbManger.connection();
        }

        PreparedStatement statement = connection.prepareStatement("select * from s_indexbuilder_config where dr = 0 and source_table = '" + table + "'");
        ResultSet resultSet = statement.executeQuery();
        String sourceDbName = null;
        String sourceTable = null;
        String parent_id = null;
        String parentSourceTable = null;
        while (resultSet.next()) {
            sourceDbName = resultSet.getString(3);
            sourceTable = resultSet.getString(4);
            parent_id = resultSet.getString(5);
        }
        if (statement != null) {
            statement.close();
        }
        if (!parent_id.equals("0")) {
            pstate = connection.prepareStatement("select * from s_indexbuilder_config where dr = 0 and id ='" + parent_id + "'");
            ResultSet resultSet1 = pstate.executeQuery();
            while (resultSet1.next()) {
                parentSourceTable = resultSet1.getString(4);
            }
        }
        String mappings = mappingsUtil.createMappings(sourceDbName, sourceTable, parentSourceTable);
        //writeMappings(mappings);
        fileOutputStream.write(mappings.getBytes());
        return "success";
    }
    /*void writeMappings(String mappings){
        try {
            if (fileOutputStream == null) {
            }
            fileOutputStream.write(mappings.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/
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
