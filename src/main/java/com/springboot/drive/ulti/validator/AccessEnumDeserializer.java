package com.springboot.drive.ulti.validator;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.error.InValidAccessEnumTypeException;
import lombok.SneakyThrows;

import java.io.IOException;

public class AccessEnumDeserializer extends JsonDeserializer<AccessEnum> {

    @SneakyThrows
    @Override
    public AccessEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase(); // Chuyển đổi giá trị thành chữ hoa
        switch (value) {
            case "CREATE":
                return AccessEnum.CREATE;
            case "UPDATE":
                return AccessEnum.UPDATE;
            case "DELETE":
                return AccessEnum.DELETE;
            case "VIEW":
                return AccessEnum.VIEW;
            case "SOFT_DELETE":
                return AccessEnum.SOFT_DELETE;
            default:
                throw new InValidAccessEnumTypeException("Enum type not supported. Try again!!!");
        }
    }
}