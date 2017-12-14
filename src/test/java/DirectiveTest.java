import com.google.inject.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import service.User;

@JFinalDirective("testDirective")
public class DirectiveTest extends JbootDirectiveBase {

    @Inject
    ControllerTest.ServiceInter service;


    @Override
    public void exec(Env env, Scope scope, Writer writer) {

        initParams(scope);

        String d = getParam("d",scope);

        String content = service.hello(d);

        User user = new User();
        user.setName(content);

        scope.set("user",user);
        stat.exec(env,scope,writer);


//
//        System.out.println((HttpServletRequest) getParam(0,scope));
//        System.out.println((String) getParam(1,scope));
//        System.out.println((String) getParam("c",scope));
//        System.out.println((String) getParam("d",scope));
//
//        System.out.println("DirectiveTest:"+this);
//        System.out.println("scope:"+scope);


    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
