package com.lion.codecatcherbe.infra.mongoDB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class Config extends AbstractMongoClientConfiguration {

    @Value("${data.mongodb.uri}")
    private String uri;
    @Value("${data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString(uri);
        final MongoClientSettings.Builder mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToConnectionPoolSettings(
                builder -> builder.applySettings(connectionPoolSettings()));
        return MongoClients.create(mongoClientSettings.build());
    }

    private ConnectionPoolSettings connectionPoolSettings() {
        return ConnectionPoolSettings.builder()
            .maxSize(50)
            .maxWaitTime(20, TimeUnit.SECONDS)
            .maxConnectionIdleTime(20, TimeUnit.SECONDS)
            .maxConnectionLifeTime(60, TimeUnit.SECONDS).build();
    }
}