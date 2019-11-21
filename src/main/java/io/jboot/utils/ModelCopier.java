/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.utils;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.JbootModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/21
 */
public class ModelCopier {

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

        return modelList.stream()
                .map(ModelCopier::copy)
                .collect(Collectors.toList());
    }

    /**
     * copy model set
     *
     * @param modelList
     * @param <M>
     * @return
     */
    public static <M extends JbootModel> Set<M> copy(Set<M> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            return modelList;
        }

        return modelList.stream()
                .map(ModelCopier::copy)
                .collect(Collectors.toSet());
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
}
