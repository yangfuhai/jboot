import com.jfinal.template.Engine;
import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.event.JbootEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainTest {
    static Class[] default_excludes = new Class[]{JbootEventListener.class, JbootmqMessageListener.class};

    public static void main(String[] args) {


//        Class[] excludes =  ArrayUtils.concat(default_excludes, new Class[]{MainTest.class});
//
//        System.out.println(Arrays.toString(excludes));

        Map<String,Object> data = new HashMap();


        String text = new Engine("test").getTemplateByString("#(a==null)").renderToString(data);
        System.out.println(text);
    }


}


