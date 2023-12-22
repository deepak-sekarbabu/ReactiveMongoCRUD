package com.deepak.mongoreactive.configuration;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@Configuration
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {

    private final MongoProperties mongoProperties;

    public MongoConfiguration(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    @Override
    protected String getDatabaseName() {
        return "users";
    }

    @Override
    @Bean
    public MongoClient reactiveMongoClient() {
        return MongoClients
                .create("mongodb://" + this.mongoProperties.getUserName() + ":" + this.mongoProperties.getPassword()
                        + "@" + this.mongoProperties.getHost() + ":" + this.mongoProperties.getPort());
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(this.reactiveMongoClient(), this.getDatabaseName());
    }
}