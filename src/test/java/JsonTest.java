import com.jfinal.json.FastJson;
import com.jfinal.json.JFinalJson;
import org.junit.Test;
import service.MyUser;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JsonTest {

    @Test
    public void testJFinalJson() {

        MyUser user = new MyUser();
        user.setId(100);
        user.setName("michael");

        String json = JFinalJson.getJson().toJson(user);
        System.out.println(json);


        MyUser user1 = new MyUser();
        user1.setId(100);
        user1.setName(null);


        String json1 = JFinalJson.getJson().toJson(user1);
        System.out.println(json1);

    }

    @Test
    public void testFastJson() {

        MyUser user = new MyUser();
        user.setId(100);
        user.setName("michael");

        String json = FastJson.getJson().toJson(user);
        System.out.println(json);


        MyUser user1 = new MyUser();
        user1.setId(100);
        user1.setName(null);

        String json1 = FastJson.getJson().toJson(user1);
        System.out.println(json1);
    }
}
