package com.revature.p1.utils;

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
