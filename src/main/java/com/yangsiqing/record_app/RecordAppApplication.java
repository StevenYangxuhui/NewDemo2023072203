package com.yangsiqing.record_app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class})
@MapperScan("com.yangsiqing.record_app.mapper")
public class RecordAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecordAppApplication.class, args);
	}

}
