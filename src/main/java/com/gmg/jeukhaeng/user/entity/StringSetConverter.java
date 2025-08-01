package com.gmg.jeukhaeng.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Set을 콤마(,)로 구분된 문자열로 변환하여 DB에 저장하고,
 * DB에서 읽어올 때 다시 Set으로 변환하는 JPA AttributeConverter입니다.
 */
@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return attribute == null ? "" : String.join(",", attribute);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return dbData == null ? new HashSet<>() : new HashSet<>(List.of(dbData.split(",")));
    }
}