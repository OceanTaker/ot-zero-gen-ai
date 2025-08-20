package com.oceantaker.otzerogenai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 避免在每个 Mapper 接口上单独添加 @Mapper注解，同时提供更灵活的批量管理能力
@MapperScan("com.oceantaker.otzerogenai.mapper")
public class OtZeroGenAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtZeroGenAiApplication.class, args);
    }

}
