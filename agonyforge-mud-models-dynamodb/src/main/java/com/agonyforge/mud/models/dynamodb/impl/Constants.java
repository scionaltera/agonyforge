package com.agonyforge.mud.models.dynamodb.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class Constants {
    public static final DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final String DYNAMO_TABLE_NAME = System.getenv("DYNAMO_TABLE_NAME");
    public static final String DYNAMO_GSI1_NAME = System.getenv("DYNAMO_GSI1_NAME");
    public static final String DYNAMO_GSI2_NAME = System.getenv("DYNAMO_GSI2_NAME");

    public static final String KEY_MODIFIED = "lastModified";

    public static final String SORT_DATA = "DATA";
    public static final String SORT_SESSION = "SESSION#";

    public static final String TYPE_USER = "USER";
    public static final String TYPE_SESSION = "SESSION";

    public static final String DB_USER = "USER#";
}
