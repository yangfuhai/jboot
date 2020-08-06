package io.jboot.test.db.simple;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.web.controller.JbootController;

import java.util.List;

public class SuperDbController extends JbootController {




    public void index() {
        List<Record> records = Db.find("select * from `user`");
        renderJson(records);
    }

}
