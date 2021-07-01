package io.jboot.apidoc;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import java.util.Map;

public class ApiRet<T> extends Ret {


    private static final long serialVersionUID = -3021472182023759198L;

    private static final String STATE = "state";
    private static final String STATE_OK = "ok";
    private static final String STATE_FAIL = "fail";

    public ApiRet() {
    }

    public static ApiRet by(Object key, Object value) {
        return new ApiRet().set(key, value);
    }

    public static ApiRet create(Object key, Object value) {
        return new ApiRet().set(key, value);
    }

    public static ApiRet create() {
        return new ApiRet();
    }

    public static ApiRet ok() {
        return new ApiRet().setOk();
    }

    public static ApiRet ok(Object key, Object value) {
        return ok().set(key, value);
    }

    public static ApiRet fail() {
        return new ApiRet().setFail();
    }

    public static ApiRet fail(Object key, Object value) {
        return fail().set(key, value);
    }

    @Override
    public ApiRet<T> setOk() {
        super.put(STATE, STATE_OK);
        return this;
    }

    @Override
    public ApiRet<T> setFail() {
        super.put(STATE, STATE_FAIL);
        return this;
    }


    @Override
    public ApiRet<T> set(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    @Override
    public ApiRet<T> setIfNotBlank(Object key, String value) {
        if (StrKit.notBlank(value)) {
            set(key, value);
        }
        return this;
    }

    @Override
    public ApiRet<T> setIfNotNull(Object key, Object value) {
        if (value != null) {
            set(key, value);
        }
        return this;
    }

    @Override
    public ApiRet<T> set(Map map) {
        super.putAll(map);
        return this;
    }

    @Override
    public ApiRet<T> set(Ret ret) {
        super.putAll(ret);
        return this;
    }


    @Override
    public ApiRet<T> delete(Object key) {
        super.remove(key);
        return this;
    }

}
