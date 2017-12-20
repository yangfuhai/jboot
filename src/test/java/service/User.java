package service;

import io.jboot.db.model.JbootModel;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package service
 */
public class User extends JbootModel<User> {


    public User() {
    }

    public User(int id, String name) {
        setId(id);
        setName(name);
    }

    public int getId() {
        return get("id");
    }

    public void setId(int id) {
        set("id", id);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

   
}
