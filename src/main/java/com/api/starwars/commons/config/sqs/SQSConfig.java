package com.api.starwars.commons.config.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQSConfig {

    @Value("${aws.sqs.access.key}")
    private String sqsAccessKey;

    @Value("${aws.sqs.secret.key}")
    private String sqsSecretKey;

    @Bean(name = "sqsClient")
    public AmazonSQS getSQSClient() {
        return  AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(this.getAWSCredentials()))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    private AWSCredentials getAWSCredentials() {
        return new BasicAWSCredentials(
                sqsAccessKey,
                sqsSecretKey
        );
    }

}
