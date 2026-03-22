package com.kds;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kds.mapper")
public class KdsApplication {

    public static void main(String[] args) {
        SpringApplication.run(KdsApplication.class, args);
    }
}
