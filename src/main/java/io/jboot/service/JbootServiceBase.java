/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.db.model.JbootModel;
import io.jboot.exception.JbootException;
import io.jboot.utils.ClassUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * JbootServiceBase 类
 * Jboot 1.x 的 Service 需要 Join 功能的话，需要实现 JbootServiceJoiner 接口
 */
public class JbootServiceBase<M extends JbootModel<M>>
        extends JbootServiceJoinerImpl
        implements JbootServiceJoiner {


    protected M DAO = null;

    public JbootServiceBase() {
        DAO = initDao();
    }

    /**
     * 初始化 DAO
     * 子类可以复写 自定义自己的DAO
     *
     * @return
     */
    protected M initDao() {
        Type type = ClassUtil.getUsefulClass(getClass()).getGenericSuperclass();
        Class<M> modelClass = (Class<M>) ((ParameterizedType) type).getActualTypeArguments()[0];
        if (modelClass == null) throw new JbootException("can not get model class name in JbootServiceBase");

        //默认不通过AOP构建DAO，提升性能，若特殊需要重写initDao()方法即可
        return ClassUtil.newInstance(modelClass, false);
    }


    public M getDao() {
        return DAO;
    }


    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public M findById(Object id) {
        return DAO.findById(id);
    }


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<M> findAll() {
        return DAO.findAll();
    }


    /**
     * 根据ID 删除model
     *
     * @param id
     * @return
     */
    public boolean deleteById(Object id) {
        return DAO.deleteById(id);
    }


    /**
     * 删除
     *
     * @param model
     * @return
     */
    public boolean delete(M model) {
        return model.delete();
    }


    /**
     * 保存到数据库
     *
     * @param model
     * @return id if success
     */
    public Object save(M model) {
        return model.save() ? model._getIdValue() : null;
    }


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(M model) {
        if (model._getIdValue() == null) {
            return save(model);
        } else if (update(model)) {
            return model._getIdValue();
        }
        return null;
    }

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(M model) {
        return model.update();
    }


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<M> paginate(int page, int pageSize) {
        return DAO.paginate(page, pageSize);
    }


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<M> paginateByColumns(int page, int pageSize, Columns columns) {
        return DAO.paginateByColumns(page, pageSize, columns);
    }


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<M> paginateByColumns(int page, int pageSize, Columns columns, String orderBy) {
        return DAO.paginateByColumns(page, pageSize, columns, orderBy);
    }


    /**
     * 复写 JbootServiceJoinerImpl 的方法
     *
     * @param id
     * @return
     */
    @Override
    protected JbootModel joinById(Object id) {
        return findById(id);
    }
}
