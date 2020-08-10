package com.softvision.dynamodb.rest.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Component
public class DynamoDBListConverter implements DynamoDBTypeConverter<String, List<Song>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convert(List<Song> object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (final Exception e) {
            throw new DynamoDBMappingException("Unable to write object to JSON", e);
        }
    }

    @Override
    public List<Song> unconvert(String object) {
        try {
            return OBJECT_MAPPER.readValue(object, new TypeReference<>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (final JsonProcessingException e) {
            throw new DynamoDBMappingException("Unable to read JSON string", e);
        }
    }
}
