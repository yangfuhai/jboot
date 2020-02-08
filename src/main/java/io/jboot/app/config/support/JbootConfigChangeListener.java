package io.jboot.app.config.support;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/8
 */
public interface JbootConfigChangeListener<T> {

    public void onChange(T newObj,T oldObj);

}
