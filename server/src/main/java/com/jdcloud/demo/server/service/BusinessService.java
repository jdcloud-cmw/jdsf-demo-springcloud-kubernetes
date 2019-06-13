package com.jdcloud.demo.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecwid.consul.v1.ConsulClient;

import java.util.Random;

/**
 * @program: jdsf_demo
 * @description: 业务实现类
 * @author: jdsf
 * @create: 2018-12-20 20:03
 **/
@Service
public class BusinessService {

    @Autowired
    private ConfigService configService;

    public String  getBusinessRandom(){
        Random random = new Random();
        String testValue = configService.getTestKey();
        return testValue + ":::" + random.nextInt();
    }
}
