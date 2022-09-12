package web.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
@ComponentScan(value = {"dao", "model", "service"})
public class PersistenceJPAConfig {
    protected static Logger log = Logger.getLogger(PersistenceJPAConfig.class.getName());

    @Autowired
    private Environment env;

    PersistenceJPAConfig() {
        super();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        log.debug("entityManagerFactory: <-");

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "dao", "model", "service"});

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        em.afterPropertiesSet();

        return em.getNativeEntityManagerFactory();
    }

    @Bean
    public DataSource dataSource() {
        log.debug("dataSource: <-");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driver"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties prop = new Properties();
        prop.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        prop.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        prop.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        prop.put("hibernate.current_session_context_class", "thread");

        return prop;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        log.debug("transactionManager: <-");

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory());

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        log.debug("exceptionTranslation: <-");

        return new PersistenceExceptionTranslationPostProcessor();
    }
}
