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

import java.util.ArrayList;
import java.util.List;

/**
 * Column 的工具类，用于方便组装sql
 */
public class Columns {

    private List<Column> cols = new ArrayList<>();


    public static Columns create() {
        return new Columns();
    }

    public static Columns create(String name, Object value) {
        return create().eq(name, value);
    }

    /**
     * equals
     *
     * @param name
     * @param value
     * @return
     */
    public Columns eq(String name, Object value) {
        cols.add(Column.create(name, value));
        return this;
    }

    /**
     * not equals !=
     *
     * @param name
     * @param value
     * @return
     */
    public Columns ne(String name, Object value) {
        cols.add(Column.create(name, value, Column.LOGIC_NOT_EQUALS));
        return this;
    }


    /**
     * like
     *
     * @param name
     * @param value
     * @return
     */

    public Columns like(String name, Object value) {
        cols.add(Column.create(name, value, Column.LOGIC_LIKE));
        return this;
    }

    /**
     * 大于 great than
     *
     * @param name
     * @param value
     * @return
     */
    public Columns gt(String name, Object value) {
        cols.add(Column.create(name, value, Column.LOGIC_GT));
        return this;
    }

    /**
     * 大于等于 great or equal
     *
     * @param name
     * @param value
     * @return
     */
    public Columns ge(String name, Object value) {
        cols.add(Column.create(name, value, Column.LOGIC_GE));
        return this;
    }

    /**
     * 小于 less than
     *
     * @param name
     * @param value
     * @return
     */
    public Columns lt(String name, Object value) {
        cols.add(Column.create(name, value, Column.LOGIC_LT));
        return this;
    }

    /**
     * 小于等于 less or equal
     *
     * @param name
     * @param value
     * @return
     */
    public Columns le(String name, Object value) {
        cols.add(Column.create(name, value, Column.LOGIC_LE));
        return this;
    }


    public List<Column> getCols() {
        return cols;
    }

    public void setCols(List<Column> cols) {
        this.cols = cols;
    }
}
