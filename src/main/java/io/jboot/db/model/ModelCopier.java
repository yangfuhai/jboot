/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.db.model;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.ClassNewer;

import java.util.ArrayList;
import java.util.List;

/**
 * VoModel的拷贝器
 */
public class ModelCopier {

    public static final String MODEL_FROM_COPIER = "__from_copier__";


    public static <M extends JbootVoModel> Page<M> convertToVo(Page<? extends JbootModel> page, Class<M> clazz) {
        if (page == null) {
            return null;
        }

        List<M> list = new ArrayList<>();
        for (JbootModel model : page.getList()) {
            list.add(convertToVo(model, clazz));
        }

        return new Page<M>(list, page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow());
    }


    public static <M extends JbootVoModel> List<M> convertToVo(List<? extends JbootModel> modelList, Class<M> clazz) {
        if (modelList == null) {
            return null;
        }

        List<M> datas = new ArrayList<>();
        for (JbootModel model : modelList) {
            datas.add(convertToVo(model, clazz));
        }

        return datas;
    }


    public static <M extends JbootVoModel> M convertToVo(JbootModel model, Class<M> clazz) {
        return (M) copyToVo(model, ClassNewer.newInstance(clazz));
    }


    public static JbootVoModel copyToVo(JbootModel model, JbootVoModel toObject) {
        if (model == null) return toObject;
        toObject.putAll(model._getAttrsAsMap());
        toObject.remove(MODEL_FROM_COPIER);
        return toObject;
    }


    ////////一下是JbootModel 转化到 JbootVoModel////////
    public static <M extends Model> Page<M> convertToModel(Page<? extends JbootVoModel> page, Class<M> clazz) {
        if (page == null) {
            return null;
        }

        List<M> list = new ArrayList<>();
        for (JbootVoModel voModel : page.getList()) {
            list.add(convertToModel(voModel, clazz));
        }

        return new Page<M>(list, page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow());
    }


    public static <M extends JbootModel> List<M> convertToModel(List<? extends JbootVoModel> modelList, Class<M> clazz) {
        if (modelList == null) {
            return null;
        }

        List<M> datas = new ArrayList<>();
        for (JbootVoModel voModel : modelList) {
            datas.add(convertToModel(voModel, clazz));
        }

        return datas;
    }


    public static <M extends Model> M convertToModel(JbootVoModel voModel, Class<M> clazz) {
        return (M) copyToModel(voModel, ClassNewer.newInstance(clazz));
    }


    public static Model copyToModel(JbootVoModel voModel, Model toModel) {
        if (voModel == null) return toModel;
        toModel.put(voModel);
        toModel.put(MODEL_FROM_COPIER, true);
        return toModel;
    }

}
