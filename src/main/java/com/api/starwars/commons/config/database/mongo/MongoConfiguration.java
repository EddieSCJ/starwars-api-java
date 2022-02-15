package com.api.starwars.commons.config.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.SpringDataMongoDB;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.ReadPreference.primary;
import static com.mongodb.ReadPreference.secondaryPreferred;

@Slf4j
@Configuration
@EnableMongoRepositories(basePackages = {"com.api.starwars.planets.repositories"})
@RefreshScope
public class MongoConfiguration {

    @Value("${mongo.readPreferenceTags:}")
    private String readPreferenceTags;

    @Value("${mongo.username:}")
    private String username;

    @Value("${mongo.password:}")
    private String password;

    @Value("${mongo.host}")
    private String host;

    @Value("${mongo.port:}")
    private String port;

    @Bean
    @RefreshScope
    public MongoClient client() {
        log.warn("Refreshing MongoClient");

        String completeUri = MessageFormat.format("mongodb://{0}:{1}@{2}:{3}", username, password, host, port);

        final MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(completeUri)).build();
        return MongoClients.create(settings, SpringDataMongoDB.driverInformation());
    }

    @Bean
    @RefreshScope
    public MongoDatabaseFactorySupport<?> databaseFactory(MongoClient client, MongoProperties properties) {
        log.warn("Refreshing MongoDatabaseFactory");
        return new SimpleMongoClientDatabaseFactory(client, properties.getMongoClientDatabase());
    }

    @Bean(name = "primaryMongoReadNode")
    @RefreshScope
    public MongoTemplate primaryMongoReadNode(MongoDatabaseFactory mongoDatabaseFactory) {
        log.warn("Refreshing PrimaryMongoReadNode");

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory);
        mongoTemplate.setReadPreference(primary());
        return mongoTemplate;
    }

    @Bean(name = "mongoTemplate")
    @RefreshScope
    public MongoTemplate mongoTemplate(MongoDatabaseFactory databaseFactory) {
        log.warn("Refresh MongoTemplate");

        MongoTemplate template = new MongoTemplate(databaseFactory);
        template.setReadPreference(getReadPreferenceConfig());

        return template;
    }

    private ReadPreference getReadPreferenceConfig() {
        TagSet tags = getReadPreferenceTags();
        return tags == null
                ? secondaryPreferred()
                : secondaryPreferred(tags);
    }

    private TagSet getReadPreferenceTags() {
        if (StringUtils.isBlank(readPreferenceTags)) {
            return null;
        }

        String[] readPreferenceTags = this.readPreferenceTags.split(",");

        List<Tag> tags = Arrays.stream(readPreferenceTags)
                .map(tag -> tag.split("#"))
                .map(splittedTag -> new Tag(splittedTag[0], splittedTag[1]))
                .collect(Collectors.toList());

        return new TagSet(tags);
    }
}
