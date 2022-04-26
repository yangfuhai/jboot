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
package io.jboot.web.converter;

import com.jfinal.core.converter.IConverter;
import com.jfinal.core.converter.TypeConverter;
import io.jboot.utils.StrUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 针对 数组 类型实现 IConverter 接口
 */
public class ArrayConverters {

    private static String array_separator = ",";

    public static String getArraySeparator() {
        return array_separator;
    }

    public static void setArraySeparator(String arraySeparator) {
        ArrayConverters.array_separator = arraySeparator;
    }

    public static class IntArrayConverter implements IConverter<int[]> {
        @Override
        public int[] convert(String s) {
            String[] strings = s.split(array_separator);
            int[] ret = new int[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? 0 : Integer.parseInt(str);
            }
            return ret;
        }
    }


    public static class Int1ArrayConverter implements IConverter<Integer[]> {
        @Override
        public Integer[] convert(String s) {
            String[] strings = s.split(array_separator);
            Integer[] ret = new Integer[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? null : Integer.parseInt(str);
            }
            return ret;
        }
    }


    public static class LongArrayConverter implements IConverter<long[]> {
        @Override
        public long[] convert(String s) {
            String[] strings = s.split(array_separator);
            long[] ret = new long[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? 0 : Long.parseLong(str);
            }
            return ret;
        }
    }


    public static class Long1ArrayConverter implements IConverter<Long[]> {
        @Override
        public Long[] convert(String s) {
            String[] strings = s.split(array_separator);
            Long[] ret = new Long[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? null : Long.parseLong(str);
            }
            return ret;
        }
    }


    public static class FloatArrayConverter implements IConverter<float[]> {
        @Override
        public float[] convert(String s) {
            String[] strings = s.split(array_separator);
            float[] ret = new float[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? 0 : Float.parseFloat(str);
            }
            return ret;
        }
    }


    public static class Float1ArrayConverter implements IConverter<Float[]> {
        @Override
        public Float[] convert(String s) {
            String[] strings = s.split(array_separator);
            Float[] ret = new Float[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? null : Float.parseFloat(str);
            }
            return ret;
        }
    }

    public static class DoubleArrayConverter implements IConverter<double[]> {
        @Override
        public double[] convert(String s) {
            String[] strings = s.split(array_separator);
            double[] ret = new double[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? 0 : Double.parseDouble(str);
            }
            return ret;
        }
    }


    public static class Double1ArrayConverter implements IConverter<Double[]> {
        @Override
        public Double[] convert(String s) {
            String[] strings = s.split(array_separator);
            Double[] ret = new Double[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? null : Double.parseDouble(str);
            }
            return ret;
        }
    }


    public static class BigIntegerArrayConverter implements IConverter<BigInteger[]> {
        @Override
        public BigInteger[] convert(String s) {
            String[] strings = s.split(array_separator);
            BigInteger[] ret = new BigInteger[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? null : new BigInteger(str);
            }
            return ret;
        }
    }


    public static class BigDecimalArrayConverter implements IConverter<BigDecimal[]> {
        @Override
        public BigDecimal[] convert(String s) {
            String[] strings = s.split(array_separator);
            BigDecimal[] ret = new BigDecimal[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                ret[i] = StrUtil.isBlank(str) ? null : new BigDecimal(str);
            }
            return ret;
        }
    }


    public static void init() {
        TypeConverter.me().regist(int[].class, new IntArrayConverter());
        TypeConverter.me().regist(Integer[].class, new Int1ArrayConverter());

        TypeConverter.me().regist(long[].class, new LongArrayConverter());
        TypeConverter.me().regist(Long[].class, new Long1ArrayConverter());

        TypeConverter.me().regist(float[].class, new FloatArrayConverter());
        TypeConverter.me().regist(Float[].class, new Float1ArrayConverter());

        TypeConverter.me().regist(double[].class, new DoubleArrayConverter());
        TypeConverter.me().regist(Double[].class, new Double1ArrayConverter());

        TypeConverter.me().regist(BigInteger[].class, new BigIntegerArrayConverter());
        TypeConverter.me().regist(BigDecimal[].class, new BigDecimalArrayConverter());
    }

}
