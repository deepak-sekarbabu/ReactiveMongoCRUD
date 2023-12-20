package com.deepak.mongoreactive.configuration;


import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
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
        return MongoClients.create("mongodb://" + mongoProperties.getUserName() + ":" + mongoProperties.getPassword() + "@" + mongoProperties.getHost() + ":" + mongoProperties.getPort());
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

/*    @Bean
    @ConditionalOnProperty(prefix = "job.autorun", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner loadData(UserRepository repository) {
        return (args) -> {
            // save a couple of users
            var users = Flux.just(
                    new User("Test", "10", "Test@test.com")
            );
            User user = User.builder().firstName()
            repository.saveAll(users).subscribe();
        };
    }*/
}