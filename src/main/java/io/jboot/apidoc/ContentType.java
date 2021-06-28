package io.jboot.apidoc;

public enum ContentType {

    DEFAULT("application/x-www-form-urlencoded"), JSON("application/json");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
