package sharding;

import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package sharding
 */
@RequestMapping("/sharding")
public class ShardingController extends JbootController {

    static UserModel DAO = new UserModel().dao();


    public void index() {

        String id = StringUtils.uuid();

        UserModel user = new UserModel();
        user.setId(id);
        user.setName("Michael yang");

        //增加
        user.save();

        System.out.println("保存成功！");

        user.setName("fuhai yang");

//        DAO.findFirstByColumn("name","fuhaiyang");

        //更新
        user.update();

        //查询
        UserModel findModel = DAO.findById(id);
        renderJson(findModel);

    }



}
