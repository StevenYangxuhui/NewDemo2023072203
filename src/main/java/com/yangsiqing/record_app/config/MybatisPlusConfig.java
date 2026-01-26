package com.yangsiqing.record_app.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MybatisPlusConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * 配置 MyBatis-Plus 拦截器
     * 这是必需的，否则 MyBatis-Plus 的 BaseMapper 方法无法正常工作
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        return interceptor;
    }

    /**
     * 配置 SqlSessionFactory，使用 MyBatis-Plus 的 MybatisSqlSessionFactoryBean
     * 这是关键：必须使用 MybatisSqlSessionFactoryBean 而不是普通的 SqlSessionFactoryBean
     */
    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 配置 MyBatis-Plus
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addInterceptor(mybatisPlusInterceptor());
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}
