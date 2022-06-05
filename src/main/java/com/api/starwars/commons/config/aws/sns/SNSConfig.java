package com.api.starwars.commons.config.aws.sns;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SNSConfig {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String sqsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String sqsSecretKey;

    @Value("${cloud.aws.sns.endpoint:defaultEndPoint}")
    private String snsEndPoint;

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNSAsync amazonSNS) {
        return new NotificationMessagingTemplate(amazonSNS);
    }

    @Bean
    @Primary
    public AmazonSNSAsync amazonSNSAsync() {
        if (snsEndPoint.equals("defaultEndPoint")) {
            return AmazonSNSAsyncClientBuilder.standard().withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
                    .build();
        }
        return AmazonSNSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(snsEndPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
                .build();
    }

}
