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
    public void onRender(Env env, Scope scope, Writer writer) {

        String name = getParam("name", scope);

        String helloName = service.hello(name);

        User user = new User();
        user.setName(helloName);

        scope.set("user", user);


        renderBody(env, scope, writer);

    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
