package com.teamtreehouse.instateam.config;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
public class DataConfig {
    @Autowired
    private Environment environment;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        Resource config = new ClassPathResource("hibernate.cfg.xml");
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setConfigLocation(config);
        sessionFactory.setPackagesToScan(environment.getProperty("instateam.entity.package"));
        sessionFactory.setDataSource(dataSource());
        return sessionFactory;
    }

    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(environment.getProperty("instateam.db.driver"));
        dataSource.setUrl(environment.getProperty("instateam.db.url"));
        dataSource.setUsername(environment.getProperty("instateam.db.username"));
        dataSource.setPassword(environment.getProperty("instateam.db.password"));

        return dataSource;
    }
}
