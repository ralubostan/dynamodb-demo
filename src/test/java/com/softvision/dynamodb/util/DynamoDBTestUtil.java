package com.softvision.dynamodb.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.ArrayList;
import java.util.List;

public final class DynamoDBTestUtil {

    private DynamoDBTestUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated!");
    }

    public static CreateTableResult createTable(AmazonDynamoDB amazonDynamoDB, String tableName, String hashKeyName, String rangeKeyName) {
        ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);

        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S));
        if (rangeKeyName != null) {
            attributeDefinitions.add(new AttributeDefinition(rangeKeyName, ScalarAttributeType.S));
        }
        attributeDefinitions.add(new AttributeDefinition("genre", ScalarAttributeType.S));

        List<KeySchemaElement> ks = new ArrayList<>();
        ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH));
        if (rangeKeyName != null) {
            ks.add(new KeySchemaElement(rangeKeyName, KeyType.RANGE));
        }

        List<KeySchemaElement> indexKeys = new ArrayList<>();
        indexKeys.add(new KeySchemaElement("genre", KeyType.HASH));
        indexKeys.add(new KeySchemaElement("id", KeyType.RANGE));
        Projection projection = new Projection()
                .withProjectionType(ProjectionType.ALL);

        GlobalSecondaryIndex globalSecondaryIndex = new GlobalSecondaryIndex()
                .withIndexName("genrePlaylists")
                .withKeySchema(indexKeys)
                .withProvisionedThroughput(provisionedthroughput)
                .withProjection(projection);

        CreateTableRequest request =
                new CreateTableRequest()
                        .withTableName(tableName)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withKeySchema(ks)
                        .withGlobalSecondaryIndexes(globalSecondaryIndex)
                        .withProvisionedThroughput(provisionedthroughput);

        return amazonDynamoDB.createTable(request);
    }

    public static DeleteTableResult deleteTable(AmazonDynamoDB amazonDynamoDB, String tableName) {
        return amazonDynamoDB.deleteTable(tableName);
    }

    public static AmazonDynamoDB createEmbeddedDatabase() {
        System.getProperties().setProperty("sqlite4java.library.path", "build/libs/native");
        return DynamoDBEmbedded.create().amazonDynamoDB();
    }
}
