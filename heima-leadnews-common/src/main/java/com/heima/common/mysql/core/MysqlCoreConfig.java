package com.heima.common.mysql.core;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "mysql.core")
@PropertySource("classpath:mysql-core-jdbc.properties")
@MapperScan(basePackages = "com.heima.model.mappers", sqlSessionFactoryRef = "mysqlCoreSqlSessionFactory")
public class MysqlCoreConfig {
    String jdbcUrl;
    String jdbcUserName;
    String jdbcPassword;
    String jdbcDriver;
    String rootMapper;//mapper文件在classpath下存放的根路径
    String aliasesPackage;//别名包

    /**
     * 配置数据库连接池,采用Hikari，是整合springboot最快的连接池
     */
    @Bean("mysqlCoreDataSource")
    public DataSource mysqlCoreDataSource(){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(this.getJdbcUserName());
        hikariDataSource.setPassword(this.getRealPassword());
        hikariDataSource.setJdbcUrl(this.getJdbcUrl());
        hikariDataSource.setDriverClassName(this.getJdbcDriver());
        // 最大连接数
        hikariDataSource.setMaximumPoolSize(50);
        // 最小连接数
        hikariDataSource.setMinimumIdle(5);
        return hikariDataSource;
    }

    // 密码反转
    public String getRealPassword(){
        return StringUtils.reverse(this.getJdbcPassword());
    }

    /**
     * 创建mybatis的工厂对象并放入容器中
     */

    @Bean("mysqlCoreSqlSessionFactory")
    public SqlSessionFactoryBean mysqlCoreSqlSessionFactory(@Qualifier("mysqlCoreDataSource") DataSource mysqlCoreDataSource) throws IOException {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        // 数据源
        sessionFactory.setDataSource(mysqlCoreDataSource);
        // 别名
        sessionFactory.setTypeAliasesPackage(this.getAliasesPackage());
        // mapper文件路径
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(this.getMapperFilePath());
        sessionFactory.setMapperLocations(resources);
        // 开启自动驼峰标识
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);
        return sessionFactory;
    }

    /**
     * 拼接Mapper.xml文件路径
     */
    public String getMapperFilePath(){
        return new StringBuffer().append("classpath:").append(this.getRootMapper()).append("/**/*.xml").toString();
    }
}
