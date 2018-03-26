import com.jfinal.kit.Base64Kit;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.web.JbootWebConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package PACKAGE_NAME
 */
public class EncryptCookieTest {

    public static void main(String args[]){

        String value = EncryptCookieUtils.buildCookieValue("aaaaa",10000);
        System.out.println(value);

        String readValue = EncryptCookieUtils.getFromCookieInfo(JbootWebConfig.DEFAULT_COOKIE_ENCRYPT_KEY, Base64Kit.decodeToStr(value));
        System.out.println(readValue);
    }
}
