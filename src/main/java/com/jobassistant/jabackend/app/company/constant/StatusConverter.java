package com.jobassistant.jabackend.app.company.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        if (status == null) return null;
        if (status == Status.final_) return "final";
        return status.name();
    }

    @Override
    public Status convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        if ("final".equals(dbValue)) return Status.final_;
        return Status.valueOf(dbValue);
    }
}
