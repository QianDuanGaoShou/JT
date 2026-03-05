package com.guitar;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.guitar.mapper")
public class GuitarPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuitarPlatformApplication.class, args);
    }
}
