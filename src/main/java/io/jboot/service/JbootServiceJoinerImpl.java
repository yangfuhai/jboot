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

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.JbootModel;
import io.jboot.kits.ArrayKits;

import java.util.List;


public abstract class JbootServiceJoinerImpl implements JbootServiceJoiner {


    @Override
    public <M extends Model> Page<M> join(Page<M> page, String joinOnField) {
        join(page.getList(), joinOnField);
        return page;
    }

    @Override
    public <M extends Model> Page<M> join(Page<M> page, String joinOnField, String[] attrs) {
        join(page.getList(), joinOnField, attrs);
        return page;
    }


    @Override
    public <M extends Model> Page<M> join(Page<M> page, String joinOnField, String joinName) {
        join(page.getList(), joinOnField, joinName);
        return page;
    }


    @Override
    public <M extends Model> Page<M> join(Page<M> page, String joinOnField, String joinName, String[] attrs) {
        join(page.getList(), joinOnField, joinName, attrs);
        return page;
    }


    @Override
    public <M extends Model> List<M> join(List<M> models, String joinOnField) {
        if (ArrayKits.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField);
            }
        }
        return models;
    }


    @Override
    public <M extends Model> List<M> join(List<M> models, String joinOnField, String[] attrs) {
        if (ArrayKits.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField, attrs);
            }
        }
        return models;
    }


    @Override
    public <M extends Model> List<M> join(List<M> models, String joinOnField, String joinName) {
        if (ArrayKits.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField, joinName);
            }
        }
        return models;
    }


    @Override
    public <M extends Model> List<M> join(List<M> models, String joinOnField, String joinName, String[] attrs) {
        if (ArrayKits.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField, joinName, attrs);
            }
        }
        return models;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model       要添加到的model
     * @param joinOnField model对于的关联字段
     */
    @Override
    public <M extends Model> M join(M model, String joinOnField) {
        if (model == null)
            return model;
        Object id = model.get(joinOnField);
        if (id == null) {
            return model;
        }
        Model m = joinById(id);
        if (m != null) {
            model.put(StrKit.firstCharToLowerCase(m.getClass().getSimpleName()), m);
        }
        return model;
    }

    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param attrs
     */
    @Override
    public <M extends Model> M join(M model, String joinOnField, String[] attrs) {
        if (model == null)
            return model;
        Object id = model.get(joinOnField);
        if (id == null) {
            return model;
        }
        JbootModel m = joinById(id);
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
     * @param joinOnField
     * @param joinName
     */
    @Override
    public <M extends Model> M join(M model, String joinOnField, String joinName) {
        if (model == null)
            return model;
        Object id = model.get(joinOnField);
        if (id == null) {
            return model;
        }
        Model m = joinById(id);
        if (m != null) {
            model.put(joinName, m);
        }
        return model;
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param joinName
     * @param attrs
     */
    @Override
    public <M extends Model> M join(M model, String joinOnField, String joinName, String[] attrs) {
        if (model == null)
            return model;
        Object id = model.get(joinOnField);
        if (id == null) {
            return model;
        }
        JbootModel m = joinById(id);
        if (m != null) {
            m = m.copy();
            m.keep(attrs);
            model.put(joinName, m);
        }
        return model;
    }


    /**
     * 可以让子类去复写joinById ，比如默认只 join 部分字段等
     *
     * @param id
     * @return
     */
    protected abstract JbootModel joinById(Object id);


}
