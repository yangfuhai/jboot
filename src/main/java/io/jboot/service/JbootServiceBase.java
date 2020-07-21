/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.db.model.JbootModel;
import io.jboot.exception.JbootException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.ObjectFunc;
import io.jboot.utils.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * JbootServiceBase 类
 * Jboot 1.x 的 Service 需要 Join 功能的话，需要实现 JbootServiceJoiner 接口
 */
public class JbootServiceBase<M extends JbootModel<M>>
        extends JbootServiceJoinerImpl
        implements JbootServiceJoiner {

    protected static final int ACTION_ADD = 1;
    protected static final int ACTION_DEL = 2;
    protected static final int ACTION_UPDATE = 3;

    protected JbootModel<M> DAO = null;

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
        Class<M> modelClass = ClassUtil.getGenericClass(getClass());
        if (modelClass == null) {
            throw new JbootException("can not get model class name in " + ClassUtil.getUsefulClass(getClass()));
        }

        //默认不通过AOP构建DAO，提升性能，若特殊需要重写initDao()方法即可
        return ClassUtil.newInstance(modelClass, false);
    }


    public JbootModel getDao() {
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
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public M findFirstByColumns(Columns columns) {
        return findFirstByColumns(columns, null);
    }


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public M findFirstByColumns(Columns columns, String orderBy) {
        return DAO.findFirstByColumns(columns, orderBy);
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
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<M> findListByColumns(Columns columns) {
        return DAO.findListByColumns(columns);
    }


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<M> findListByColumns(Columns columns, String orderBy) {
        return DAO.findListByColumns(columns, orderBy);
    }

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<M> findListByColumns(Columns columns, Integer count) {
        return DAO.findListByColumns(columns, count);
    }

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<M> findListByColumns(Columns columns, String orderBy, Integer count) {
        return DAO.findListByColumns(columns, orderBy, count);
    }


    public List<M> findListByIds(Object... ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }

        List<M> list = new ArrayList<>();
        for (Object id : ids) {
            if (id.getClass() == int[].class) {
                findListByIds(list, (int[]) id);
            } else if (id.getClass() == long[].class) {
                findListByIds(list, (long[]) id);
            } else if (id.getClass() == short[].class) {
                findListByIds(list, (short[]) id);
            } else {
                M model = findById(id);
                if (model != null) {
                    list.add(model);
                }
            }
        }
        return list;
    }

    private void findListByIds(List<M> list, int[] ids) {
        for (int id : ids) {
            M model = findById(id);
            if (model != null) {
                list.add(model);
            }
        }
    }

    private void findListByIds(List<M> list, long[] ids) {
        for (long id : ids) {
            M model = findById(id);
            if (model != null) {
                list.add(model);
            }
        }
    }


    private void findListByIds(List<M> list, short[] ids) {
        for (short id : ids) {
            M model = findById(id);
            if (model != null) {
                list.add(model);
            }
        }
    }


    /**
     * 根据提交查询数据量
     *
     * @param columns
     * @return
     */
    public long findCountByColumns(Columns columns) {
        return DAO.findCountByColumns(columns);
    }


    /**
     * 根据ID 删除model
     *
     * @param id
     * @return
     */
    public boolean deleteById(Object id) {
        boolean result = DAO.deleteById(id);
        if (result) {
            shouldUpdateCache(ACTION_DEL, null, id);
        }
        return result;
    }


    /**
     * 删除
     *
     * @param model
     * @return
     */
    public boolean delete(M model) {
        boolean result = model.delete();
        if (result) {
            shouldUpdateCache(ACTION_DEL, model, model._getIdValue());
        }
        return result;
    }


    /**
     * 根据 多个 id 批量删除
     *
     * @param ids
     * @return
     */
    public boolean batchDeleteByIds(Object... ids) {
        boolean result = DAO.batchDeleteByIds(ids);
        if (result) {
            for (Object id : ids) {
                shouldUpdateCache(ACTION_DEL, null, id);
            }
        }
        return result;
    }


    /**
     * 保存到数据库
     *
     * @param model
     * @return id if success
     */
    public Object save(M model) {
        boolean result = model.save();
        if (result) {
            shouldUpdateCache(ACTION_ADD, model, model._getIdValue());
            return model._getIdValue();
        }
        return null;
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
        boolean result = model.update();
        if (result) {
            shouldUpdateCache(ACTION_UPDATE, model, model._getIdValue());
        }
        return result;
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
     * 同步 model 数据到数据库
     *
     * @param columns
     * @param syncModels
     * @param compareAttrGetters
     */
    public void syncModels(Columns columns, List<M> syncModels, ObjectFunc<M>... compareAttrGetters) {
        if (columns == null) {
            throw new NullPointerException("columns must not be null");
        }

        if (syncModels == null || syncModels.isEmpty()) {
            DAO.deleteByColumns(columns);
            return;
        }

        List<M> existModels = findListByColumns(columns);
        if (existModels == null || existModels.isEmpty()) {
            Db.batchSave(syncModels, syncModels.size());
            return;
        }


        for (M existModel : existModels) {
            if (!ObjectUtil.isContainsObject(syncModels, existModel, compareAttrGetters)) {
                existModel.delete();
            }
        }


        for (M syncModel : syncModels) {
            M existModel = ObjectUtil.getContainsObject(existModels, syncModel, compareAttrGetters);
            if (existModel == null) {
                syncModel.save();
            } else {
                existModel._setAttrs(syncModel).update();
            }
        }
    }


    /**
     * 复写 JbootServiceJoinerImpl 的方法
     *
     * @param columnValue
     * @return
     */
    @Override
    protected JbootModel joinByValue(Object columnValue, JbootModel sourceModel) {
        return findById(columnValue);
    }


    /**
     * 用于给子类复写，用于刷新缓存
     *
     * @param action
     * @param model
     * @param id
     */
    public void shouldUpdateCache(int action, Model model, Object id) {
    }


    @Override
    protected <M extends JbootModel> List<M> joinManyByValue(String columnName, Object value, M sourceModel) {
        return (List<M>) findListByColumns(Columns.create(columnName, value));
    }
}
