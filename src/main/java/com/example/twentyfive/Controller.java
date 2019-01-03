package com.example.twentyfive;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.example.twentyfive.mapper.CmptAreaMapper;
import com.example.twentyfive.vo.CmptAreaVo;
import com.example.twentyfive.vo.UserVo;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hello")
public class Controller {
    @Resource
    CmptAreaMapper cmptAreaMapper;
    @Autowired
    DruidDataSource dataSource;
    @Autowired
    JedisPool jedisPool;

    @RequestMapping("/word")
    public String test() {
        CmptAreaVo cmptAreaVo1 = new CmptAreaVo();
        cmptAreaVo1.setName("台北市");
        CmptAreaVo cmptAreaVo = cmptAreaMapper.selectCmptArea(3490);
        CmptAreaVo cmptAreaVo2 = cmptAreaMapper.selectCmptAeraByVo(cmptAreaVo1);
        return "hello,word"+cmptAreaVo.toString()+cmptAreaVo2.toString();
    }

    @RequestMapping("/connetion")
    public String getConnection() throws Exception{
        for (int i = 0; i < 20; i++) {
            DruidPooledConnection connection = dataSource.getConnection();
            connection.close();
            boolean closed = connection.isClosed();
            System.out.println(closed);
        }
        return "success";
    }

    @RequestMapping(value="/getjson")
    public String getJson(@RequestBody List<UserVo> userVo,String name) {
        return JSON.toJSONString(userVo)+name;
    }


    @RequestMapping("getRedis")
    public String getRedis() {

        Jedis jedis = jedisPool.getResource();
        String abc = jedis.get("abc");
        System.out.println(abc);
        String hget = jedis.hget("item1", "id");
        Long aLong = jedis.hset("item1", "age", "21");
        Map<String, String> map = new HashMap<>();
        map.put("hige", "180");
        map.put("time", "14.49");
        String s = jedis.hmset("item1", map);
        Long length = jedis.hlen("item1");
        String s1 = jedis.hget("item1", "hige");
        Map<String, String> item1 = jedis.hgetAll("item1");

        jedis.close();
        return JSON.toJSONString(hget)+JSON.toJSONString(item1);
    }

}
