package com.jobassistant.jabackend.core.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UuidUtils {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
