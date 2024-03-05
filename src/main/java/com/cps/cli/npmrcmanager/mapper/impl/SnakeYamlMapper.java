package com.cps.cli.npmrcmanager.mapper.impl;

import com.cps.cli.npmrcmanager.mapper.Mapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
@RequiredArgsConstructor
public class SnakeYamlMapper implements Mapper {

    @NonNull
    private final Yaml yaml;

    @Override
    public <T> T readValue(String value, Class<T> clazz) throws IllegalStateException {
        try {
            return yaml.loadAs(value, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse yml content", e);
        }
    }

    @Override
    public String writeValueAsString(Object value) throws IllegalStateException {
        try {
            return yaml.dump(value);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write yml", e);
        }
    }
}
