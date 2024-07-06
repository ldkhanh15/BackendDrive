package com.springboot.drive.ulti.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springboot.drive.ulti.validator.AccessEnumDeserializer;

@JsonDeserialize(using = AccessEnumDeserializer.class)
public enum AccessEnum {
    CREATE, UPDATE, DELETE,VIEW,SOFT_DELETE
}
