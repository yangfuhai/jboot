package io.jboot.app.config;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/8
 */
public interface JbootConfigChangeListener<T> {

    public void onChange(T newObj,T oldObj);

}
