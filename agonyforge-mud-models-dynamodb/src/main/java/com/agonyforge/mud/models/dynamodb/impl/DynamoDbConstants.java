package com.agonyforge.mud.models.dynamodb.impl;

import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class DynamoDbConstants {
    public static final DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final String KEY_MODIFIED = "lastModified";
}
