/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/4/6
 */
public class ResponseEntity {

    //响应的数据
    private Object body;

    //自定义响应头部信息
    private Map<String, String> headers;

    //默认http状态
    private HttpStatus httpStatus = HttpStatus.OK;


    public ResponseEntity() {
    }

    public ResponseEntity(Object body) {
        this.body = body;
    }

    public ResponseEntity(Map<String, String> headers, HttpStatus httpStatus) {
        this.headers = headers;
        this.httpStatus = httpStatus;
    }

    public ResponseEntity(Object body, Map<String, String> headers, HttpStatus httpStatus) {
        this.body = body;
        this.headers = headers;
        this.httpStatus = httpStatus;
    }

    public ResponseEntity body(Object body) {
        this.body = body;
        return this;
    }

    public ResponseEntity header(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    public ResponseEntity status(int status) {
        for (HttpStatus httpStatus : HttpStatus.values()) {
            if (Objects.equals(httpStatus.value(), status)) {
                this.httpStatus = httpStatus;
                break;
            }
        }
        return this;
    }


    public ResponseEntity status(HttpStatus status) {
        this.httpStatus = status;
        return this;
    }


    public <T> T getBody() {
        return (T) body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }


    public static ResponseEntity ok() {
        ResponseEntity responseEntity = new ResponseEntity();
        return responseEntity.status(HttpStatus.OK);
    }


}
