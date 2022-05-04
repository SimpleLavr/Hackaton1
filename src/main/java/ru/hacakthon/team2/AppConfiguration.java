package ru.hacakthon.team2;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@SpringBootApplication
@PropertySource("application.properties")
public class AppConfiguration {
    public static void main(String[] args) {
        SpringApplication.run(AppConfiguration.class, args);
    }

    @Bean
    DataSource dataSource(Environment environment) {
        HikariConfig dbHikariConfig = new HikariConfig();
        dbHikariConfig.setJdbcUrl(environment.getProperty("database.url"));
        dbHikariConfig.setDriverClassName(environment.getProperty("database.driver"));
        dbHikariConfig.setUsername(environment.getProperty("database.username"));
        dbHikariConfig.setPassword(environment.getProperty("database.password"));
        return new HikariDataSource(dbHikariConfig);
    }
}
