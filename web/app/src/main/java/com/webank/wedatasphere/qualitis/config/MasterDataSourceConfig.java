package com.webank.wedatasphere.qualitis.config;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-08 10:44
 * @description
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryMaster"
        , basePackages = "com.webank.wedatasphere.qualitis"
        , excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.webank.wedatasphere.qualitis.worker.*"}))
public class MasterDataSourceConfig {

    @Resource
    private DataSource dataSource;

    @Resource
    private HibernateJpaVendorAdapter hibernateJpaVendorAdapter;

    @Resource
    private Map<String, Object> vendorProperties;

    @Primary
    @Bean(name = "entityManagerFactoryMaster")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaPropertyMap(vendorProperties);
        factoryBean.setPackagesToScan("com.webank.wedatasphere.qualitis");
        return factoryBean;
    }
}
