import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.event.JbootEventListener;
import io.jboot.utils.ArrayUtils;

import java.util.Arrays;

public class MainTest {
    static Class[] default_excludes = new Class[]{JbootEventListener.class, JbootmqMessageListener.class};

    public static void main(String[] args) {


        Class[] excludes =  ArrayUtils.concat(default_excludes, new Class[]{MainTest.class});

        System.out.println(Arrays.toString(excludes));
    }


}


