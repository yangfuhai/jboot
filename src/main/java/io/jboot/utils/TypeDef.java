package io.jboot.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class TypeDef<T> {

    public static final TypeDef list_string = new TypeDef<List<String>>() {
    };
    public static final TypeDef list_integer = new TypeDef<List<Integer>>() {
    };
    public static final TypeDef list_biginteger = new TypeDef<List<BigInteger>>() {
    };

    public static final TypeDef map_string = new TypeDef<Map<String, String>>() {
    };
    public static final TypeDef map_integer = new TypeDef<Map<String, Integer>>() {
    };
    public static final TypeDef map_biginteger = new TypeDef<Map<String, BigInteger>>() {
    };


    protected Type type;
    protected Class defClass;

    protected TypeDef() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass == TypeDef.class) {
            throw new IllegalArgumentException("Must appoint generic class in TypeDef.");
        }

        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            ParameterizedType paraType = (ParameterizedType) type;
            this.defClass = (Class) paraType.getRawType();
            this.type = paraType;
        } else {
            this.type = type;
            this.defClass = (Class) type;
        }
    }


    public Type getType() {
        return type;
    }

    public Class getDefClass() {
        return defClass;
    }

}
