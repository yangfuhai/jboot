package io.jboot.test.jfinal;

import com.jfinal.template.Engine;

public class StaticInvoke {

    public static void main(String[] args) {

        Engine engine = new Engine();
        String template = "#(io.jboot.utils.StrUtil::isNumeric('123'))";
        String string = engine.getTemplateByString(template).renderToString(null);
        System.out.println(string);


    }
}
