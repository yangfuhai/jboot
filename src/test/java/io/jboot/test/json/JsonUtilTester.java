package io.jboot.test.json;

import io.jboot.utils.JsonUtil;
import io.jboot.utils.TypeDef;

import java.util.List;
import java.util.Set;

public class JsonUtilTester {

    public static void main(String[] args) {
        String json = "{\n" +
                "  \"aaa\": [\n" +
                "    {\n" +
                "      \"id\": \"100\",\n" +
                "      \"age\": 10,\n" +
                "      \"amount\": 99\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 101,\n" +
                "      \"age\": \"20\",\n" +
                "      \"amount\": 999\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 101,\n" +
                "      \"age\": \"30\",\n" +
                "      \"amount\": \"9999\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        System.out.println("----");
        List<MyBean> myBeans0 = JsonUtil.get(json, "aaa", new TypeDef<List<MyBean>>() {
        });

        List<MyBean> myBeans1 = JsonUtil.getList(json, "aaa", MyBean.class);

        Set<MyBean> myBeans2 = JsonUtil.getSet(json, "aaa", MyBean.class);

        System.out.println("-----");
    }
}
