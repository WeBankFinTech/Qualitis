package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-08 10:26
 * @description
 */
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryWorker"
        , basePackages = {"com.webank.wedatasphere.qualitis.worker"})
public class WorkerDataSourceConfig {

    @Resource
    @Qualifier("workerDataSource")
    private DataSource workerDataSource;

    @Resource
    private HibernateJpaVendorAdapter hibernateJpaVendorAdapter;

    @Resource
    private Map<String, Object> vendorProperties;

    @Bean(name = "entityManagerFactoryWorker")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        factoryBean.setDataSource(workerDataSource);
        factoryBean.setJpaPropertyMap(vendorProperties);
        factoryBean.setPackagesToScan("com.webank.wedatasphere.qualitis");
        return factoryBean;
    }
}
