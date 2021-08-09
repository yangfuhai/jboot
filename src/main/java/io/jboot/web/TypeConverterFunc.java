package io.jboot.web;

import com.jfinal.core.JFinal;
import com.jfinal.core.converter.IConverter;
import com.jfinal.core.converter.TypeConverter;
import com.jfinal.kit.Func;

import java.text.ParseException;

public class TypeConverterFunc implements Func.F21<Class<?>, String, Object> {


    @Override
    public Object call(Class<?> type, String s) {
        if (s == null) {
            return null;
        }

        // mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
        if (type == String.class) {
            return ("".equals(s) ? null : s);    // 用户在表单域中没有输入内容时将提交过来 "", 因为没有输入,所以要转成 null.
        }
        s = s.trim();
        if ("".equals(s)) {    // 前面的 String跳过以后,所有的空字符串全都转成 null,  这是合理的
            return null;
        }

        // 以上两种情况无需转换,直接返回, 注意, 本方法不接受null为 s 参数(经测试永远不可能传来null, 因为无输入传来的也是"")
        //String.class提前处理

        if (type.isEnum()) {
            for (Enum<?> e : ((Class<? extends Enum<?>>) type).getEnumConstants()) {
                if (e.name().equals(s)) {
                    return e;
                }
            }
        }

        // --------
        IConverter<?> converter = TypeConverter.me().getConverterMap().get(type);
        if (converter != null) {
            try {
                return converter.convert(s);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (JFinal.me().getConstants().getDevMode()) {
            throw new RuntimeException("Please add code in " + TypeConverter.class + ". The type can't be converted: " + type.getName());
        } else {
            throw new RuntimeException(type.getName() + " can not be converted, please use other type of attributes in your model!");
        }
    }
}
