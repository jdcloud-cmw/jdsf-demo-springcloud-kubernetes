package com.jdcloud.demo.server.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @program: jdsf_demo
 * @description: 业务实现类
 * @author: jdsf
 * @create: 2018-12-20 20:03
 **/
@Service
public class BusinessService {


    public Integer getBusinessRandom(){
        Random random = new Random();
        return random.nextInt();
    }
}
