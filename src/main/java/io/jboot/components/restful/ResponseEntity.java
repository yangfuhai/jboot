package io.jboot.components.restful;

import java.util.HashMap;
import java.util.Map;

public class ResponseEntity<T> {

    //响应的数据
    private T data;

    //自定义响应头部信息
    private Map<String, String> headers = new HashMap<>();

    //默认http状态
    private HttpStatus httpStatus = HttpStatus.OK;

    public T getData() {
        return data;
    }

    public ResponseEntity<T> setData(T data) {
        this.data = data;
        return this;
    }

    public ResponseEntity<T> addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public ResponseEntity<T> addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ResponseEntity<T> setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ResponseEntity() {
    }

    public ResponseEntity(T data, Map<String, String> headers, HttpStatus httpStatus) {
        this.data = data;
        this.headers = headers;
        this.httpStatus = httpStatus;
    }

    public ResponseEntity(Map<String, String> headers, HttpStatus httpStatus) {
        this.headers = headers;
        this.httpStatus = httpStatus;
    }

    public ResponseEntity(T data) {
        this.data = data;
    }

}
