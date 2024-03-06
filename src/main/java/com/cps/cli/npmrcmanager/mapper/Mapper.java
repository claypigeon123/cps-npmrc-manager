package com.cps.cli.npmrcmanager.mapper;

public interface Mapper {
    <T> T readValue(String value, Class<T> clazz) throws IllegalStateException;

    String writeValueAsString(Object value);
}
