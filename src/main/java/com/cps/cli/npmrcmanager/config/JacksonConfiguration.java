package com.cps.cli.npmrcmanager.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = buildObjectMapperBase();

        // offset date time serialization / deserialization
        SimpleModule odtModule = new SimpleModule();

        JsonSerializer<OffsetDateTime> odtSerializer = new JsonSerializer<>() {
            @Override
            public void serialize(OffsetDateTime odt, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                String formatted = odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of(ZoneOffset.UTC.getId())));
                jgen.writeString(formatted);
            }
        };
        JsonDeserializer<OffsetDateTime> odtDeserializer = new JsonDeserializer<>() {
            @Override
            public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return OffsetDateTime.parse(jsonParser.getValueAsString());
            }
        };

        odtModule.addSerializer(OffsetDateTime.class, odtSerializer);
        odtModule.addDeserializer(OffsetDateTime.class, odtDeserializer);
        objectMapper.registerModule(odtModule);

        return objectMapper;
    }

    // --

    private ObjectMapper buildObjectMapperBase() {
        ObjectMapper objectMapper = new ObjectMapper();

        // pretty printer config
        DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);

        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentObjectsWith(indenter);
        pp.indentArraysWith(indenter);

        objectMapper.setDefaultPrettyPrinter(pp);

        // deserialization config
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig()
            .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        objectMapper.setConfig(deserializationConfig);

        return objectMapper;
    }
}
