package io.jboot.apidoc;

public enum HttpMethod {

    ALL("*"), GET("GET"), POST("POST"), PUT("PUT"), PATCH("PATCH"), DELETE("DELETE");

    private String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
