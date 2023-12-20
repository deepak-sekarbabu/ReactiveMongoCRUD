package com.deepak.mongoreactive.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mongo")
@Data
public class MongoProperties {

    private String userName;
    private String password;
    private String databaseName;
    private String host;
    private Integer port;
}