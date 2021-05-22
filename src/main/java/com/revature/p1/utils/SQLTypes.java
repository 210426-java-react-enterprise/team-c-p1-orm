package com.revature.p1.utils;

import java.lang.reflect.Field;

public enum SQLTypes { //should this exist?

    INT("int"),
    DOUBLE("double"),
    LONG("bigint"),
    STRING("varchar"),
    BOOLEAN("bit"),
    CHARACTER("char(1)"),
    BIGDECIMAL("numeric"),
    LOCALDATETIME("timestamp");

    private final String value;

    SQLTypes(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

//    private String getSQLType(Field field) {
//        switch (field.getType().getSimpleName()) {
//            case "int":
//                return "int";
//            case "double":
//                return "double";
//            case "long":
//                return "bigint";
//            case "String":
//                return "varchar";
//            case "boolean":
//                return "bit";
//            case "character":
//                return "char(1)";
//            case "BigDecimal":
//                return "numeric";
//            case "LocalDateTime":
//                return "timestamp";
//            default:
//                return null;
//        }
//    }
