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
package io.jboot.utils;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.JbootModel;
import io.jboot.exception.JbootException;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/21
 */
public class ModelUtil {

    /**
     * copy model list
     *
     * @param modelList
     * @param <M>
     * @return
     */
    public static <M extends JbootModel> List<M> copy(List<M> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            return modelList;
        }

        List<M> list = modelList instanceof ArrayList
                ? new ArrayList<>(modelList.size())
                : newInstance(modelList.getClass());

        for (M m : modelList) {
            list.add(copy(m));
        }

        return list;
    }


    /**
     * copy model set
     *
     * @param modelSet
     * @param <M>
     * @return
     */
    public static <M extends JbootModel> Set<M> copy(Set<M> modelSet) {
        if (modelSet == null || modelSet.isEmpty()) {
            return modelSet;
        }

        Set<M> set = modelSet instanceof HashSet
                ? new HashSet<>(modelSet.size())
                : newInstance(modelSet.getClass());

        for (M m : modelSet) {
            set.add(copy(m));
        }

        return set;
    }


    /**
     * copy model array
     * @param models
     * @param <M>
     * @return
     */
    public static <M extends JbootModel> M[] copy(M[] models) {
        if (models == null || models.length == 0) {
            return models;
        }

        M[] array = (M[]) Array.newInstance(models.getClass().getComponentType(), models.length);
        int i = 0;
        for (M m : models) {
            array[i++] = copy(m);
        }
        return array;
    }


    /**
     * copy model page
     *
     * @param modelPage
     * @param <M>
     * @return
     */
    public static <M extends JbootModel> Page<M> copy(Page<M> modelPage) {
        if (modelPage == null) {
            return null;
        }

        List<M> modelList = modelPage.getList();
        if (modelList == null || modelList.isEmpty()) {
            return modelPage;
        }

        modelPage.setList(copy(modelList));
        return modelPage;
    }


    /**
     * copy model
     *
     * @param model
     * @param <M>
     * @return
     */
    public static <M extends JbootModel> M copy(M model) {
        return model == null ? null : (M) model.copy();
    }



    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new JbootException("can not newInstance class:" + clazz + "\n" + e.toString(), e);
        }
    }




    /**
     * 判断 某个 Model list 里是否包含了某个 Model
     *
     * @param models  model list
     * @param compareModel   是否被包 list 含的对比 model
     * @param compareByAttrs 需要对比的字段
     * @param <T>
     * @return true 包含，false 不包含
     */
    public static <T extends Model> boolean isContainsModel(List<T> models, T compareModel, ObjectFunc<T>... compareByAttrs) {
        if (models == null || models.isEmpty() || compareModel == null) {
            return false;
        }

        for (T item : models) {
            boolean equals = true;
            for (ObjectFunc getter : compareByAttrs) {
                if (!Objects.equals(getter.get(item), getter.get(compareModel))) {
                    equals = false;
                    break;
                }
            }
            if (equals) {
                return true;
            }
        }

        return false;
    }


    /**
     * 获取 某个 Model list 里包含的 Model
     *
     * @param models  model list
     * @param compareModel   是否被包 list 含的对比 model
     * @param compareByAttrs 对比字段
     * @param <T>
     * @return 返回 model list 中对比成功的 model
     */
    public static <T extends Model> T getContainsModel(List<T> models, T compareModel, ObjectFunc<T>... compareByAttrs) {
        if (models == null || models.isEmpty() || compareModel == null) {
            return null;
        }

        for (T item : models) {
            boolean equals = true;
            for (ObjectFunc getter : compareByAttrs) {
                if (!Objects.equals(getter.get(item), getter.get(compareModel))) {
                    equals = false;
                    break;
                }
            }
            if (equals) {
                return item;
            }
        }

        return null;
    }

}
