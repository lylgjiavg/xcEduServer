package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Classname FreemarkerController
 * @Description Freemarker 控制层
 * @Date 2020/2/2 18:14
 * @Created by 姜立成
 */
@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/test1")
    public String freemarker(Map<String, Object> map) {

        //向数据模型放数据
        map.put("name", "黑马程序员");

        // stu1
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        // stu2
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());

        // friends
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);

        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);

        // stus
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus", stus);

        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);

        //向数据模型放数据
        map.put("stu1", stu1);

        //向数据模型放数据
        map.put("stuMap", stuMap);

        //返回模板文件名称
        return "test1";
    }

    @RequestMapping("/indexbanner")
    public String indexBanner(Map<String, Object> map) {

        String dataUrl = "http://localhost:31001/cms/config/get/5e37cd44c8b1695c026af5a8";
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();

        map.putAll(body);

        return "index_banner";
    }

    @RequestMapping("/course")
    public String course(Map<String, Object> map) {

        ResponseEntity<Map> forEntity =
                restTemplate.getForEntity("http://localhost:31200/course/courseview/4028e581617f945f01617f9dabc40000", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "course";
    }

}
