package com.api.starwars.commons.config.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.ReadPreference.primary;
import static com.mongodb.ReadPreference.secondaryPreferred;

@Slf4j
@Configuration
@RefreshScope
public class MongoConfiguration {

    @Value("${spring.data.mongodb.readPreferenceTags:}")
    private String readPreferenceTags;

    @Value("${spring.data.mongodb.username:}")
    private String username;

    @Value("${spring.data.mongodb.password:}")
    private String password;

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port:}")
    private String port;

    @Value("${spring.data.mongodb.database:}")
    private String databaseName;

    @Value("${spring.data.mongodb.authSource:}")
    private String authSource;

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    @RefreshScope
    public MongoClient client() {
        log.info("Refreshing MongoClient");
        log.info("host: " + host);

        String completeUri;
        if (!StringUtils.isBlank(profile) && StringUtils.isBlank(username)) {
            completeUri = MessageFormat.format("mongodb://{0}:{1}/{2}?authSource={3}", host, port, databaseName,
                    authSource);
        } else {
            completeUri = MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/{4}?authSource={5}&authMechanism=SCRAM-SHA-1", username, password, host,
                    port, databaseName, authSource);
        }

        final MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(completeUri)).build();
        return MongoClients.create(settings, SpringDataMongoDB.driverInformation());
    }

    @Bean
    @RefreshScope
    public MongoDatabaseFactorySupport<?> databaseFactory(MongoClient client, MongoProperties properties) {
        log.info("Refreshing MongoDatabaseFactory");
        return new SimpleMongoClientDatabaseFactory(client, properties.getMongoClientDatabase());
    }

    @Bean(name = "primaryMongoReadNode")
    @RefreshScope
    public MongoTemplate primaryMongoReadNode(MongoDatabaseFactory mongoDatabaseFactory) {
        log.info("Refreshing PrimaryMongoReadNode");

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory);
        mongoTemplate.setReadPreference(primary());
        return mongoTemplate;
    }

    @Bean(name = "mongoTemplate")
    @RefreshScope
    public MongoTemplate mongoTemplate(MongoDatabaseFactory databaseFactory) {
        log.info("Refresh MongoTemplate");

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
        if (StringUtils.isEmpty(readPreferenceTags)) {
            return null;
        }

        String[] readPreferenceTags = this.readPreferenceTags.split(",");

        List<Tag> tags = Arrays.stream(readPreferenceTags)
                .map(tag -> tag.split("#"))
                .map(splattedTag -> new Tag(splattedTag[0], splattedTag[1]))
                .toList();

        return new TagSet(tags);
    }
}
