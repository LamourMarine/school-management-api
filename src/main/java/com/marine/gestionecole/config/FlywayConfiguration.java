package com.marine.gestionecole.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfiguration {

    @Autowired
    public FlywayConfiguration(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .locations("classpath:db/migration")
            .dataSource(dataSource)
            .load();
        
        flyway.migrate();
    }
}