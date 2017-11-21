import com.google.inject.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.io.IOException;

@JFinalDirective("testDirective")
public class DirectiveTest extends JbootDirectiveBase {

    @Inject
    ControllerTest.ServiceInter service;


    @Override
    public void exec(Env env, Scope scope, Writer writer) {
        String abc = service.hello("aabbcc");
        try {
            writer.write("testDirective : " + abc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
