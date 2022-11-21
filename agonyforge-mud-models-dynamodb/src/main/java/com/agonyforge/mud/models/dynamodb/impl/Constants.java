package com.agonyforge.mud.models.dynamodb.impl;

import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class Constants {
    public static final DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final String KEY_MODIFIED = "lastModified";

    public static final String SORT_DATA = "DATA";
    public static final String SORT_SESSION = "SESSION#";
    public static final String SORT_INSTANCE = "INSTANCE#";

    public static final String TYPE_USER = "USER";
    public static final String TYPE_SESSION = "SESSION";
    public static final String TYPE_PC = "PC";

    public static final String DB_USER = "USER#";
    public static final String DB_PC = "PC#";
}