package com.nfy.cancerapp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 扫描 Mapper，配合 spring.datasource 访问 MySQL（报告「数据库访问」截图位：本类 + application.properties + 任一 *Mapper.java）。72203
 */
@Configuration
@MapperScan("com.nfy.cancerapp.mapper")
public class MybatisPlusConfig {
}
