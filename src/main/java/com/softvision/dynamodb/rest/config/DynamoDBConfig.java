package com.softvision.dynamodb.rest.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING;

@Configuration
@ComponentScan
@EnableDynamoDBRepositories
public class DynamoDBConfig {

    @Value("${dynamodb.endpoint}")
    private String awsDynamoDBEndpoint;

    @Value("${dynamodb.region}")
    private String awsRegion;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsDynamoDBEndpoint, awsRegion))
                .build();
    }

    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDB(amazonDynamoDB);
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB, DynamoDBMapperConfig dynamoDBMapperConfig) {
        return new DynamoDBMapper(amazonDynamoDB, dynamoDBMapperConfig);
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(DynamoDBMapperConfig.TableNameResolver tableNameResolver) {
        return new DynamoDBMapperConfig.Builder()
                .withTableNameResolver(tableNameResolver)
                .withPaginationLoadingStrategy(EAGER_LOADING)
                .build();
    }

    @Bean
    public DynamoDBMapperConfig.TableNameResolver tableNameResolver() {
        return (clazz, config) -> {
            Optional<String> tableName = Arrays.stream(clazz.getDeclaredAnnotations())
                    .filter(annotation -> annotation.annotationType().equals(DynamoDBTable.class))
                    .map(annotation -> ((DynamoDBTable) annotation).tableName())
                    .findFirst();

            return tableName.orElse(null);
        };
    }
}
