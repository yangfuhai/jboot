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

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ObjectFunc;

import java.util.ArrayList;
import java.util.List;


public abstract class JbootServiceJoinerImpl implements JbootServiceJoiner {


    @Override
    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName) {
        join(page.getList(), columnName);
        return page;
    }

    @Override
    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName, String[] attrs) {
        join(page.getList(), columnName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName, String joinName) {
        join(page.getList(), columnName, joinName);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName, String joinName, String[] attrs) {
        join(page.getList(), columnName, joinName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> List<M> join(List<M> models, String columnName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (JbootModel m : models) {
                join(m, columnName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> join(List<M> models, String columnName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (JbootModel m : models) {
                join(m, columnName, attrs);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> join(List<M> models, String columnName, String joinName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (JbootModel m : models) {
                join(m, columnName, joinName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> join(List<M> models, String columnName, String joinName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (JbootModel m : models) {
                join(m, columnName, joinName, attrs);
            }
        }
        return models;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model      要添加到的model
     * @param columnName model对于的关联字段
     */
    @Override
    public <M extends JbootModel> M join(M model, String columnName) {
        if (model == null) {
            return null;
        }
        Object columnValue = model.get(columnName);
        if (columnValue == null) {
            return model;
        }
        JbootModel m = joinByColumnValue(columnValue, model);
        if (m != null) {
            model.put(StrKit.firstCharToLowerCase(m.getClass().getSimpleName()), m);
        }
        return model;
    }

    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param columnName
     * @param attrs
     */
    @Override
    public <M extends JbootModel> M join(M model, String columnName, String[] attrs) {
        if (model == null) {
            return null;
        }
        Object columnValue = model.get(columnName);
        if (columnValue == null) {
            return model;
        }
        JbootModel m = joinByColumnValue(columnValue, model);
        if (m != null) {
            m = m.copy();
            m.keep(attrs);
            model.put(StrKit.firstCharToLowerCase(m.getClass().getSimpleName()), m);
        }
        return model;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param columnName
     * @param joinName
     */
    @Override
    public <M extends JbootModel> M join(M model, String columnName, String joinName) {
        if (model == null) {
            return null;
        }
        Object columnValue = model.get(columnName);
        if (columnValue == null) {
            return model;
        }
        JbootModel m = joinByColumnValue(columnValue, model);
        if (m != null) {
            model.put(joinName, m);
        }
        return model;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param columnName
     * @param joinName
     * @param attrs
     */
    @Override
    public <M extends JbootModel> M join(M model, String columnName, String joinName, String[] attrs) {
        if (model == null) {
            return null;
        }
        Object columnValue = model.get(columnName);
        if (columnValue == null) {
            return model;
        }
        JbootModel m = joinByColumnValue(columnValue, model);
        if (m != null) {
            m = m.copy();
            m.keep(attrs);
            model.put(joinName, m);
        }
        return model;
    }


    /**
     * 可以让子类去复写 joinByColumnValue ，比如默认只 join 部分字段等，或者不是根据主键进行查询等
     *
     * @param columnValue
     * @return
     */
    protected abstract JbootModel joinByColumnValue(Object columnValue, JbootModel sourceModel);


/////////////////joinMany start/////////////////////////////


    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName) {
        joinMany(page.getList(), targetColumnName);
        return page;
    }

    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName, String[] attrs) {
        joinMany(page.getList(), targetColumnName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName, String joinName) {
        joinMany(page.getList(), targetColumnName, joinName);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName, String joinName, String[] attrs) {
        joinMany(page.getList(), targetColumnName, joinName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, targetColumnName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, targetColumnName, attrs);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName, String joinName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, targetColumnName, joinName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName, String joinName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, targetColumnName, joinName, attrs);
            }
        }
        return models;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model            要添加到的model
     * @param targetColumnName model对于的关联字段
     */
    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName) {
        if (model == null) {
            return null;
        }
        Object columnValue = model._getIdValue();
        if (columnValue == null) {
            return model;
        }
        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            JbootModel joinModel = list.get(0);
            model.put(StrKit.firstCharToLowerCase(joinModel.getClass().getSimpleName() + "List"), list);
        }
        return model;
    }

    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param targetColumnName
     * @param attrs
     */
    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String[] attrs) {
        if (model == null) {
            return null;
        }
        Object columnValue = model._getIdValue();
        if (columnValue == null) {
            return model;
        }

        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            JbootModel joinModel = list.get(0);
            model.put(StrKit.firstCharToLowerCase(joinModel.getClass().getSimpleName() + "List"), keepModelListAttrs(list, attrs));
        }

        return model;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param targetColumnName
     * @param joinName
     */
    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String joinName) {
        if (model == null) {
            return null;
        }
        Object columnValue = model._getIdValue();
        if (columnValue == null) {
            return model;
        }

        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            model.put(joinName, list);
        }

        return model;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param targetColumnName
     * @param joinName
     * @param attrs
     */
    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String joinName, String[] attrs) {
        if (model == null) {
            return null;
        }
        Object columnValue = model._getIdValue();
        if (columnValue == null) {
            return model;
        }

        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            model.put(joinName, keepModelListAttrs(list, attrs));
        }

        return model;
    }


    protected <M extends JbootModel> List<M> keepModelListAttrs(List<M> list, String[] attrs) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<M> retList = new ArrayList<>(list.size());
        for (M model : list) {
            retList.add((M) model.copy().keep(attrs));
        }
        return retList;
    }


    protected abstract <M extends JbootModel> List<M> joinManyByColumnValue(String targetColumnName, Object columnValue, M sourceModel);


    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName) {
        joinMany(page.getList(), modelValueGetter, targetColumnName);
        return page;
    }

    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName, String[] attrs) {
        joinMany(page.getList(), modelValueGetter, targetColumnName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName) {
        joinMany(page.getList(), modelValueGetter, targetColumnName, joinName);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName, String[] attrs) {
        joinMany(page.getList(), modelValueGetter, targetColumnName, joinName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, modelValueGetter, targetColumnName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, modelValueGetter, targetColumnName, attrs);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, modelValueGetter, targetColumnName, joinName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinMany(m, modelValueGetter, targetColumnName, joinName, attrs);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName) {
        if (model == null) {
            return null;
        }
        Object columnValue = modelValueGetter.get(model);
        if (columnValue == null) {
            return model;
        }
        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            JbootModel joinModel = list.get(0);
            model.put(StrKit.firstCharToLowerCase(joinModel.getClass().getSimpleName() + "List"), list);
        }
        return model;
    }

    @Override
    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String[] attrs) {
        if (model == null) {
            return null;
        }
        Object columnValue = modelValueGetter.get(model);
        if (columnValue == null) {
            return model;
        }

        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            JbootModel joinModel = list.get(0);
            model.put(StrKit.firstCharToLowerCase(joinModel.getClass().getSimpleName() + "List"), keepModelListAttrs(list, attrs));
        }

        return model;
    }


    @Override
    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName) {
        if (model == null) {
            return null;
        }
        Object columnValue = modelValueGetter.get(model);
        if (columnValue == null) {
            return model;
        }

        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            model.put(joinName, list);
        }

        return model;
    }


    @Override
    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName, String[] attrs) {
        if (model == null) {
            return null;
        }
        Object columnValue = modelValueGetter.get(model);
        if (columnValue == null) {
            return model;
        }

        List<M> list = joinManyByColumnValue(targetColumnName, columnValue, model);
        if (list != null && !list.isEmpty()) {
            model.put(joinName, keepModelListAttrs(list, attrs));
        }

        return model;
    }


/////////////////joinMany end/////////////////////////////

}
