package com.cps.cli.npmrcmanager.mapper.impl;

import com.cps.cli.npmrcmanager.mapper.Mapper;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

@Component
public class YmlMapper implements Mapper {

    @Override
    public <T> T readValue(String value, Class<T> clazz) throws IllegalStateException {
        try {
            return yaml().loadAs(value, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse yml content", e);
        }
    }

    @Override
    public String writeValueAsString(Object value) {
        return yaml().dumpAsMap(value);
    }

    private Yaml yaml() {
        Yaml yaml = new Yaml();
        yaml.setBeanAccess(BeanAccess.FIELD);

        return yaml;
    }
}
