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
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.JbootDb;
import io.jboot.db.model.Columns;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ObjectFunc;
import io.jboot.utils.StrUtil;

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
        return join(model, columnName, null, null);
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
        return join(model, columnName, null, attrs);
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
        return join(model, columnName, joinName, null);
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
        Object value = model.get(columnName);
        if (value == null) {
            return model;
        }
        JbootModel m = joinByValue(value, model);
        if (m != null) {
            joinName = StrUtil.isNotBlank(joinName) ? joinName : StrKit.firstCharToLowerCase(m.getClass().getSimpleName());
            model.put(joinName, ArrayUtil.isNotEmpty(attrs) ? m.copy().keep(attrs) : m);
        }
        return model;
    }


    /**
     * 可以让子类去复写 joinByColumnValue ，比如默认只 join 部分字段等，或者不是根据主键进行查询等
     * 一般情况下，传入的 columnValue 是主键的值，但是也有可能不是，要看场景，如果不是的情况下可以通过 sourceModel 来进行判断
     *
     * @param columnValue
     * @return
     */
    protected abstract JbootModel joinByValue(Object columnValue, JbootModel sourceModel);


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


    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName) {
        return joinMany(model, null, targetColumnName, null, null);
    }

    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String[] attrs) {
        return joinMany(model, null, targetColumnName, null, attrs);
    }

    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String joinName) {
        return joinMany(model, null, targetColumnName, joinName, null);
    }


    @Override
    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String joinName, String[] attrs) {
        return joinMany(model, null, targetColumnName, joinName, attrs);
    }


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
        return joinMany(model, modelValueGetter, targetColumnName, null, null);
    }

    @Override
    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String[] attrs) {
        return joinMany(model, modelValueGetter, targetColumnName, null, attrs);
    }


    @Override
    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName) {
        return joinMany(model, modelValueGetter, targetColumnName, joinName, null);
    }


    @Override
    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName, String[] attrs) {
        if (model == null) {
            return null;
        }
        Object value = modelValueGetter != null ? modelValueGetter.get(model) : model._getIdValue();
        if (value == null) {
            return model;
        }

        List<M> list = joinManyByValue(targetColumnName, value, model);
        if (list != null && !list.isEmpty()) {
            joinName = StrUtil.isNotBlank(joinName) ? joinName : StrKit.firstCharToLowerCase(list.get(0).getClass().getSimpleName()) + "List";
            model.put(joinName, ArrayUtil.isNotEmpty(attrs) ? keepModelListAttrs(list, attrs) : list);
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


    protected abstract <M extends JbootModel> List<M> joinManyByValue(String columnName, Object value, M sourceModel);


/////////////////joinMany end/////////////////////////////


/////////////////joinManyByTable start/////////////////////////////

    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String tableName, String columnName, String targetColumnName) {
        joinManyByTable(page.getList(), tableName, columnName, targetColumnName);
        return page;
    }

    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String tableName, String columnName, String targetColumnName, String[] attrs) {
        joinManyByTable(page.getList(), tableName, columnName, targetColumnName, attrs);
        return page;
    }

    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String tableName, String columnName, String targetColumnName, String joinName) {
        joinManyByTable(page.getList(), tableName, columnName, targetColumnName, joinName);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String tableName, String columnName, String targetColumnName, String joinName, String[] attrs) {
        joinManyByTable(page.getList(), tableName, columnName, targetColumnName, joinName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String tableName, String columnName, String targetColumnName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, tableName, columnName, targetColumnName);
            }
        }
        return models;
    }

    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String tableName, String columnName, String targetColumnName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, tableName, columnName, targetColumnName, attrs);
            }
        }
        return models;
    }

    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String tableName, String columnName, String targetColumnName, String joinName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, tableName, columnName, targetColumnName, joinName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String tableName, String columnName, String targetColumnName, String joinName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, tableName, columnName, targetColumnName, joinName, attrs);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> M joinManyByTable(M model, String tableName, String columnName, String targetColumnName) {
        return joinManyByTable(model, tableName, columnName, targetColumnName, null, null);
    }

    @Override
    public <M extends JbootModel> M joinManyByTable(M model, String tableName, String columnName, String targetColumnName, String[] attrs) {
        return joinManyByTable(model, tableName, columnName, targetColumnName, null, attrs);
    }

    @Override
    public <M extends JbootModel> M joinManyByTable(M model, String tableName, String columnName, String targetColumnName, String joinName) {
        return joinManyByTable(model, tableName, columnName, targetColumnName, joinName, null);
    }

    @Override
    public <M extends JbootModel> M joinManyByTable(M model, String tableName, String columnName, String targetColumnName, String joinName, String[] attrs) {
        return joinManyByTable(model, null, tableName, columnName, targetColumnName, joinName, attrs);
    }


    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName) {
        joinManyByTable(page.getList(), modelValueGetter, tableName, columnName, targetColumnName);
        return page;
    }

    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String[] attrs) {
        joinManyByTable(page.getList(), modelValueGetter, tableName, columnName, targetColumnName, attrs);
        return page;
    }

    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String joinName) {
        joinManyByTable(page.getList(), modelValueGetter, tableName, columnName, targetColumnName, joinName);
        return page;
    }


    @Override
    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String joinName, String[] attrs) {
        joinManyByTable(page.getList(), modelValueGetter, tableName, columnName, targetColumnName, joinName, attrs);
        return page;
    }


    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, modelValueGetter, tableName, columnName, targetColumnName);
            }
        }
        return models;
    }

    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, modelValueGetter, tableName, columnName, targetColumnName, attrs);
            }
        }
        return models;
    }

    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String joinName) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, modelValueGetter, tableName, columnName, targetColumnName, joinName);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String joinName, String[] attrs) {
        if (ArrayUtil.isNotEmpty(models)) {
            for (M m : models) {
                joinManyByTable(m, modelValueGetter, tableName, columnName, targetColumnName, joinName, attrs);
            }
        }
        return models;
    }


    @Override
    public <M extends JbootModel> M joinManyByTable(M model, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName) {
        return joinManyByTable(model, modelValueGetter, tableName, columnName, targetColumnName, null, null);
    }

    @Override
    public <M extends JbootModel> M joinManyByTable(M model, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String[] attrs) {
        return joinManyByTable(model, modelValueGetter, tableName, columnName, targetColumnName, null, attrs);
    }

    @Override
    public <M extends JbootModel> M joinManyByTable(M model, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String joinName) {
        return joinManyByTable(model, modelValueGetter, tableName, columnName, targetColumnName, joinName, null);
    }

    @Override
    public <M extends JbootModel> M joinManyByTable(M model, ObjectFunc<M> modelValueGetter, String tableName, String columnName, String targetColumnName, String joinName, String[] attrs) {
        if (model == null) {
            return null;
        }

        Object columnValue = modelValueGetter != null ? modelValueGetter.get(model) : model._getIdValue();
        if (columnValue == null) {
            return model;
        }

        List<Record> middleTableRecords = findMiddleTableRecords(tableName, columnName, columnValue);
        if (middleTableRecords == null || middleTableRecords.isEmpty()) {
            return model;
        }

        List<M> list = new ArrayList();
        for (Record record : middleTableRecords) {
            Object targetTableValue = record.get(targetColumnName);
            if (targetTableValue != null) {
                M data = (M) joinByValue(targetTableValue, model);
                if (data != null) {
                    list.add(data);
                }
            }
        }

        if (!list.isEmpty()) {
            joinName = StrUtil.isNotBlank(joinName) ? joinName : StrKit.firstCharToLowerCase(list.get(0).getClass().getSimpleName()) + "List";
            model.put(joinName, ArrayUtil.isNotEmpty(attrs) ? keepModelListAttrs(list, attrs) : list);
        }

        return model;
    }


    /**
     * 查询中间表数据，方便子类复写，比如：通过缓存获取等
     * @param tableName
     * @param columnName
     * @param columnValue
     * @return
     */
    protected List<Record> findMiddleTableRecords(String tableName, String columnName, Object columnValue) {
        return JbootDb.find(tableName, Columns.create(columnName, columnValue));
    }


/////////////////joinManyByTable end/////////////////////////////

}
