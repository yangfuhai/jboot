/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.utils;

import io.jboot.components.http.JbootHttpManager;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能更强大的http工具类
 * 1、支持 http get post 操作
 * 2、支持文件下载 和 文件上传
 * 3、支持自定义https文件证书（可以用在类似调用支付相关接口等）
 */
public class HttpUtil {

    /**
     * http get操作
     *
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        return httpGet(url, null);
    }

    /**
     * http get操作
     *
     * @param url
     * @param paras
     * @return
     */
    public static String httpGet(String url, Map<String, Object> paras) {
        return httpGet(url, paras, null);
    }


    /**
     * 发送可以配置headers 的 http请求
     *
     * @param url
     * @param paras
     * @param headers
     * @return
     */
    public static String httpGet(String url, Map<String, Object> paras, Map<String, String> headers) {
        JbootHttpRequest request = JbootHttpRequest.create(url, paras, JbootHttpRequest.METHOD_GET);
        request.addHeaders(headers);
        JbootHttpResponse response = handle(request);
        return response.isError() ? null : response.getContent();
    }

    /**
     * http post 操作
     *
     * @param url
     * @return
     */
    public static String httpPost(String url) {
        return httpPost(url, null, null, null);
    }

    /**
     * http post 操作
     *
     * @param url
     * @return
     */
    public static String httpPost(String url, String postData) {
        return httpPost(url, null, null, postData);
    }


    /**
     * Http post 操作
     *
     * @param url
     * @param paras post的参数，可以是文件
     * @return
     */
    public static String httpPost(String url, Map<String, Object> paras) {
        return httpPost(url, paras, null, null);
    }

    /**
     * Http post 操作
     *
     * @param url
     * @param paras
     * @param postData
     * @return
     */
    public static String httpPost(String url, Map<String, Object> paras, String postData) {
        return httpPost(url, paras, null, postData);
    }

    /**
     * Http post 操作
     *
     * @param url
     * @param paras
     * @param headers
     * @return
     */
    public static String httpPost(String url, Map<String, Object> paras, Map<String, String> headers) {
        return httpPost(url, paras, headers, null);
    }


    /**
     * Http post 操作
     *
     * @param url
     * @param paras
     * @param headers
     * @param postData
     * @return
     */
    public static String httpPost(String url, Map<String, Object> paras, Map<String, String> headers, String postData) {
        JbootHttpRequest request = JbootHttpRequest.create(url, paras, JbootHttpRequest.METHOD_POST);
        request.setBodyContent(postData);
        request.addHeaders(headers);
        JbootHttpResponse response = handle(request);
        return response.isError() ? null : response.getContent();
    }


    /**
     * 下载文件
     *
     * @param url
     * @param toFile
     * @return
     */
    public static boolean download(String url, File toFile) {
        return download(url, null, null, toFile);
    }


    /**
     * 下载文件
     *
     * @param url
     * @param paras
     * @param toFile
     * @return
     */
    public static boolean download(String url, Map<String, Object> paras, File toFile) {
        return download(url, paras, null, toFile);
    }


    /**
     * 下载文件
     *
     * @param url
     * @param paras
     * @param headers
     * @param toFile
     * @return
     */
    public static boolean download(String url, Map<String, Object> paras, Map<String, String> headers, File toFile) {
        JbootHttpRequest request = JbootHttpRequest.create(url, paras, JbootHttpRequest.METHOD_GET);
        request.setDownloadFile(toFile);
        request.addHeaders(headers);
        return handle(request).getError() == null;
    }

    /**
     * 上传文件
     *
     * @param url
     * @return
     */
    public static String upload(String url, File file) {
        return upload(url, null, null, file);
    }


    /**
     * 上传文件
     *
     * @param url
     * @param paras
     * @param file
     * @return
     */
    public static String upload(String url, Map<String, Object> paras, File file) {
        return upload(url, paras, null, file);
    }


    /**
     * 上传文件
     *
     * @param url
     * @param paras
     * @param headers
     * @param file
     * @return
     */
    public static String upload(String url, Map<String, Object> paras, Map<String, String> headers, File file) {
        Map<String, Object> newParas = new HashMap();
        if (paras != null) {
            newParas.putAll(paras);
        }

        newParas.put("uploadFile", file);

        JbootHttpRequest request = JbootHttpRequest.create(url, newParas, JbootHttpRequest.METHOD_POST);
        request.addHeaders(headers);
        JbootHttpResponse response = handle(request);
        return response.isError() ? null : response.getContent();
    }


    public static JbootHttpResponse handle(JbootHttpRequest request) {
        return JbootHttpManager.me().getJbootHttp().handle(request);
    }


}
