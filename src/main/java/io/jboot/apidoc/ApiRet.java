package io.jboot.apidoc;

import com.jfinal.kit.Ret;

public class ApiRet<T> {

    private static final String STATE_OK = "ok";
    private static final String STATE_FAIL = "fail";

    //状态，使用 string 是为了兼容 JFinal 的 Ret，目前很多前端是通过 state 来进行判断的
    private String state;

    //错误码
    private Integer errorCode;

    //对本次状态码的描述
    private String message;

    //数据
    private T data;

    public static ApiRet by(Ret ret) {
        ApiRet apiRet = new ApiRet();
        apiRet.state = ret.isOk() ? STATE_OK : (ret.isFail() ? STATE_FAIL : null);
        for (Object key : ret.keySet()) {
            if ("state".equals(key)) {
                continue;
            }
            apiRet.data = ret.get(key);
        }
        return apiRet;
    }

    public static ApiRet ok() {
        ApiRet apiRet = new ApiRet();
        apiRet.state = STATE_OK;
        return apiRet;
    }

    public static ApiRet ok(Object data) {
        ApiRet apiRet = new ApiRet();
        apiRet.state = STATE_OK;
        apiRet.data = data;
        return apiRet;
    }

    public static ApiRet fail() {
        ApiRet apiRet = new ApiRet();
        apiRet.state = STATE_FAIL;
        return apiRet;
    }


    public static ApiRet fail(int errorCode) {
        ApiRet apiRet = new ApiRet();
        apiRet.state = STATE_FAIL;
        apiRet.errorCode = errorCode;
        return apiRet;
    }

    public static ApiRet fail(String message) {
        ApiRet apiRet = new ApiRet();
        apiRet.state = STATE_FAIL;
        apiRet.message = message;
        return apiRet;
    }


    public static ApiRet fail(int errorCode, String message) {
        ApiRet apiRet = new ApiRet();
        apiRet.state = STATE_FAIL;
        apiRet.errorCode = errorCode;
        apiRet.message = message;
        return apiRet;
    }


    public ApiRet<T> data(T data) {
        this.data = data;
        return this;
    }

    public ApiRet<T> message(String message) {
        this.message = message;
        return this;
    }

    public ApiRet<T> code(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ApiRet<T> state(boolean ok) {
        this.state = ok ? STATE_OK : STATE_FAIL;
        return this;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
