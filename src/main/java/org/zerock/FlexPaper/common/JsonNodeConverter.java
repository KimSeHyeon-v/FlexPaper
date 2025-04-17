package org.zerock.FlexPaper.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ JsonNode → String 변환 (DB 저장)
    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        try {
            return attribute != null ? objectMapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JsonNode to String", e);
        }
    }

    // ✅ String → JsonNode 변환 (DB 조회)
    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readTree(dbData) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting String to JsonNode", e);
        }
    }
}
