package com.api.starwars.commons.config.aws.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SQSConfig {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String sqsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String sqsSecretKey;

    @Value("${cloud.aws.sqs.endpoint:defaultEndPoint}")
    private String sqsEndPoint;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQSAsync());
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        if (sqsEndPoint.equals("defaultEndPoint")) {
            return AmazonSQSAsyncClientBuilder.standard().withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
                    .build();
        }
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsEndPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
                .build();
    }

}
