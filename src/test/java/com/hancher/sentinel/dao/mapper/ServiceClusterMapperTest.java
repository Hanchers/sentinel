package com.hancher.sentinel.dao.mapper;

import com.hancher.sentinel.SentinelApplicationTests;
import com.hancher.sentinel.entity.ServiceCluster;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceClusterMapperTest extends SentinelApplicationTests {

    @Autowired
    private ServiceClusterMapper serviceClusterMapper;



    @Test
    public void testSelectById() {
        ServiceCluster serviceCluster = serviceClusterMapper.selectOneById(1L);
        System.out.println(serviceCluster);
        Assertions.assertEquals(1L, serviceCluster.getId());
    }
}
