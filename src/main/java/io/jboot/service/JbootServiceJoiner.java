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

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.ObjectFunc;

import java.util.List;

/**
 * JbootService 需要 Join 功能的话，需要实现 JbootServiceJoiner 接口
 */
public interface JbootServiceJoiner {


    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName);

    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName, String[] attrs);

    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName, String joinName);

    public <M extends JbootModel> Page<M> join(Page<M> page, String columnName, String joinName, String[] attrs);


    public <M extends JbootModel> List<M> join(List<M> models, String columnName);

    public <M extends JbootModel> List<M> join(List<M> models, String columnName, String[] attrs);

    public <M extends JbootModel> List<M> join(List<M> models, String columnName, String joinName);

    public <M extends JbootModel> List<M> join(List<M> models, String columnName, String joinName, String[] attrs);


    public <M extends JbootModel> M join(M model, String columnName);

    public <M extends JbootModel> M join(M model, String columnName, String[] attrs);

    public <M extends JbootModel> M join(M model, String columnName, String joinName);

    public <M extends JbootModel> M join(M model, String columnName, String joinName, String[] attrs);


    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName);

    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName, String[] attrs);

    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName, String joinName);

    public <M extends JbootModel> Page<M> joinMany(Page<M> page, String targetColumnName, String joinName, String[] attrs);


    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName);

    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName, String[] attrs);

    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName, String joinName);

    public <M extends JbootModel> List<M> joinMany(List<M> models, String targetColumnName, String joinName, String[] attrs);


    public <M extends JbootModel> M joinMany(M model, String targetColumnName);

    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String[] attrs);

    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String joinName);

    public <M extends JbootModel> M joinMany(M model, String targetColumnName, String joinName, String[] attrs);



    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName);

    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName, String[] attrs);

    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName);

    public <M extends JbootModel> Page<M> joinMany(Page<M> page, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName, String[] attrs);


    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName);

    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName, String[] attrs);

    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName);

    public <M extends JbootModel> List<M> joinMany(List<M> models, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName, String[] attrs);


    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName);

    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String[] attrs);

    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName);

    public <M extends JbootModel> M joinMany(M model, ObjectFunc<M> modelValueGetter, String targetColumnName, String joinName, String[] attrs);


//
//
//
//
//
//
//    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String tableName,);
//
//    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String targetColumnName, String[] attrs);
//
//    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String targetColumnName, String joinName);
//
//    public <M extends JbootModel> Page<M> joinManyByTable(Page<M> page, String targetColumnName, String joinName, String[] attrs);
//
//
//    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String targetColumnName);
//
//    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String targetColumnName, String[] attrs);
//
//    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String targetColumnName, String joinName);
//
//    public <M extends JbootModel> List<M> joinManyByTable(List<M> models, String targetColumnName, String joinName, String[] attrs);
//
//
//    public <M extends JbootModel> M joinManyByTable(M model, String targetColumnName);
//
//    public <M extends JbootModel> M joinManyByTable(M model, String targetColumnName, String[] attrs);
//
//    public <M extends JbootModel> M joinManyByTable(M model, String targetColumnName, String joinName);
//
//    public <M extends JbootModel> M joinManyByTable(M model, String targetColumnName, String joinName, String[] attrs);


}
