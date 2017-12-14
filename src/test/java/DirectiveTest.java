import com.google.inject.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@JFinalDirective("testDirective")
public class DirectiveTest extends JbootDirectiveBase {

    @Inject
    ControllerTest.ServiceInter service;


    @Override
    public void exec(Env env, Scope scope, Writer writer) {
        String abc = service.hello("aabbcc");
        initParams(scope);

        try {
            writer.write("testDirective : " + getParam("c",scope) +"<br />");
        } catch (IOException e) {
            e.printStackTrace();
        }





        System.out.println((HttpServletRequest) getParam(0,scope));
        System.out.println((String) getParam(1,scope));
        System.out.println((String) getParam("c",scope));

        System.out.println(this);


    }


}
