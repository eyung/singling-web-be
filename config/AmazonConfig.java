package com.ey.singlingweb.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class AmazonConfig {

    @Bean
    public AmazonS3 s3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
        //"AKIA2XFBBWKQA37PGVHJ",
        //"LcW4IZrVxvkTAE7hdqi3aqX2MqX2"
                "AKIA2XFBBWKQH36AWJ2V",
                "zyv/Uq7YvAWEbC3/R6Py5JfUpA7UcR85M9FifPH1"
        );

        return AmazonS3ClientBuilder
                .standard()
                .withRegion("ca-central-1")
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

}
