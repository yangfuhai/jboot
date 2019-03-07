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
package io.jboot.db.model;


import java.io.Serializable;

public class Column implements Serializable {
    public static final String LOGIC_LIKE = " LIKE ";
    public static final String LOGIC_GT = " > ";
    public static final String LOGIC_GE = " >= ";
    public static final String LOGIC_LT = " < ";
    public static final String LOGIC_LE = " <= ";
    public static final String LOGIC_EQUALS = " = ";
    public static final String LOGIC_NOT_EQUALS = " != ";

    public static final String LOGIC_IS_NULL = " IS NULL ";
    public static final String LOGIC_IS_NOT_NULL = " IS NOT NULL ";

    public static final String LOGIC_IN = " IN ";
    public static final String LOGIC_BETWEEN = " BETWEEN ";


    private String name;
    private Object value;
    private String logic = LOGIC_EQUALS;


    public static Column create(String name) {
        Column column = new Column();
        column.setName(name);
        return column;
    }

    public static Column create(String name, Object value) {
        Column column = new Column();
        column.setName(name);
        column.setValue(value);
        return column;
    }

    public static Column create(String name, Object value, String logic) {
        Column column = new Column();
        column.setName(name);
        column.setValue(value);
        column.setLogic(logic);
        return column;
    }

    public Column logic(String logic) {
        this.setLogic(logic);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public boolean isMustNeedValue() {
        return !LOGIC_IS_NULL.equals(getLogic())
                && !LOGIC_IS_NOT_NULL.equals(getLogic());
    }

}
