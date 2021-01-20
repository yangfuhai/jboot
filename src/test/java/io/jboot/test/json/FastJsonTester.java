package io.jboot.test.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class FastJsonTester {

    public static void main(String[] args) {
        Map map = new JsonMap();
        map.put("key1", 1);
        map.put("key2", "2");
        map.put("key3", new BigDecimal(3));
        map.put("key4", new BigInteger("4"));
        map.put("key41", 41);
        map.put("key42", new BigInteger("42"));
        map.put("key5", Long.valueOf("5"));
        map.put("key6", 6F);
        map.put("key7", new BigInteger("00000000000077"));

        ParserConfig config22 = new ParserConfig();
//        config22
        config22.addAccept("io.jboot.test.json.JsonMap");
        config22.putDeserializer(String.class, new ObjectDeserializer() {
            @Override
            public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
                return null;
            }

            @Override
            public int getFastMatchToken() {
                return 0;
            }
        });
//        config22.putDeserializer(JwtBigInteger.class, new ObjectDeserializer() {
//
//            @Override
//            public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
////                final JSONLexer lexer = parser.lexer;
////                if (lexer.token() == JSONToken.LITERAL_STRING) {
////                    String val = lexer..numberString();
////                    lexer.nextToken(JSONToken.COMMA);
////
////                    if (val.length() > 65535) {
////                        throw new JSONException("decimal overflow");
////                    }
////
////                    return (T) new BigInteger(val);
////                }
////
////                Object value = parser.parse();
////                return value == null //
////                        ? null //
////                        : (T) TypeUtils.castToBigInteger(value);
//
//                System.out.println(">>>>deserialze");
//
//                Object sv = parser.parse(fieldName);
//                if (sv.toString().endsWith("BI")) {
//                    return (T) new BigInteger(sv.toString().substring(0, sv.toString().length() - 2));
//                }
//                return null;
//            }
//
//            @Override
//            public int getFastMatchToken() {
//                System.out.println(">>>>getFastMatchToken");
//                return JSONToken.LITERAL_INT;
//            }
//        });
        SerializeConfig config1 = new SerializeConfig();
        config1.put(BigInteger.class, ToStringSerializer.instance);
//        config1.put(BigInteger.class, new ObjectSerializer() {
//            @Override
//            public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
//                System.out.println("config1-->>" + object);
//                serializer.out.write((object.toString()+"BI"));
//            }
//        });

        String jsonString = JSON.toJSONString(map, config1, SerializerFeature.WriteClassName);
        System.out.println(jsonString);

        Map newMap = JSON.parseObject(jsonString, HashMap.class, config22);
        System.out.println(newMap);

        Map newMap2 = (Map) JSON.parse(jsonString, config22);
        System.out.println(newMap2);
    }
}
