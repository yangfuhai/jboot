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
package io.jboot.service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.ArrayUtils;

import java.io.Serializable;
import java.util.List;

/**
 * JbootService 类
 */
public abstract class JbootService implements Serializable {


    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public abstract JbootModel findById(Object id);


    /**
     * 根据ID 删除model
     *
     * @param id
     * @return
     */
    public abstract boolean deleteById(Object id);

    /**
     * 删除
     *
     * @param model
     * @return
     */
    public boolean delete(JbootModel<?> model) {
        return model.delete();
    }


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(JbootModel<?> model) {
        return model.save();
    }

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(JbootModel<?> model) {
        return model.saveOrUpdate();
    }

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(JbootModel<?> model) {
        return model.update();
    }


    public void join(Page<? extends Model> page, String joinOnField) {
        join(page.getList(), joinOnField);
    }

    public void join(Page<? extends Model> page, String joinOnField, String[] attrs) {
        join(page.getList(), joinOnField, attrs);
    }


    public void join(List<? extends Model> models, String joinOnField) {
        if (ArrayUtils.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField);
            }
        }
    }


    public void join(List<? extends Model> models, String joinOnField, String[] attrs) {
        if (ArrayUtils.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField, attrs);
            }
        }
    }


    public void join(Page<? extends Model> page, String joinOnField, String joinName) {
        join(page.getList(), joinOnField, joinName);
    }


    public void join(List<? extends Model> models, String joinOnField, String joinName) {
        if (ArrayUtils.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField, joinName);
            }
        }
    }

    public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs) {
        join(page.getList(), joinOnField, joinName, attrs);
    }


    public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs) {
        if (ArrayUtils.isNotEmpty(models)) {
            for (Model m : models) {
                join(m, joinOnField, joinName, attrs);
            }
        }
    }

    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model       要添加到的model
     * @param joinOnField model对于的关联字段
     */
    public void join(Model model, String joinOnField) {
        if (model == null)
            return;
        String id = model.getStr(joinOnField);
        if (id == null) {
            return;
        }
        Model m = findById(id);
        if (m != null) {
            model.put(StrKit.firstCharToLowerCase(m.getClass().getSimpleName()), m);
        }
    }

    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param attrs
     */
    public void join(Model model, String joinOnField, String[] attrs) {
        if (model == null)
            return;
        String id = model.getStr(joinOnField);
        if (id == null) {
            return;
        }
        JbootModel m = findById(id);
        if (m != null) {
            m = m.copy();
            m.keep(attrs);
            model.put(StrKit.firstCharToLowerCase(m.getClass().getSimpleName()), m);
        }
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param joinName
     */
    public void join(Model model, String joinOnField, String joinName) {
        if (model == null)
            return;
        String id = model.getStr(joinOnField);
        if (id == null) {
            return;
        }
        Model m = findById(id);
        if (m != null) {
            model.put(joinName, m);
        }
    }


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param joinName
     * @param attrs
     */
    public void join(Model model, String joinOnField, String joinName, String[] attrs) {
        if (model == null)
            return;
        String id = model.getStr(joinOnField);
        if (id == null) {
            return;
        }
        JbootModel m = findById(id);
        if (m != null) {
            m = m.copy();
            m.keep(attrs);
            model.put(joinName, m);
        }

    }


    public void keep(Model model, String... attrs) {
        if (model == null) {
            return;
        }

        model.keep(attrs);
    }

    public void keep(List<? extends Model> models, String... attrs) {
        if (ArrayUtils.isNotEmpty(models)) {
            for (Model m : models) {
                keep(m, attrs);
            }
        }
    }
}
