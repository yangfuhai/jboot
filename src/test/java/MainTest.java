import javafx.beans.NamedArg;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MainTest {


    public static void main(@Named("a") String[] args) {

        System.out.println(MainTest.class);

        Method method = MainTest.class.getDeclaredMethods()[1];
        System.out.println(method.getName());
//        System.out.println(method.getParameterTypes()[0]);
//        System.out.println(method.getParameterAnnotations()[0][0]);


        Annotation[][] annotationss = method.getParameterAnnotations();

        for (int i = 0; i < annotationss.length; i++) {
            for (int j = 0; j < annotationss[i].length; j++) {
                System.out.println(" i:" + i + " j:" + j + "    " + (annotationss[i][j].annotationType() == Named.class));
            }
        }
    }


    public void test(String aa, @Named("bbb") @NamedArg("bbb") String bbb, @Named("ccc") String ccc) {

    }


}


