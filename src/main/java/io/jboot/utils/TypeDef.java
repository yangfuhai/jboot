package io.jboot.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class TypeDef<T> {

    public static final TypeDef LIST_STRING = new TypeDef<List<String>>() {};
    public static final TypeDef LIST_INTEGER = new TypeDef<List<Integer>>() {};
    public static final TypeDef LIST_BIGINTEGER = new TypeDef<List<BigInteger>>() {};
    public static final TypeDef LIST_BIGDECIMAL = new TypeDef<List<BigDecimal>>() {};

    public static final TypeDef MAP_STRING = new TypeDef<Map<String, String>>() {};
    public static final TypeDef MAP_INTEGER = new TypeDef<Map<String, Integer>>() {};
    public static final TypeDef MAP_BIGINTEGER = new TypeDef<Map<String, BigInteger>>() {};
    public static final TypeDef MAP_BIGDECIMAL = new TypeDef<Map<String, BigDecimal>>() {};


    protected Type type;
    protected Class<?> defClass;

    protected TypeDef() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass == TypeDef.class) {
            throw new IllegalArgumentException("Must appoint generic class in TypeDef.");
        }

        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            ParameterizedType paraType = (ParameterizedType) type;
            this.type = paraType;
            this.defClass = (Class<?>) paraType.getRawType();
        } else {
            this.type = type;
            this.defClass = (Class<?>) type;
        }
    }


    public Type getType() {
        return type;
    }


    public Class<?> getDefClass() {
        return defClass;
    }

}
